package com.nc.visual;

import com.google.common.collect.Multimap;
import com.nc.events.StateEvent;
import com.nc.host.Host;

public interface View {
	public void updateView(String scenarioId, Multimap<Host, StateEvent> hse);
	public UpdateOn getUpdateCondition();
}
