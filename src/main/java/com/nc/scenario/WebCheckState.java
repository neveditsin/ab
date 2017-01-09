package com.nc.scenario;

import java.util.Map;



import com.nc.action.WebChecker;
import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.host.Host;

public class WebCheckState extends AbstractState {
	
	private final String mustContain;
	
	public WebCheckState(String seq, Map<Event, String> transitions,
			String scenarioId, String mustContain) {
		super(seq, transitions, scenarioId);
		this.mustContain = mustContain;
	}
	
	@Override
	public StateType getType() {
		return StateType.WEBCHECK;
	}


	@Override
	public Event run(Host h) {
		try{
			WebChecker w = new WebChecker(h.getUrl(), mustContain);		
			return w.exec();
		} catch (UnsupportedOperationException e){
			return new Event(EventType.UNSUPPORTED, e.toString());
		}		
	}

}
