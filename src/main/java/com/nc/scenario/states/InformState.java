package com.nc.scenario.states;

import java.util.Map;

import javax.naming.ConfigurationException;

import com.nc.events.Event;
import com.nc.events.EventCollector;
import com.nc.events.Event.EventType;
import com.nc.host.Host;

class InformState extends AbstractState {

	InformState(String seq, Map<Event, String> transitions,
			String scenarioId, Map<String, Object> parameters)
			throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
	}

	@Override
	public StateType getType() {
		return StateType.INFORM;
	}

	@Override
	public Event run(Host h) {
		EventCollector.INSTANCE.onStateInform(super.getScenarioId(), h);	
		return new Event(EventType.SUCCESS);
	}

}
