package com.nc.scenario.states;

import java.util.Map;

import javax.naming.ConfigurationException;

import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.host.Host;

class PauseState extends AbstractState {
	
	@StateParameter(isOptional = false, xmlName = "pause_milliseconds")
	private final long pause;
	
	PauseState(String seq, Map<Event, String> transitions,
			String scenarioId, Map<String, Object> parameters)
			throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
		try {
			pause = Long.parseLong((String) parameters
					.get("pause_milliseconds"));
		} catch (NumberFormatException e) {
			throw new ConfigurationException(
					"NumberFormatException (pause_milliseconds parameter of PAUSE state): "
							+ e.getMessage());
		}
	}

	@Override
	public StateType getType() {
		return StateType.PAUSE;
	}

	@Override
	public Event run(Host h) {
		try {
			Thread.sleep(pause);
		} catch (InterruptedException e) {						
		}
		
		return new Event(EventType.SUCCESS);
	}

}
