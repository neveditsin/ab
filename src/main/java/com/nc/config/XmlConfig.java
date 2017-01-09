package com.nc.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.ConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.nc.events.Event;
import com.nc.host.GenericHost;
import com.nc.host.Host;
import com.nc.inform.ConfigurableEmailInformer;
import com.nc.inform.ConsoleInformer;
import com.nc.inform.EmailInformers;
import com.nc.inform.InformUnit;
import com.nc.inform.Informer;
import com.nc.scenario.FinalState;
import com.nc.scenario.GenericScenario;
import com.nc.scenario.InformState;
import com.nc.scenario.PauseState;
import com.nc.scenario.PingState;
import com.nc.scenario.Scenario;
import com.nc.scenario.SshState;
import com.nc.scenario.State;
import com.nc.scenario.StateType;
import com.nc.scenario.WebCheckState;


public class XmlConfig implements Config {
	
	private final List<Scenario> scenarios;
	private final List<Host> hosts;
	private final int httpPort;
	private final InformUnit iu;
	
	public XmlConfig(String path) throws XPathExpressionException, ConfigurationException, SAXException, IOException {
		super();
		
		Validator.validate(path);
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		InputSource inputSource = new InputSource(path);
		
		String htp = (String)xpath.evaluate("/servmoncfg/http_port", inputSource, XPathConstants.STRING);
		httpPort = Integer.parseInt(htp);
		
		iu = InformUnit.fromString((String)xpath.evaluate("/servmoncfg/inform_unit", inputSource, XPathConstants.STRING));

		hosts = parseHosts((NodeList) xpath.evaluate("/servmoncfg/hosts/host", inputSource, XPathConstants.NODESET));
//		System.out.println(hosts);
		
		List<Informer> informers = parseInformers((NodeList) xpath.evaluate("/servmoncfg/informers/informer", inputSource, XPathConstants.NODESET));
//		System.out.println(informers);

		
		scenarios = parseScenarios((NodeList) xpath.evaluate("/servmoncfg/scenarios/scenario", inputSource, XPathConstants.NODESET), hosts, informers);
		
	}



	
	@Override
	public int getHttpPort() {
		return httpPort;
	}



	@Override
	public InformUnit getInformUnit() {
		return iu;
	}



