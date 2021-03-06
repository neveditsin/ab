package com.nc.config;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
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

import com.cronutils.utils.StringUtils;
import com.nc.events.Event;
import com.nc.host.GenericHost;
import com.nc.host.Host;
import com.nc.inform.ConsoleInformer;
import com.nc.inform.EmailInformer;
import com.nc.inform.Informer;
import com.nc.mailbox.EmailProvider;
import com.nc.mailbox.Mailbox;
import com.nc.mailbox.MailboxParameter;
import com.nc.mailbox.Mailboxes;
import com.nc.mailbox.Mailboxes.MailboxFromParametersBuilder;
import com.nc.mailbox.Mailboxes.MailboxFromProviderBuilder;
import com.nc.scenario.GenericScenario;
import com.nc.scenario.Scenario;
import com.nc.scenario.ScenarioSchedule;
import com.nc.scenario.states.FinalState;
import com.nc.scenario.states.State;
import com.nc.scenario.states.StateType;
import com.nc.scenario.states.States;



public class XmlConfig implements Config {
	
	private final List<Scenario> scenarios;
	private final List<Host> hosts;
	private final int httpPort;
	private final int wsPort;
	private final Level loggingLevel;
	private final String loggingFilePath;
	private final boolean isWsPublic;
	private final String wsUsername;
	private final String wsPassword;
	private final String authString;
	
