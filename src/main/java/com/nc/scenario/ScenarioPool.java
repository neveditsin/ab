package com.nc.scenario;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableSet;


public enum ScenarioPool {	
	INSTANCE;
	
	private Map<String, Scenario> scenarioMap = new ConcurrentHashMap<>();
	
	void putScenario(String id, Scenario s) {
		scenarioMap.put(id, s);

	}
	
	public Scenario getScenario(String scenarioId){
		return scenarioMap.get(scenarioId);
	}
	
	public Set<String> getScenarioIds(){
		return ImmutableSet.copyOf(scenarioMap.keySet());
	}
	

	
	
}
