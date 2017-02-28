package com.nc.ws.resources;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.quartz.SchedulerException;

import com.nc.events.EventCollector;
import com.nc.events.GlobalEvent;
import com.nc.scenario.Scenario;
import com.nc.scenario.ScenarioPool;
import com.nc.scenario.ScenarioScheduler;

@Path("scenario_control")
public class ScenarioControl {
	//TODO ret values should be refactored
    @GET
    @Path("unschedule")
    @Produces(MediaType.TEXT_PLAIN)
	public String unschedule(@MatrixParam("scenario_id") String scenarioId) {
		if (scenarioId == null) {
			for(String scId : ScenarioPool.INSTANCE.getScenarioIds()){
				try {
					ScenarioScheduler.INSTANCE.unscheduleScenario(scId);
					EventCollector.INSTANCE.registerGlobalEvent(scId, GlobalEvent.SCENARIO_UNSCHEDULED);
				} catch (SchedulerException e) {
					return "ERROR: " + e.toString();
				}
			}
			return "all scenarios unscheduled";
		}

		if (ScenarioPool.INSTANCE.getScenario(scenarioId) == null) {
			return String.format("ERROR: scenario '%s' not found", scenarioId);
		}
		
		try{
			ScenarioScheduler.INSTANCE.unscheduleScenario(scenarioId);
			EventCollector.INSTANCE.registerGlobalEvent(scenarioId, GlobalEvent.SCENARIO_UNSCHEDULED);
			return "scenario unscheduled";
		} catch (SchedulerException e){
			return "ERROR: " + e.toString();
		}		 
	}
    
    
	//TODO ret values should be refactored
    @GET
    @Path("schedule")
    @Produces(MediaType.TEXT_PLAIN)
	public String schedule(@MatrixParam("scenario_id") String scenarioId) {
		if (scenarioId == null) {
			for(Scenario sc : ScenarioPool.INSTANCE.getScenarios()){
				try {
					ScenarioScheduler.INSTANCE.scheduleScenario(sc);
					EventCollector.INSTANCE.registerGlobalEvent(sc.getId(), GlobalEvent.SCENARIO_SCHEDULED);
				} catch (SchedulerException e) {
					return "ERROR: " + e.toString();
				}
			}
			return "all scenarios scheduled";
		}

		if (ScenarioPool.INSTANCE.getScenario(scenarioId) == null) {
			return String.format("ERROR: scenario '%s' not found", scenarioId);
		}
		
		try{
			ScenarioScheduler.INSTANCE.scheduleScenario(ScenarioPool.INSTANCE.getScenario(scenarioId));
			EventCollector.INSTANCE.registerGlobalEvent(scenarioId, GlobalEvent.SCENARIO_SCHEDULED);
			return "scenario scheduled";
		} catch (SchedulerException e){
			return "ERROR: " + e.toString();
		}		 
	}
}
