package com.nc.events;

import com.nc.scenario.states.State;

public final class StateEvent {
	final State st;
	final Event e;
	final String description;
	final String descriptionFull;
	StateEvent(State st, Event e) {
		super();
		this.st = st;
		this.e = e;
		StringBuilder sb = new StringBuilder();
		sb.append("State seq: '").append(st.getSeq()).append("'. Type: '")
				.append(st.getType()).append("'. Event: '").append(e.getEventType()).append("'");
		
		description = sb.toString() + ";";		
		
		sb.append(". Output: '" + e.getEventInfo() + "';");
		
		descriptionFull = sb.toString();
	}		
	
	public State getState() {
		return st;
	}

	public Event getEvent() {
		return e;
	}

	public String getDescriptionWithOutputs(){		
		return descriptionFull;			
	}
	
	public String getDescriptionWithoutOutputs(){		
		return description;			
	}
	
	@Override
	public String toString(){		
		return description;			
	}
}