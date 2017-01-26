package com.nc.events;

import java.util.HashMap;
import java.util.Map;

public class Event{
	private final EventType et;
	private final String info;
	
	public Event(EventType et, String info) {
		super();
		this.et = et;
		this.info = info;
	}
	
	public EventType getEventType(){
		return et;
	}
	
	public String getEventInfo(){
		return info;
	}
	
	public Event(EventType et) {
		super();
		this.et = et;
		this.info = null;
	}

	@Override
	public int hashCode() {
		return et.hashCode();
	}
	
	@Override
	public String toString() {
		return et.toString() + (info != null? "[" +  info + "]" : "");
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		
		return et.equals(other.et);
	}




	public static enum EventType {
		SUCCESS("success"), 
		FAILURE("failure"),
		EXCEPTION("exception"),
		UNSUPPORTED("unsupported"),
		UNDEFINED("undefined"),
		ABNORMAL_TERMINATION("abnormal_termination"),
		ANY("any");
		
		private String name;
		private static final Map<String, EventType> MAP = new HashMap<>();

		static {
			for (EventType e : EventType.values()) {
				MAP.put(e.name, e);
			}
		}

		EventType(String name) {
			this.name = name;
		}

		public static EventType fromString(String event) {
			return MAP.get(event.toLowerCase());
		}
	}
}

