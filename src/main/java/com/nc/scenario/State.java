package com.nc.scenario;

import java.util.Map;

import com.nc.events.Event;
import com.nc.host.Host;

public interface State {
	StateType getType();
	String getSeq();
	Map<Event, String> getTransitions();
	Event run(Host h);
}
