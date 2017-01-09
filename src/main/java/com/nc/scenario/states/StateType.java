package com.nc.scenario.states;

import java.util.HashMap;
import java.util.Map;

public enum StateType {
	SSH("ssh"),
	PING("ping"),
	PAUSE("pause"),
	WEBCHECK("webcheck"),
	INFORM("inform"),
	LOCAL_EXEC("local_exec"),
	FINAL("final"),
	UNDEFINED("undefined");
	
	private String configName;
	private static final Map<String, StateType> MAP = new HashMap<>();

	static {
		for (StateType e : StateType.values()) {
			MAP.put(e.configName, e);
		}
	}

	StateType(String configName) {
		this.configName = configName;
	}

	public static StateType fromString(String stateType) {
		return MAP.getOrDefault(stateType.toLowerCase(), UNDEFINED);
	}
}
