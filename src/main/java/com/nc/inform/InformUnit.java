package com.nc.inform;

import java.util.HashMap;
import java.util.Map;

import javax.naming.ConfigurationException;


public enum InformUnit {
	GLOBAL("global"), 
	SCENARIO("scenario"),
	HOST("host");

	private String name;
	private static final Map<String, InformUnit> MAP = new HashMap<>();

	static {
		for (InformUnit e : InformUnit.values()) {
			MAP.put(e.name, e);
		}
	}

	InformUnit(String name) {
		this.name = name;
	}

	public static InformUnit fromString(String iu) throws ConfigurationException {
		if(!MAP.containsKey(iu)){
			throw new ConfigurationException("InformUnit '" + iu + "' is invalid");
		} 
		
		return MAP.get(iu.toLowerCase());

	}
}
