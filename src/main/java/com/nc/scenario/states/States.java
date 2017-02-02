package com.nc.scenario.states;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.naming.ConfigurationException;

import com.nc.events.Event;

public class States {
	private States() {

	}
	
	public static State newState(StateType stateType, String seq,
			Map<Event, String> transitions, String scenarioId,
			Map<String, Object> parameters) throws ConfigurationException {
		
		if (stateType == StateType.FINAL) {
			return new FinalState();
		}
		

		Class<? extends State> cl = stateType.getImplementingClass();
		if (cl == null) {
			throw new ConfigurationException("Scneario id: " + scenarioId
					+ ". State type with seq '" + seq
					+ "' is undefined or has no implementation.");
		}

		try {
			Constructor<?> cons = cl.getDeclaredConstructor(String.class, Map.class,
					String.class, Map.class);
			return (State) cons.newInstance(seq, transitions, scenarioId,
					parameters);
		} catch (InvocationTargetException e){
			throw new ConfigurationException(e.getCause().getMessage());
		} catch (InstantiationException | IllegalAccessException
				| NoSuchMethodException | SecurityException
				| IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}		

	}
	

}
