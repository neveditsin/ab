package com.nc.scenario;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.naming.ConfigurationException;

import org.apache.commons.lang.NotImplementedException;

import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.events.EventCollector;
import com.nc.host.Host;
import com.nc.inform.Informer;
import com.nc.scenario.states.State;
import com.nc.scenario.states.StateType;
import com.nc.utils.GlobalLogger;

public class GenericScenario implements Scenario {

	private final Map<String, State> statesMap;
	private final List<State> states;
	private final List<Host> hosts;
	private final List<Informer> informers;
	private final int interval;
	private final String id;	
	
	public GenericScenario(String id, List<State> states, List<Host> hosts,
			List<Informer> informers, int interval) {
		super();
		this.id = id;
		this.states = states;
		this.statesMap = states.stream().collect(
				Collectors.toMap(State::getSeq, s -> s, (v1, v2) -> {
					throw new IllegalStateException("Scenario build error: sequences should be unique");
				}, HashMap::new));
		this.hosts = hosts;
		this.informers = informers;
		this.interval = interval;		
		ScenarioPool.INSTANCE.putScenario(id, this);
	}

	@Override
	public List<Host> getHosts() {
		return hosts;
	}

	@Override
	public List<Informer> getInformers() {
		return informers;
	}
	

	@Override
	public void start() {
		while (true) {
			try {
				run();
			}
			catch (ConfigurationException ce) {
				EventCollector.INSTANCE.scenarioFinish(id);
				return;
			}
			catch (Throwable e) {
				GlobalLogger.error("Uncaught exception in scenario \"" + id
						+ "\". Scenario terminated abnormally", e);
				EventCollector.INSTANCE.scenarioFinish(id);
				return;
			}

			try {
				Thread.sleep(1000 * 60 * interval);
			} catch (InterruptedException e) {
				GlobalLogger.warning(e.toString());
			}
		}
	}
	
	private void run() throws ConfigurationException{
		EventCollector.INSTANCE.scenarioStart(id);
		
		for(Host h: hosts){
			State st = statesMap.get("initial");
			do {
				GlobalLogger.fine("Scenario: " + id + ". Host: " + h.getId() + ". State: " + st);
				Event e;

				try{
					e = st.run(h);
				} catch (ConfigurationException ce) {
					GlobalLogger.error("Configuration exception in scenario \"" + id
							+ "\". Scenario terminated abnormally", ce);
					EventCollector.INSTANCE.registerEvent(this.id, h, st, new Event(EventType.ABNORMAL_TERMINATION));
					throw ce;
				}
				
				EventCollector.INSTANCE.registerEvent(this.id, h, st, e);
				
				GlobalLogger.fine("Scenario: " + id + ". Host: " + h.getId() +  ". State: " + st.getType() + ". Event: " + e);
				String target = st.getTransitions().get(e);
				if(target == null){
					//try to get transition for event without tag
					target = st.getTransitions().get(e.getUntaggedEvent());
				}
				//if there are no targets
				if(target == null){
					GlobalLogger.warning(
							"There are no targets. Go to final state. Please check the scenario \""
									+ id + "\" configuration");
					target = "final";
				}
				GlobalLogger.fine("Scenario: " + id + ". Host: " + h.getId() +  ". State: " + st.getType() + ". Target: " + target);				

				st = statesMap.get(target);
			} while(st.getType() != StateType.FINAL);
		}
		
		EventCollector.INSTANCE.scenarioFinish(id);
	}

	@Override
	public void stop() {
		throw new NotImplementedException();
		
	}
	
	@Override
	public String toString(){
		return "Scenario \"" + id + "\"";
	}

	@Override
	public List<State> getStates() {
		return Collections.unmodifiableList(states);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getInterval() {
		return interval;
	}



}
