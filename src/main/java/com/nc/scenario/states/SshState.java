package com.nc.scenario.states;

import java.util.List;
import java.util.Map;

import javax.naming.ConfigurationException;

import com.nc.action.Ssher;
import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.host.Host;

class SshState extends AbstractState {
	
	@StateParameter(isOptional = false, xmlName = "commands")
	private final List<String> commands;
	
	@SuppressWarnings("unchecked")
	SshState(String seq, Map<Event, String> transitions,
			String scenarioId, Map<String, Object> parameters)
			throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
		
		commands = (List<String>) parameters.get("commands");
	}



	@Override
	public StateType getType() {
		return StateType.SSH;
	}

	@Override
	public Event run(Host h) {
		try{
			Ssher ss = new Ssher(h.getAddress(), h.getSshPort(), h.getSshUsername(), false, h.getSshPawwsord(), commands);
			return ss.exec();
		} catch (UnsupportedOperationException e){
			return new Event(EventType.UNSUPPORTED, e.toString());
		}
	}

}
