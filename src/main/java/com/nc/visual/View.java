package com.nc.visual;

import com.google.common.collect.Multimap;
import com.nc.events.GlobalEvent;
import com.nc.events.StateEvent;
import com.nc.host.Host;

public interface View {
	
	/**
	 * updates view using information based on current status of <@Scenario, @Host,
	 * and (local) @Event> if appropriate UpdateCondition is set (see @UpdateOn)
	 * 
	 * @param scenarioId
	 * @param hse
	 * 
	 */
	public void updateView(String scenarioId, Multimap<Host, StateEvent> hse);
	
	/**
	 * updates view using information based on current status of <@Scenario and
	 * @GlobalEvent>. UpdateCondition is not checked
	 * 
	 * @param scenarioId
	 * @param hse
	 * 
	 */
	public void updateViewWithGlobalEvent(String scenarioId, GlobalEvent ge);
	
	public UpdateOn getUpdateCondition();
}
