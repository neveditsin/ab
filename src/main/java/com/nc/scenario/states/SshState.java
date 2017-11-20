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
	
	
	@StateParameter(isOptional = true, xmlName = "timeout")
	private final long timeout;
	
	@SuppressWarnings("unchecked")
	SshState(String seq, Map<Event, String> transitions,
			String scenarioId, Map<String, Object> parameters)
			throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
		
		commands = (List<String>) parameters.get("commands");
		
		overriddenHost = (Host) parameters.get("host_id");
		
		if (parameters.get("timeout") != null) {
			try {
				timeout = Long.parseLong((String) parameters.get("timeout"));
			} catch (NumberFormatException e) {
				throw new ConfigurationException("Scenario '" + scenarioId + "'. State '" + seq
						+ "': timeout is invalid");
			}
			if (timeout < 0) {
				throw new ConfigurationException("Scenario '" + scenarioId + "'. State '" + seq
						+ "': timeout cannot be negative");
			}
		} else {
			timeout = -1;
		}
	}



	@Override
	public StateType getType() {
		return StateType.SSH;
	}

	@Override
	public Event run(Host h, Event lastEvent) throws ConfigurationException {
		try{
			
			
			//preprocess commands
			List<String> cmds = new ArrayList<>();
			for(String cmd : commands){
				cmds.add(Utils.preprocessString(cmd, h));
			}

			Host host = overriddenHost != null? overriddenHost : h;
			
			Ssher ss = !host.isKeyAuthUsed()? new Ssher(host.getAddress(), host.getSshPort(), host.getSshUsername(), false, host.getSshPawwsord(), cmds, timeout) :
				new Ssher(host.getAddress(), host.getSshPort(), host.getSshUsername(), true, host.getSshKeypath(), cmds, timeout);
			
			
			return ss.exec();
		} catch (UnsupportedOperationException e){
			return new Event(EventType.UNSUPPORTED, e.toString());
		}
	}
	

	


}
