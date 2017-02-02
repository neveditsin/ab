package com.nc.scenario.states;

import java.util.Map;

import javax.naming.ConfigurationException;

import com.nc.action.LocalExec;
import com.nc.events.Event;
import com.nc.host.Host;

class LocalExecState extends AbstractState {	
	@StateParameter(isOptional = false, xmlName = "command")
	private final String command;
	
	@StateParameter(isOptional = true, xmlName = "timeout")
	private final long timeout;
	
	@StateParameter(isOptional = true, xmlName = "force_terminate_in_milliseconds")
	private final long forceTerminateIn;	
	
	@StateParameter(isOptional = true, xmlName = "is_daemon")
	private final boolean isDaemon;
	
	LocalExecState(String seq, Map<Event, String> transitions,
			String scenarioId, Map<String, Object> parameters)
			throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);

		command = (String) parameters.get("command");

		if (parameters.get("timeout") != null) {
			try {
				timeout = Long.parseLong((String) parameters.get("timeout"));
			} catch (NumberFormatException e) {
				throw new ConfigurationException("Scenario '" + scenarioId + "'. State '" + seq
						+ "': timeout is invalid");
			}
			if (timeout < 0) {
				throw new ConfigurationException("Scenario '" + scenarioId + "'. State '" + seq
						+ "': timeout cannot be negative");
			}
		} else {
			timeout = -1;
		}

		
		
		forceTerminateIn = Long.parseUnsignedLong((String) parameters
				.getOrDefault("force_terminate_in_milliseconds", "0"));
		isDaemon = Boolean.parseBoolean((String) parameters.getOrDefault(
				"is_daemon", "false"));

	}

	@Override
	public StateType getType() {
		return StateType.LOCAL_EXEC;
	}

	@Override
	public Event run(Host h, Event lastEvent) {
		return new LocalExec(command, timeout, forceTerminateIn, isDaemon).exec();
	}

}
