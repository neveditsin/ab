package com.nc.scenario.states;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.naming.ConfigurationException;

import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.host.Host;

public class ConditionCheckState extends AbstractState {
	
	@StateParameter(isOptional = true, xmlName = "event_info_matches")
	private final String eiMatches;
	
	@StateParameter(isOptional = true, xmlName = "event_info_contains")
	private final String eiContains;
	
	protected ConditionCheckState(String seq, Map<Event, String> transitions,
			String scenarioId, Map<String, Object> parameters)
			throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
		
		eiMatches = (String) parameters.get("event_info_matches");
		eiContains = (String) parameters.get("event_info_contains");
	}

	@Override
	public StateType getType() {
		return StateType.CONDITION_CHECK;
	}

	@Override
	public Event run(Host h, Event lastEvent) throws ConfigurationException {
		if (lastEvent == null || lastEvent.getEventInfo() == null
				|| (eiMatches == null && eiContains == null)) {
			return new Event(EventType.EXCEPTION, "No Event/EventInfo/String to match provided");
		}
		
		try {
			if (eiMatches != null && Pattern.compile(eiMatches, Pattern.DOTALL)
					.matcher(lastEvent.getEventInfo()).matches()) {
				return new Event(EventType.SUCCESS);
			} 
			
			if (eiContains != null && lastEvent.getEventInfo().contains(eiContains)) {
				return new Event(EventType.SUCCESS);
			}			
		} catch (PatternSyntaxException e) {
			return new Event(EventType.EXCEPTION, e.toString());
		}
		
		return new Event(EventType.FAILURE);
	}
	

}
