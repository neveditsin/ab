package com.nc.scenario;

import java.util.List;
import java.util.Map;

import com.nc.action.Ssher;
import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.host.Host;

public class SshState extends AbstractState {

	private final List<String> commands;
	
	public SshState(String seq, Map<Event, String> transitions,
			String scenarioId, List<String> commands) {
		super(seq, transitions, scenarioId);
		this.commands = commands;
	}

	@Override
	public StateType getType() {
		return StateType.SSH;
	}

	@Override
	public Event run(Host h) {
		try{
			Ssher ss = new Ssher(h.getAddress(), h.getSshPort(), h.getSshUsername(), false, h.getSshPawwsord(), commands);
			return ss.exec();
		} catch (UnsupportedOperationException e){
			return new Event(EventType.UNSUPPORTED, e.toString());
		}
	}

}
