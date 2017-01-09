package com.nc.scenario;


import java.util.Map;

import com.nc.action.Pinger;
import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.host.Host;

public class PingState extends AbstractState{


	public PingState(String seq, Map<Event, String> transitions,
			String scenarioId) {
		super(seq, transitions, scenarioId);
	}


	@Override
	public StateType getType() {
		return StateType.PING;
	}


	@Override
	public Event run(Host h) {
		try{
			return new Pinger(h.getAddress()).exec();
		} catch (UnsupportedOperationException e){
			return new Event(EventType.UNSUPPORTED, e.toString());
		}		
	}	
}