	@Override
	public List<Scenario> getScenarios() {
		return new ArrayList<>(scenarios);
	}
	
	
	private static List<Host> parseHosts(NodeList nl) throws ConfigurationException {
		List<Host> l = new ArrayList<>();
		Set<String> idm = new HashSet<>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);			
			String id = n.getAttributes().getNamedItem("id").getTextContent();
			if(idm.add(id) != true){
				throw new ConfigurationException("Each host should have unique id. Id '" + id + "' is not unique.");
			}
			GenericHost.GenericHostBuilder hb = new GenericHost.GenericHostBuilder(id);
			for (int j = 0; j < n.getChildNodes().getLength(); j++) {
				Node hc = n.getChildNodes().item(j);
				if (hc.hasChildNodes()) {
					String hostParamName = n.getChildNodes().item(j)
							.getNodeName();
					String hostParamValue = n.getChildNodes().item(j)
							.getTextContent();
					switch (hostParamName) {
					case "address":
						hb.setAddress(hostParamValue);
						break;
					case "url":
						hb.setUrl(hostParamValue);
						break;
					case "ssh_params":
						NodeList sshp = n.getChildNodes().item(j).getChildNodes();
						Map<String, String> sshpMap = new HashMap<>();
						for (int k = 0; k < sshp.getLength(); k++){
							sshpMap.put(sshp.item(k).getNodeName(), sshp.item(k).getTextContent());
						}
						hb.setSshPort(Integer.parseInt(sshpMap.get("port")));
						hb.setSshUsername(sshpMap.get("username"));
						hb.setSshPassword(sshpMap.get("password"));
						hb.setSshKeypath(sshpMap.get("keypath"));
						break;					

					}
				}
			}
			l.add(hb.getGenericHost());
		}
		return l;
	}
	
	private static List<Informer> parseInformers(NodeList nl) throws ConfigurationException {
		List<Informer> l = new ArrayList<>();
		Set<String> idm = new HashSet<>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			String id = n.getAttributes().getNamedItem("id").getTextContent();			
			if(idm.add(id) != true){
				throw new ConfigurationException("Each informer should have unique id. Id '" + id + "' is not unique.");
			}
			for (int j = 0; j < n.getChildNodes().getLength(); j++) {				
				Node ic = n.getChildNodes().item(j);
				String informerType = ic.getNodeName();
				switch (informerType) {
				case "email":
					l.add(parseEmailInformer(id, ic));
					break;
				case "console":
					l.add(parseConsoleInformer(id, ic));
					break;
				}

			}
		}
		return l;
	}
	
	private static Informer parseEmailInformer(String id, Node n) throws ConfigurationException {
		Map<String, String> emailParams = new HashMap<>();
		List<String> addresseesList = new ArrayList<>();
		boolean isConfigurable = false;
		//collect all parameters to map begin
		for (int i = 0; i < n.getChildNodes().getLength(); i++) {
			Node cn = n.getChildNodes().item(i);
			if (cn.getNodeName().equals("addressees")) {
				NodeList addressees = cn.getChildNodes();
				for (int j = 0; j < addressees.getLength(); j++) {
					if (addressees.item(j).getNodeName().equals("address")) {
						addresseesList.add(addressees.item(j).getTextContent());
					}
				}
			} else if (cn.getNodeName().equals("connection_parameters")) {
				isConfigurable = true;
				NodeList cparams = cn.getChildNodes();
				for (int j = 0; j < cparams.getLength(); j++) {
					emailParams.put(cparams.item(j).getNodeName(), cparams
							.item(j).getTextContent());
				}
			} else {
				emailParams.put(cn.getNodeName(), cn.getTextContent());
			}
		}		
		//collect all parameters to map end		
		
		
		if (isConfigurable){
			return new ConfigurableEmailInformer(id,
					emailParams.get("email_address"),
					emailParams.get("password"),
					emailParams.get("sender_name"), 
					emailParams.get("host_name"), 
					Integer.parseInt(emailParams.get("smtp_port")), 
					Boolean.parseBoolean(emailParams.get("use_ssl")), 
					Boolean.parseBoolean(emailParams.get("use_tls")), 
					addresseesList);
		} else if (emailParams.get("provider") != null){
			return EmailInformers.getEmailInformer(emailParams.get("provider"),
					id,
					emailParams.get("email_address"),
					emailParams.get("password"),
					emailParams.get("sender_name"), addresseesList);
		} else {
			throw new ConfigurationException("Email informer \"" + id
					+ "\" is not recognized. Please check the configuration");
		}
	}

	private static Informer parseConsoleInformer(String id, Node i){		
		return new ConsoleInformer(id);
	}
	
	
	
	private static List<Scenario> parseScenarios(NodeList nl, List<Host> hosts, List<Informer> informers) throws ConfigurationException {
		List<Scenario> l = new ArrayList<>();
		Set<String> idm = new HashSet<>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			String scId = n.getAttributes().getNamedItem("id").getTextContent();
			if(idm.add(scId) != true){
				throw new ConfigurationException("Each scenario should have unique id. Id '" + scId + "' is not unique.");
			}
			int interval = Integer.parseInt(n.getAttributes().getNamedItem("interval").getTextContent());
			List<State> scStates = new ArrayList<>();
			List<Host> scHosts = new ArrayList<>();
			List<Informer> scInformers = new ArrayList<>();
			for (int j = 0; j < n.getChildNodes().getLength(); j++) {
				Node ic = n.getChildNodes().item(j);
				String nodeName = ic.getNodeName();
				switch (nodeName) {
				case "states":
					scStates.addAll(parseStates(ic.getChildNodes(), scId));
					//System.out.println(scStates);
					break;
				case "host_ids":
					scHosts.addAll(parseHostIds(ic.getChildNodes(), hosts, scId));
					//System.out.println(scHosts);
					break;
				case "informer_ids":
					scInformers.addAll(parseInformerIds(ic.getChildNodes(), informers, scId));
					//System.out.println(scInformers);
					break;
				}
			}
			l.add(new GenericScenario(scId, scStates, scHosts, scInformers, interval));
		}
		return l;
	}
	
	private static List<Host> parseHostIds(NodeList nl, List<Host> hosts, String scId) throws ConfigurationException{
		List<Host> scHosts = new ArrayList<>();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeName().equals("host_id")) {
				String hid = nl.item(i).getTextContent();
				if (scHosts.addAll(hosts.stream()
						.filter(h -> h.getId().equals(hid))
						.collect(Collectors.toList())) != true) {
					//list scHosts not changed: host id is invalid (not defined in hosts section)
					throw new ConfigurationException(
							"Scneario id: "
									+ scId
									+ ". Host with id '"
									+ hid
									+ "' not found. Please specify any host Id from Hosts section of XML configuration file");

				}				
			}
		}
		return scHosts;
	}
	
	private static List<Informer> parseInformerIds(NodeList nl, List<Informer> informers, String scId) throws ConfigurationException{
		List<Informer> scInformers = new ArrayList<>();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeName().equals("informer_id")) {
				String iid = nl.item(i).getTextContent();
				if (scInformers.addAll(informers.stream()
						.filter(h -> h.getId().equals(iid))
						.collect(Collectors.toList())) != true) {
					//list scHosts not changed: host id is invalid (not defined in hosts section)
					throw new ConfigurationException(
							"Scneario id: "
									+ scId
									+ ". Informer with id '"
									+ iid
									+ "' not found. Please specify any informer Id from Hosts section of XML configuration file");

				}				
			}
		}
		return scInformers;
	}
	
	private static List<State> parseStates(NodeList nl, String scId) throws ConfigurationException{
		List<State> states = new ArrayList<>();
		Set<String> seqs = new HashSet<>();
		boolean hasFinal = false;
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			String st = n.getNodeName();
			StateType stateType = StateType.UNDEFINED;
			switch (st) {
			case "webcheck":
				stateType = StateType.WEBCHECK;
				break;
			case "ping":
				stateType = StateType.PING;
				break;
			case "ssh":
				stateType = StateType.SSH;
				break;
			case "inform":
				stateType = StateType.INFORM;
				break;
			case "pause":
				stateType = StateType.PAUSE;
				break;
			case "final":
				stateType = StateType.FINAL;
				break;
			default:
				continue; //skip undefined states
			}			
			
			if (stateType == StateType.FINAL) {
				states.add(new FinalState());
				hasFinal = true;
				continue; //final state has no parameters
			}
			
			//get seq
			String seq = null;			
			NamedNodeMap stateAttributes = n.getAttributes();
			if(stateAttributes!=null){
				Node nseq = stateAttributes.getNamedItem("seq");
				if (nseq!=null){
					seq = nseq.getTextContent();
					if(seqs.add(seq) != true){
						throw new ConfigurationException("Scneario id: " + scId + ". Each state should have unique seq. Seq '" + seq + "' is not unique.");
					}
				}
			}
			//end get seq
			
			Map<Event, String> transitions = new HashMap<>();
			List<String> commands = new ArrayList<>();
			String mustContain = null;
			long pause = 0;
			for (int j = 0; j < n.getChildNodes().getLength(); j++) {
				Node ic = n.getChildNodes().item(j);				
				String nodeName = ic.getNodeName();
				switch (nodeName) {
				case "transition":
					    String contains = null;
					    if(ic.getAttributes().getNamedItem("tag") != null){
					    	contains = ic.getAttributes().getNamedItem("tag").getTextContent();
					    }
						Event evt = new Event(Event.EventType.fromString(ic.getAttributes()
								.getNamedItem("event").getTextContent()), contains);
						if(transitions.put(evt, ic
								.getAttributes().getNamedItem("target")
								.getTextContent()) != null){
						throw new ConfigurationException(
								"Scneario id: "
										+ scId
										+ ". Node name: "
										+ st
										+ ": transition is not unique. If you use multiple 'tag' attributes make sure "
										+ "they are not interfered (e.g., tag = 'abcde' equals to tag = 'abc'). Event: "
										+ evt);
						}
					
					break;
				case "must_contain":
					if (!stateType.equals(StateType.WEBCHECK)) {
						throw new ConfigurationException(
								"Scneario id: "
										+ scId
										+ ". Node name: "
										+ st
										+ ": must_contain parameter can only be a part of WEBCHECK state. Current state type : "
										+ stateType);
					}
					mustContain = ic.getTextContent();					
					break;
				case "commands":
					if (!stateType.equals(StateType.SSH)) {
						throw new ConfigurationException(
								"Scneario id: "
										+ scId
										+ ". Node name: "
										+ st
										+ ": commands parameter can only be a part of SSH state. Current state type : "
										+ stateType);
					}
					for (int k = 0; k < ic.getChildNodes().getLength(); k++) {
						if (ic.getChildNodes().item(k).getNodeName().equals("command")) {
							commands.add(ic.getChildNodes().item(k)
									.getTextContent());
						}
					}
					break;
				case "pause":
					if (!stateType.equals(StateType.PAUSE)) {
						throw new ConfigurationException(
								"Scneario id: "
										+ scId
										+ ". Node name: "
										+ st
										+ ": pause parameter can only be a part of PAUSE state. Current state type : "
										+ stateType);
					}
					try{
						pause = Long.parseLong(ic.getTextContent());
					} catch (NumberFormatException e){				
						throw new ConfigurationException(e.getMessage());
					}					
					break;
				}
			}
			switch (stateType) {
			case SSH:				
				states.add(new SshState(seq, transitions, scId, commands));
				break;
			case WEBCHECK:
				states.add(new WebCheckState(seq, transitions, scId, mustContain));
				break;
			case INFORM:
				states.add(new InformState(seq, transitions, scId));
				break;
			case PING:
				states.add(new PingState(seq, transitions, scId));
				break;
			case PAUSE:
				states.add(new PauseState(seq, transitions, scId, pause));
				break;
			case UNDEFINED:
				throw new ConfigurationException("Scneario id: " + scId
						+ ". State type with seq '" + seq
						+ "' is undefined. Node name: " + st);
			default:
				break;
			}		
		}
		
		if (!seqs.contains("initial")) {
			throw new ConfigurationException(
					"Scneario id: "
							+ scId
							+ ". Initial state not found. "
							+ "Each scenario should start with the state with seq=\"initial\"");
		}
		
		if (!hasFinal) {
			throw new ConfigurationException("Scneario id: " + scId
					+ ". Final state not found. "
					+ "Each scenario should contain the <final/> state");
		}

		return states;
	}




	@Override
	public List<Host> getHosts() {
		return new ArrayList<>(hosts);
	}
	


}
