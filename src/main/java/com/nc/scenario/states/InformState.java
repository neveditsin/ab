package com.nc.scenario.states;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.naming.ConfigurationException;

import com.nc.events.Event;
import com.nc.events.EventCollector;
import com.nc.events.Event.EventType;
import com.nc.host.Host;

class InformState extends AbstractState {
	@StateParameter(isOptional = true, xmlName = "event_filter")
	private final EnumSet<EventType> eventFilter;
	
	@StateParameter(isOptional = true, xmlName = "custom_message")
	private final String customMessage;
	
	private static final Map<String, EventType> shortNameToEventType;
	static{
		shortNameToEventType = new HashMap<>();
		shortNameToEventType.put("E", EventType.EXCEPTION);
		shortNameToEventType.put("F", EventType.FAILURE);
		shortNameToEventType.put("S", EventType.SUCCESS);
	}
	
	
	InformState(String seq, Map<Event, String> transitions,
			String scenarioId, Map<String, Object> parameters)
			throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
		
		
		customMessage = (String) parameters.get("custom_message");
				
		String evts = ((String) parameters.getOrDefault("event_filter", "")).toUpperCase();
		
		if (evts.length() > 0){
			if (!evts.toUpperCase().matches("^(?:([EFS])(?!.*\1)){1,3}$")) {
				throw new ConfigurationException(
						"Scenario '"
								+ scenarioId
								+ "'. State '"
								+ seq
								+ "': invalid events set: '"
								+ evts
								+ "'. Valid values are E F S in any order without spaces between (EFS, FE, E, SE, etc.)");
				
			}			
			eventFilter = EnumSet.noneOf(EventType.class);			
			
			for (String et : evts.split("")){
				eventFilter.add(shortNameToEventType.get(et));
			}			
			
		} else{
			eventFilter = EnumSet.allOf(EventType.class);
		}
		
		
	}

	@Override
	public StateType getType() {
		return StateType.INFORM;
	}

	@Override
	public Event run(Host h, Event lastEvent) {
		EventCollector.INSTANCE.onStateInform(super.getScenarioId(), h, eventFilter, customMessage);	
		return new Event(EventType.SUCCESS);
	}

}
