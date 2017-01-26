package com.nc.scenario.states;

import java.util.HashMap;
import java.util.Map;

import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.host.Host;


//TODO extend as
public class FinalState implements State {
	private static Map<Event, String> transitions = new HashMap<>();
	static {
		transitions.put(new Event(EventType.ANY), "FIN");
	}

	@Override
	public StateType getType() {
		return StateType.FINAL;
	}

	@Override
	public String getSeq() {
		return "final";
	}

	@Override
	public Map<Event, String> getTransitions() {
		return transitions;
	}

	@Override
	public Event run(Host h, Event lastEvent) {
		throw new UnsupportedOperationException("final state is not runnable");
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
