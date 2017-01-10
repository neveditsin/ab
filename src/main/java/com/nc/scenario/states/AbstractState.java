package com.nc.scenario.states;

import java.lang.reflect.Field;
import java.util.Map;

import javax.naming.ConfigurationException;

import com.nc.events.Event;

abstract class AbstractState implements State {
	
	private final String seq;
	private final Map<Event, String> transitions;
	private final String scenarioId;
	protected final Map<String, Object> parameters;

	protected AbstractState(String seq, Map<Event, String> transitions, String scenarioId, Map<String, Object> parameters) throws ConfigurationException {
		super();
		this.seq = seq;
		this.transitions = transitions;
		this.scenarioId = scenarioId;
		this.parameters = parameters;
		
		for (Field field : this.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(StateParameter.class)) {
				StateParameter an = field.getAnnotation(StateParameter.class);
				if (!an.isOptional()) {
					if (parameters == null || parameters.get(an.xmlName()) == null) {
						throw new ConfigurationException(this.getClass().getSimpleName()
								+ ": missing mandatory parameter \""
								+ an.xmlName() + "\"");
					}
				}
			}
		}
	}

	protected String getScenarioId(){
		return scenarioId;
	}
	
	@Override
	public final String getSeq() {
		return seq;
	}

	@Override
	public final Map<Event, String> getTransitions() {
		return transitions;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append("{");
		sb.append("seq:");
		sb.append(seq);
		sb.append(", ");
		sb.append("transitions:");
		sb.append(transitions);
		sb.append(", ");
		for (Field field : this.getClass().getDeclaredFields()) {
			if(field.getName().equals("parameters")){
				continue;
			}
			field.setAccessible(true);
			String name = field.getName();
			Object value = null;
			try {
				value = field.get(this);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			sb.append(String.format("%s:%s, ", name, value));
		}
		
		sb.delete(sb.lastIndexOf(","), sb.length());	
		sb.append("}");
		return sb.toString();
	}
	

}
