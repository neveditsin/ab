package com.nc.scenario.states;

import java.util.Map;

import javax.naming.ConfigurationException;

import com.nc.action.WebChecker;
import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.host.Host;
import com.nc.utils.Utils;

class WebCheckState extends AbstractState {
	@StateParameter(isOptional = true, xmlName = "must_contain")
	private final String mustContain;
	
	
	WebCheckState(String seq, Map<Event, String> transitions,
			String scenarioId, Map<String, Object> parameters) throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
		
		mustContain = (String) parameters.get("must_contain");
	}


	
	@Override
	public StateType getType() {
		return StateType.WEBCHECK;
	}


	@Override
	public Event run(Host h, Event lastEvent) throws ConfigurationException {
		try{
			WebChecker w = new WebChecker(h.getUrl(), Utils.preprocessString(mustContain, h));		
			return w.exec();
		} catch (UnsupportedOperationException e){
			return new Event(EventType.UNSUPPORTED, e.toString());
		}		
	}
	
}
