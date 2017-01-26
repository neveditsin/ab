package com.nc.scenario.states;


import java.util.Map;

import javax.naming.ConfigurationException;

import com.nc.action.Pinger;
import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.host.Host;

class PingState extends AbstractState{


	PingState(String seq, Map<Event, String> transitions,
			String scenarioId, Map<String, Object> parameters)
			throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
	}


	@Override
	public StateType getType() {
		return StateType.PING;
	}


	@Override
	public Event run(Host h, Event lastEvent) {
		try{
			return new Pinger(h.getAddress()).exec();
		} catch (UnsupportedOperationException e){
			return new Event(EventType.UNSUPPORTED, e.toString());
		}		
	}	
}
