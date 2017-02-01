package com.nc.scenario.states;


import java.util.Map;

import javax.naming.ConfigurationException;

import com.nc.action.Pinger;
import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.host.Host;

class PingState extends AbstractState{
	
	@StateParameter(isOptional = true, xmlName = "timeout")
	private final int timeout;

	PingState(String seq, Map<Event, String> transitions,
			String scenarioId, Map<String, Object> parameters)
			throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
		
		if (parameters.get("timeout") != null) {
			try {
				timeout = Integer.parseInt((String) parameters.get("timeout"));
			} catch (NumberFormatException e) {
				throw new ConfigurationException("State '" + seq
						+ "': timeout is invalid");
			}
			if (timeout < 0) {
				throw new ConfigurationException("State '" + seq
						+ "': timeout cannot be negative");
			}
		} else {
			timeout = 5000;
		}

	}


	@Override
	public StateType getType() {
		return StateType.IS_REACHABLE;
	}


	@Override
	public Event run(Host h, Event lastEvent) {
		try{
			return new Pinger(h.getAddress(), timeout).exec();
		} catch (UnsupportedOperationException e){
			return new Event(EventType.UNSUPPORTED, e.toString());
		}		
	}	
}
