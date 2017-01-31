package com.nc.scenario;

import static org.quartz.CronScheduleBuilder.cronSchedule;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.ConfigurationException;

import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;


public abstract class ScenarioSchedule {
	private final ScheduleType st;
	
	public abstract String getHumanReadable();
	public abstract TriggerBuilder<?> getTriggerBuilder();
	
	
	ScenarioSchedule(ScheduleType st) {
		super();
		this.st = st;
	}


	public static ScenarioSchedule newInterval(String interval) throws ConfigurationException{
		Matcher m = Pattern.compile("^([0-9]{1,10})(MS|S|M|H)$").matcher(
				interval);
		if (m.find()) {
			return new Interval(IntervalTimeUnit.fromString(m.group(2)), Long.parseLong(m.group(1)));

		} else{
			throw new ConfigurationException("Unable to parse invalid interval '" + interval + "'.");
		}
	}
	
	public static ScenarioSchedule newCronExpression(String cronExpression) throws ConfigurationException{
		return new CronExpression(cronExpression);
	}
	
	public ScheduleType getScheduleType() {
		return st;
	}
	

	static class Interval extends ScenarioSchedule{
		private final IntervalTimeUnit itu;
		private final long interval;
		private final String humanReadable;
		private final TriggerBuilder<?> tb;
		
		
		Interval(IntervalTimeUnit itu, long interval) {
			super(ScheduleType.INTERVAL);
			this.itu = itu;
			this.interval = interval;
			this.humanReadable = "Every " + interval + " " + itu.toString();
			
			SimpleScheduleBuilder sb = null;
			switch (itu) {
			case HOURS:
				sb = SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInHours((int) interval).repeatForever();
				break;
			case MILLISECONDS:
				sb = SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInMilliseconds(interval).repeatForever();
				break;
			case MINUTES:
				sb = SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInMinutes((int) interval).repeatForever();
				break;
			case SECONDS:
				sb = SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds((int) interval).repeatForever();
				break;
			default:
				break;
			}
			
			
			this.tb = TriggerBuilder
					.newTrigger()					
					.startNow()					
					.withSchedule(sb);
			
		}
		public IntervalTimeUnit getIntervalTimeUnit() {
			return itu;
		}
		
		public long getInterval() {
			return interval;
		}
		
		@Override
		public String getHumanReadable() {
			return humanReadable;
		}
		
		
		@Override
		public TriggerBuilder<?> getTriggerBuilder() {
			return tb;
		}
		
	}
	
	
	static class CronExpression extends ScenarioSchedule{
		private final String cronex;
		private final String humanReadable;
		private final TriggerBuilder<?> tb;
		
		CronExpression(String expression) throws ConfigurationException {
			super(ScheduleType.CRON);
			this.cronex = expression;
			CronDefinition cronDefinition = CronDefinitionBuilder
					.instanceDefinitionFor(CronType.QUARTZ);

			try{
				CronParser parser = new CronParser(cronDefinition);
				Cron quartzCron = parser.parse(expression);
				CronDescriptor descriptor = CronDescriptor.instance(Locale.ENGLISH);
				
				//validate
				quartzCron.validate();
				this.humanReadable = descriptor.describe(quartzCron);
			} catch (IllegalArgumentException e){
				throw new ConfigurationException("Illegal Cron expression: " + e.toString());
			}
			
			this.tb = TriggerBuilder
					.newTrigger()
					.withSchedule(cronSchedule(cronex))
					.startNow();
		}
		

		
		
		public String getCronExpression() {
			return cronex;
		}
		

		@Override
		public TriggerBuilder<?> getTriggerBuilder(){
			return tb;
		}


		@Override
		public String getHumanReadable() {
			return humanReadable;
		}		
	}
	



	public static enum ScheduleType {
		CRON, INTERVAL
	}
	
	public static enum IntervalTimeUnit {
		MILLISECONDS("MS", "milliseconds"), 
		SECONDS("S", "seconds"), 
		MINUTES("M", "minutes"),
		HOURS("H", "hours");
		
		private static final Map<String, IntervalTimeUnit> MAP = new HashMap<>();
		static {
			for (IntervalTimeUnit e : IntervalTimeUnit.values()) {
				MAP.put(e.configName, e);
			}
		}

		private final String configName;
		private final String humanReadableName;
		
		private IntervalTimeUnit(String configName, String humanReadableName){
			this.configName = configName;
			this.humanReadableName = humanReadableName;
		}
		
		public static IntervalTimeUnit fromString(String intervalTimeUnit) throws ConfigurationException {
			if(!MAP.containsKey(intervalTimeUnit)){
				throw new ConfigurationException("Interval Time Unit '" + intervalTimeUnit + "' is undefined");
			}
			return MAP.get(intervalTimeUnit);
		}
		
		@Override
		public String toString(){
			return humanReadableName;
		}
		
	}
}
