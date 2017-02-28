package com.nc.scenario;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.nc.utils.GlobalLogger;

public class ScenarioJob implements Job{

	@Override
	public void execute(JobExecutionContext jec)
			throws JobExecutionException {
		String scId = null;
		Scenario sc = null;
		if (jec.getJobDetail().getJobDataMap().containsKey(ScenarioScheduler.JOB_DATA_SCENARIO_ID)) {
			scId = jec.getJobDetail().getJobDataMap().getString(ScenarioScheduler.JOB_DATA_SCENARIO_ID);
			sc = ScenarioPool.INSTANCE.getScenario(scId);
		} else {
			GlobalLogger.error("Scenario '" + scId
					+ "' not found in JobDataMap");
			throw new JobExecutionException("Scenario '" + scId
					+ "' not found in JobDataMap");
		}

		if (sc == null) {
			GlobalLogger.error("Scenario '" + scId
					+ "' not found in ScenarioPool");
			throw new JobExecutionException("Scenario '" + scId
					+ "' not found in ScenarioPool");
		}
		
		
		try {
			sc.run();
		} catch (Throwable e) {
			try {
				if (false == jec.getScheduler().deleteJob(
						jec.getJobDetail().getKey())) {
					GlobalLogger.error("Error deleting scenario job '"
							+ jec.getJobDetail().getKey() + "'");
				}
			} catch (SchedulerException se) {
				GlobalLogger.error("Error deleting scenario job", se);
			}
			e.printStackTrace();
		}



	}

}
