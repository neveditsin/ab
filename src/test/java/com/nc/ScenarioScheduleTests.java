package com.nc;

import javax.naming.ConfigurationException;

import org.junit.Test;

import com.nc.scenario.ScenarioSchedule;

public class ScenarioScheduleTests {


	@Test
	public void testGetHumanReadable() throws ConfigurationException {
		org.junit.Assert.assertEquals(ScenarioSchedule.newInterval("10M").getHumanReadable(), "Every 10 minutes");
		org.junit.Assert.assertEquals(ScenarioSchedule.newInterval("10S").getHumanReadable(), "Every 10 seconds");
		org.junit.Assert.assertEquals(ScenarioSchedule.newInterval("10H").getHumanReadable(), "Every 10 hours");
		org.junit.Assert.assertEquals(ScenarioSchedule.newInterval("000010MS").getHumanReadable(), "Every 10 milliseconds");
	}


	@Test
	public void testNewInterval() throws ConfigurationException {
		ScenarioSchedule.newInterval("9999999999M");
		ScenarioSchedule.newInterval("9999999999H");
		ScenarioSchedule.newInterval("9999999999S");
		ScenarioSchedule.newInterval("9999999999MS");
	}
	
	@Test(expected=ConfigurationException.class)
	public void testNewIntervalMinus() throws ConfigurationException {
		ScenarioSchedule.newInterval("-1M");
	}
	
	@Test(expected=ConfigurationException.class)
	public void testNewIntervalOverflow() throws ConfigurationException {
		ScenarioSchedule.newInterval("10000000000M");
	}

	@Test
	public void testNewCronExpression() throws ConfigurationException {
		ScenarioSchedule.newCronExpression("0 15 10 ? * MON-FRI");
	}

	
	@Test(expected=ConfigurationException.class)
	public void testNewCronExpressionInvalid() throws ConfigurationException {
		ScenarioSchedule.newInterval("0 15 10 ? * MN-FRI");
	}
}
