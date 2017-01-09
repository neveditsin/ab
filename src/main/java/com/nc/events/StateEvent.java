package com.nc.events;

import com.nc.scenario.states.State;

public final class StateEvent {
	final State st;
	final Event e;
	StateEvent(State st, Event e) {
		super();
		this.st = st;
		this.e = e;
	}		
	
	public State getState() {
		return st;
	}

	public Event getEvent() {
		return e;
	}

	@Override
	public String toString(){
		return st.getType().toString() + ": " + e.toString();			
	}
}