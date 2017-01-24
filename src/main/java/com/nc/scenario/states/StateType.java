package com.nc.scenario.states;

import java.util.HashMap;
import java.util.Map;

public enum StateType {
	SSH("ssh", SshState.class),
	PING("ping", PingState.class),
	PAUSE("pause",  PauseState.class),
	WEBCHECK("webcheck", WebCheckState.class),
	INFORM("inform", InformState.class),
	LOCAL_EXEC("local_exec", LocalExecState.class),
	FINAL("final", FinalState.class),
	UNDEFINED("undefined", null);

	
	private static final Map<String, StateType> MAP = new HashMap<>();
	
	
	private final String configName;
	private final Class<? extends State> implementingClass;


	static {
		for (StateType e : StateType.values()) {
			MAP.put(e.configName, e);
		}
	}

	StateType(String configName, Class<? extends State> implementingClass) {
		this.configName = configName;
		this.implementingClass = implementingClass;
	}

	public static StateType fromString(String stateType) {
		return MAP.getOrDefault(stateType.toLowerCase(), UNDEFINED);
	}	
	
	
	public Class<? extends State> getImplementingClass(){
		return implementingClass;
	}

}
