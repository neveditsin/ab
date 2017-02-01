package com.nc.scenario.states;

import java.util.Map;

import javax.naming.ConfigurationException;

import com.nc.action.PortCheck;
import com.nc.events.Event;
import com.nc.host.Host;

public class PortCheckState extends AbstractState {
	@StateParameter(isOptional = false, xmlName = "port")
	private final int port;
	
	@StateParameter(isOptional = true, xmlName = "timeout")
	private final int timeout;
	
	
	protected PortCheckState(String seq, Map<Event, String> transitions,
			String scenarioId, Map<String, Object> parameters)
			throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);

		port = Integer.parseInt((String) parameters.get("port"));
		
		if (port < 1 || port > 65535) {
			throw new ConfigurationException("State: '" + seq
					+ "': invalid port number: must be in range [1; 65535]");
		}
		
		if (parameters.get("timeout") != null) {
			try {
				timeout = Integer.parseInt((String) parameters.get("timeout"));
			} catch (NumberFormatException e) {
				throw new ConfigurationException("State '" + seq
						+ "': timeout is invalid");
			}

		} else {
			timeout = 5000;
		}
		
		
	}

	@Override
	public StateType getType() {
		return StateType.PORT_CHECK;
	}

	@Override
	public Event run(Host h, Event lastEvent) throws ConfigurationException {
		return new PortCheck(h.getAddress(), port, timeout).exec();
	}

}
