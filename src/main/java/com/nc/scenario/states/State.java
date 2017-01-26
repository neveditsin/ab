package com.nc.scenario.states;

import java.util.Map;

import javax.naming.ConfigurationException;

import com.nc.events.Event;
import com.nc.host.Host;

public interface State {
	StateType getType();
	String getSeq();
	Map<Event, String> getTransitions();
	Event run(Host h, Event lastEvent) throws ConfigurationException;

}
