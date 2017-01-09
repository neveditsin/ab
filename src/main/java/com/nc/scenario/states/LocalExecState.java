package com.nc.scenario.states;

import java.util.Map;

import com.nc.action.LocalExec;
import com.nc.events.Event;
import com.nc.host.Host;

public class LocalExecState extends AbstractState {
	private final String command;
	private final long timeout;
	private final long forceTerminateIn;
	private final boolean isDaemon;
	
	public LocalExecState(String seq, Map<Event, String> transitions,
			String scenarioId, String command, long timeout, long forceTerminateIn, boolean isDaemon) {
		super(seq, transitions, scenarioId);
		this.command = command;
		this.timeout = timeout;
		this.forceTerminateIn = forceTerminateIn;
		this.isDaemon = isDaemon;
	}

	@Override
	public StateType getType() {
		return StateType.LOCAL_EXEC;
	}

	@Override
	public Event run(Host h) {
		return new LocalExec(command, timeout, forceTerminateIn, isDaemon).exec();
	}

}
