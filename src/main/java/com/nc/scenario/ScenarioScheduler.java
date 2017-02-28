package com.nc.scenario;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public enum ScenarioScheduler {
	INSTANCE;
	
	private final Scheduler scheduler;
	public static final String SCENARIO_IDENTITY_GROUP = "scenario";
	public static final String JOB_DATA_SCENARIO_ID = "id";
	
	ScenarioScheduler() {
		Scheduler sc = null;
		try {
			sc = StdSchedulerFactory.getDefaultScheduler();
			sc.start();
		} catch (SchedulerException e) {
			System.err.println("FATAL ERROR: Scheduler can not be started. Program terminated: "
					+ e.getMessage());
			System.exit(-1);
		}
		
		scheduler = sc;
	}
	
	
	public boolean scheduleScenario(Scenario sc) throws SchedulerException{
		JobDetail job = JobBuilder.newJob(ScenarioJob.class)
				.withIdentity(sc.getId(), SCENARIO_IDENTITY_GROUP)
				.usingJobData(JOB_DATA_SCENARIO_ID, sc.getId())
				.build();
		
		if (!scheduler.checkExists(job.getKey())){
			scheduler.scheduleJob(job, sc.getSchedule().getTriggerBuilder().build());
			return true;
		}
		return false;
	}
	
	public boolean unscheduleScenario(String scenarioId) throws SchedulerException{		
		return scheduler.deleteJob(new JobKey(scenarioId, SCENARIO_IDENTITY_GROUP));
	}
	
	
}
