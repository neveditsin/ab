package com.nc.events;

import java.util.HashMap;
import java.util.Map;

public class Event{
	private final EventType et;
	private final String tag;
	
	public Event(EventType et, String tag) {
		super();
		this.et = et;
		this.tag = tag;
	}
	
	public Event getUntaggedEvent(){
		return new Event(et);
	}
	
	public EventType getEventType(){
		return et;
	}
	
	public Event(EventType et) {
		super();
		this.et = et;
		this.tag = null;
	}

	@Override
	public int hashCode() {
		return et.hashCode();
	}
	
	@Override
	public String toString() {
		return et.toString() + (tag != null? "[" +  tag + "]" : "");
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
		if (et != other.et)
			return false;
		
		if (tag != null && other.tag != null){
			if (!(tag.contains(other.tag) || other.tag.contains(tag)))
				return false;
		} else if (tag == null && other.tag == null){
			return true;
		} else {
			return false;
		}
		
		return true;
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

