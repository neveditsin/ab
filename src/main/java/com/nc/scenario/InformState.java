package com.nc.scenario;

import java.util.Map;

import com.nc.events.Event;
import com.nc.events.EventCollector;
import com.nc.events.Event.EventType;
import com.nc.host.Host;

public class InformState extends AbstractState {


	public InformState(String seq, Map<Event, String> transitions,
			String scenarioId) {
		super(seq, transitions, scenarioId);
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