	public XmlConfig(String path) throws XPathExpressionException, ConfigurationException, SAXException, IOException {
		super();
		
		Validator.validate(path);
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		InputSource inputSource = new InputSource(path);
		
		String htp = (String)xpath.evaluate("/cfg/http_port", inputSource, XPathConstants.STRING);
		httpPort = !StringUtils.isEmpty(htp)? Integer.parseInt(htp) : 0;
				
		if (httpPort < 0 || httpPort > 65535) {
			throw new ConfigurationException("Invalid http_port number: must be in range [1; 65535]");
		}
		
		String wsp = (String)xpath.evaluate("/cfg/ws_port", inputSource, XPathConstants.STRING);
		wsPort = !StringUtils.isEmpty(wsp)? Integer.parseInt(wsp) : 0;
		if (wsPort < 0 || wsPort > 65535 || (wsPort == httpPort && wsPort != 0)) {
			throw new ConfigurationException("Invalid ws_port number: must be in range [1; 65535] and be different from httpPort");
		}
		
		String iswspub = (String)xpath.evaluate("/cfg/is_ws_public", inputSource, XPathConstants.STRING);
		isWsPublic = Boolean.parseBoolean(iswspub);
		
		wsUsername = (String)xpath.evaluate("/cfg/ws_username", inputSource, XPathConstants.STRING);
		wsPassword = (String)xpath.evaluate("/cfg/ws_password", inputSource, XPathConstants.STRING);
		
		if (isWsPublic) {
			if (StringUtils.isEmpty(wsUsername) || StringUtils.isEmpty(wsPassword)) {
				throw new ConfigurationException(
						"Invalid configuration: ws_username and ws_password must not be empty for public web service");
			}
		}
		
		authString = new String(Base64.getEncoder().encode(String.format("%s:%s", wsUsername, wsPassword).getBytes()));
		
		String ll = (String)xpath.evaluate("/cfg/logging_level", inputSource, XPathConstants.STRING);
		loggingLevel = ll.length() > 0? Level.parse(ll) : Level.FINE;
		
		loggingFilePath = (String)xpath.evaluate("/cfg/log_file_path", inputSource, XPathConstants.STRING); 

		hosts = parseHosts((NodeList) xpath.evaluate("/cfg/hosts/host", inputSource, XPathConstants.NODESET));
//		System.out.println(hosts);
		
		@SuppressWarnings("unused")
		List<Mailbox> mailboxes = parseMailboxes((NodeList) xpath.evaluate("/cfg/mailboxes/mailbox", inputSource, XPathConstants.NODESET));
//		System.out.println(mailboxes);
		
		
		
		List<Informer> informers = parseInformers((NodeList) xpath.evaluate("/cfg/informers/informer", inputSource, XPathConstants.NODESET));
//		System.out.println(informers);

		
		scenarios = parseScenarios((NodeList) xpath.evaluate("/cfg/scenarios/scenario", inputSource, XPathConstants.NODESET), hosts, informers);
		
	}



	
	private List<Mailbox> parseMailboxes(NodeList nl) throws ConfigurationException {
		List<Mailbox> l = new ArrayList<>();
		Set<String> idm = new HashSet<>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node connectionParameters = null;
			Node n = nl.item(i);
			String id = n.getAttributes().getNamedItem("id").getTextContent();			
			if(idm.add(id) != true){
				throw new ConfigurationException("Each mailbox should have unique id. Id '" + id + "' is not unique.");
			}
			
			Map<String, String> paramMap = new HashMap<>();
			for (int j = 0; j < n.getChildNodes().getLength(); j++) {				
				Node ic = n.getChildNodes().item(j);
				if (ic.getNodeName().equals("connection_parameters")) {
					connectionParameters = ic;
				} else {
					paramMap.put(ic.getNodeName(), ic.getTextContent());
				}

			}
			
			if (connectionParameters != null) {
				// new mailbox from parameters
				MailboxFromParametersBuilder builder = new Mailboxes.MailboxFromParametersBuilder();
				builder.setId(id);
				for (int k = 0; k < connectionParameters.getChildNodes().getLength(); k++) {				
					Node cp = connectionParameters.getChildNodes().item(k);
					paramMap.put(cp.getNodeName(), cp.getTextContent());
				}
				collectMailBoxParameters(paramMap, builder, Mailboxes.MailboxFromParametersBuilder.class);
				l.add(builder.build());
			
			} else {
				//new mailbox from provider
				MailboxFromProviderBuilder builder = new Mailboxes.MailboxFromProviderBuilder();
				builder.setId(id);
				
				EmailProvider p = EmailProvider.fromString(paramMap.get("provider"));
				if (p == null) {
					throw new ConfigurationException("Unrecognized provider '" + paramMap.get("provider") + "'.\n"
							+ "Available providers: " + EmailProvider.getAvailableProviders());
				}
				builder.setProvider(p);
				collectMailBoxParameters(paramMap, builder, MailboxFromProviderBuilder.class);
				l.add(builder.build());
			}
			
			
		}
		return l;
	}


	private void collectMailBoxParameters(Map<String, String> paramMap, Object builder, Class<?> builderClass)
			throws ConfigurationException {
		for (Method meth : builderClass.getDeclaredMethods()) {
			if (meth.isAnnotationPresent(MailboxParameter.class)) {
				MailboxParameter an = meth.getAnnotation(MailboxParameter.class);
				try {
					meth.invoke(builder, paramMap.get(an.xmlName()));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
					throw new ConfigurationException(e.getMessage());
				}
			}
		}
	}


	@Override
	public int getHttpPort() {
		return httpPort;
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
					case "tags":
						NodeList tagsp = n.getChildNodes().item(j).getChildNodes();
						Map<String, String> tagMap = new HashMap<>();
						for (int k = 0; k < tagsp.getLength(); k++) {
							if (tagsp.item(k).getNodeName().equals("tag")) {
								NodeList tagp = tagsp.item(k).getChildNodes();
								String tagId = null;
								String tagValue = null;
								for (int m = 0; m < tagp.getLength(); m++) {
									if (tagp.item(m).getNodeName().equals("id")) {
										tagId = tagp.item(m).getTextContent();
									}
									if (tagp.item(m).getNodeName().equals("value")) {
										tagValue = tagp.item(m).getTextContent();
									}
								}
								if (tagMap.put(tagId, tagValue) != null) {
									throw new ConfigurationException(
											"Host id: '"
													+ id
													+ "'. Each tag should have unique id within a host. Tag id '"
													+ tagId
													+ "' is not unique.");
								}
							}
						}	
						hb.setTagMap(tagMap);
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
		List<String> addresseesList = new ArrayList<>();
		Mailbox mb = null;
		for (int i = 0; i < n.getChildNodes().getLength(); i++) {
			Node cn = n.getChildNodes().item(i);
			if (cn.getNodeName().equals("addressees")) {
				NodeList addressees = cn.getChildNodes();
				for (int j = 0; j < addressees.getLength(); j++) {
					if (addressees.item(j).getNodeName().equals("address")) {
						addresseesList.add(addressees.item(j).getTextContent());
					}
				}
			} else if (cn.getNodeName().equals("mailbox_id")) {				
				String mboxId = cn.getTextContent();
				mb = Mailboxes.getMailbox(mboxId);
				if (mb == null) {
					throw new ConfigurationException("Mailbox with id '" + mboxId + "' is not recognized");
				}
			}			
		}
		return new EmailInformer(id, mb, addresseesList);	

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
			
			Node interval = n.getAttributes().getNamedItem("interval");
			Node cron = n.getAttributes().getNamedItem("cron");
			
			if((interval != null) == (cron != null)){
				throw new ConfigurationException(
						"Scenario '"
								+ scId
								+ "': either 'interval' or 'cron' attribute should present. "
								+ "Both attributes are not allowed.");
			}
			
			ScenarioSchedule sched = interval != null ? ScenarioSchedule
					.newInterval(interval.getTextContent()) : ScenarioSchedule
					.newCronExpression(cron.getTextContent());
			
			
			List<State> scStates = new ArrayList<>();
			List<Host> scHosts = new ArrayList<>();
			List<Informer> scInformers = new ArrayList<>();
			for (int j = 0; j < n.getChildNodes().getLength(); j++) {
				Node ic = n.getChildNodes().item(j);
				String nodeName = ic.getNodeName();
				switch (nodeName) {
				case "states":
					scStates.addAll(parseStates(ic.getChildNodes(), hosts, scId));
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
			l.add(new GenericScenario(scId, scStates, scHosts, scInformers, sched));
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
	
	private static List<State> parseStates(NodeList nl, List<Host> hosts, String scId)
			throws ConfigurationException {
		List<State> states = new ArrayList<>();
		Set<String> seqs = new HashSet<>();
		boolean hasFinal = false;

		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			String st = n.getNodeName();
			StateType stateType = StateType.fromString(st);

			if (stateType.equals(StateType.UNDEFINED)) {
				continue; // skip undefined states
			}

			if (stateType == StateType.FINAL) {
				states.add(new FinalState());
				hasFinal = true;
				continue; // final state has no parameters
			}

			
			Map<String, Object> parameters = new HashMap<>();
			// get attributes
			String seq = null;
			NamedNodeMap stateAttributes = n.getAttributes();
			if (stateAttributes != null) {
				Node nseq = stateAttributes.getNamedItem("seq");
				if (nseq != null) {
					seq = nseq.getTextContent();
					if (seqs.add(seq) != true) {
						throw new ConfigurationException("Scneario id: '" + scId
								+ "'. Each state should have unique seq. Seq '"
								+ seq + "' is not unique.");
					}
				}
				Node nhost = stateAttributes.getNamedItem("host_id");
				if (nhost != null) {
					String hid = nhost.getTextContent();
					Host oh = null;
					try {
						oh = hosts.stream().filter(h -> h.getId().equals(hid))
								.findFirst().get();
					} catch (NoSuchElementException e) {
						throw new ConfigurationException("Scneario id: '" + scId
								+ "'. State seq: '" + seq + "'. Host with id '"
								+ hid + "' not found");
					}

					parameters.put("host_id", oh);
				}
			}
			// end get seq

			// collect parameters begin
			Map<Event, String> transitions = new HashMap<>();

			for (int j = 0; j < n.getChildNodes().getLength(); j++) {
				Node ic = n.getChildNodes().item(j);
				String nodeName = ic.getNodeName();
				switch (nodeName) {
				case "transition":
					String contains = null;
					if (ic.getAttributes().getNamedItem("tag") != null) {
						contains = ic.getAttributes().getNamedItem("tag")
								.getTextContent();
					}
					Event evt = new Event(Event.EventType.fromString(ic
							.getAttributes().getNamedItem("event")
							.getTextContent()), contains);
					if (transitions.put(evt,
							ic.getAttributes().getNamedItem("target")
									.getTextContent()) != null) {
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

				case "commands":
					List<String> commands = new ArrayList<>();
					for (int k = 0; k < ic.getChildNodes().getLength(); k++) {
						if (ic.getChildNodes().item(k).getNodeName()
								.equals("command")) {
							commands.add(ic.getChildNodes().item(k)
									.getTextContent());
						}
					}
					parameters.put(nodeName, commands);

					break;
				default:
					parameters.put(nodeName, ic.getTextContent());
					break;

				}

			}
			// collect parameters end
			
			states.add(States.newState(stateType, seq, transitions, scId, parameters));
		}
		


		// integrity checks
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
		
		List<String> orphanTransitions = states.stream().map(s -> s.getTransitions().values())
				.flatMap(v -> v.stream())
				.distinct()
				.filter(tr -> !tr.equals("final") && !tr.equals("FIN"))
				.filter(tr -> !seqs.contains(tr))
				.collect(Collectors.toList());
		
		if (!orphanTransitions.isEmpty()) {
			throw new ConfigurationException("Scneario id: " + scId
					+ ". Transitions " + orphanTransitions.toString()
					+ " are invalid. There are no corresponding states to go.");
		}

		
		return states;
	}
		




	@Override
	public List<Host> getHosts() {
		return new ArrayList<>(hosts);
	}
	
	@Override
	public Level getLoggingLevel() {
		return loggingLevel;
	}

	@Override
	public String getLoggingFilePath() {
		return loggingFilePath;
	}


	@Override
	public String getWsUsername() {
		return wsUsername;
	}


	@Override
	public String getWsPassword() {
		return wsPassword;
	}


	@Override
	public int getWsPort() {
		return wsPort;
	}


	@Override
	public boolean isWsPublic() {
		return isWsPublic;
	}


	@Override
	public String getAuthString() {
		return authString;
	}


}
