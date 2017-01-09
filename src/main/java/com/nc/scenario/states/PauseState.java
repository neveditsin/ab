package com.nc.scenario.states;

import java.util.Map;

import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.host.Host;

public class PauseState extends AbstractState {
	private final long pause;

	public PauseState(String seq, Map<Event, String> transitions,
			String scenarioId, long pause) {
		super(seq, transitions, scenarioId);
		this.pause = pause;
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
