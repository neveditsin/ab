package com.nc.scenario.states;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.ConfigurationException;

import com.nc.action.Ssher;
import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.host.Host;
import com.nc.utils.Utils;

class SshState extends AbstractState {
	
	@StateParameter(isOptional = false, xmlName = "commands")
	private final List<String> commands;
	
	@StateParameter(isOptional = true, xmlName = "host_id")
	private final Host overriddenHost;
	
	@SuppressWarnings("unchecked")
	SshState(String seq, Map<Event, String> transitions,
			String scenarioId, Map<String, Object> parameters)
			throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
		
		commands = (List<String>) parameters.get("commands");
		
		overriddenHost = (Host) parameters.get("host_id");		
	}



	@Override
	public StateType getType() {
		return StateType.SSH;
	}

	@Override
	public Event run(Host h) throws ConfigurationException {
		try{
			Host host = overriddenHost != null? overriddenHost : h;
			
			//preprocess commands
			List<String> cmds = new ArrayList<>();
			for(String cmd : commands){
				cmds.add(Utils.preprocessString(cmd, host));
			}
			
			Ssher ss = new Ssher(host.getAddress(), host.getSshPort(), host.getSshUsername(), false, host.getSshPawwsord(), cmds);
			return ss.exec();
		} catch (UnsupportedOperationException e){
			return new Event(EventType.UNSUPPORTED, e.toString());
		}
	}
	

	


}
