package com.nc.scenario;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.naming.ConfigurationException;

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
	private final ScenarioSchedule schedule;
	private final String id;
	private final AtomicReference<Host> lastHost = new AtomicReference<Host>();
	private final AtomicReference<State> lastState = new AtomicReference<State>();
	private final AtomicReference<Event> lastEvent = new AtomicReference<Event>();
	
	public GenericScenario(String id, List<State> states, List<Host> hosts,
			List<Informer> informers, ScenarioSchedule schedule) {
		super();
		this.id = id;
		this.states = states;
		this.statesMap = states.stream().collect(
				Collectors.toMap(State::getSeq, s -> s, (v1, v2) -> {
					throw new IllegalStateException("Scenario build error: sequences should be unique");
				}, HashMap::new));
		this.hosts = hosts;
		this.informers = informers;
		this.schedule = schedule;

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
	public void run() throws Throwable {
		try {
			_run();
		} catch (Throwable e) {
			GlobalLogger.error("Uncaught exception in scenario \"" + id
					+ "\". Scenario terminated abnormally", e);
			EventCollector.INSTANCE.registerStateEvent(this.id, lastHost.get(),
					lastState.get(), new Event(EventType.ABNORMAL_TERMINATION));
			EventCollector.INSTANCE.scenarioFinish(id);
			throw e;
		}
	}		
	
	
	private void _run() throws ConfigurationException{
		EventCollector.INSTANCE.scenarioStart(id);
		
		for(Host h: hosts){
			lastHost.set(h);
			State st = statesMap.get("initial");
			do {
				GlobalLogger.fine("Scenario: " + id + ". Host: " + h.getId() + ". State: " + st);
				Event e;
				lastState.set(st);

				e = st.run(h, lastEvent.get());

				lastEvent.set(e);			
				EventCollector.INSTANCE.registerStateEvent(this.id, h, st, e);
				
				GlobalLogger.fine("Scenario: " + id + ". Host: " + h.getId() +  ". State: " + st.getType() + ". Event: " + e);
				String target = st.getTransitions().get(e);

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
	public ScenarioSchedule getSchedule() {
		return schedule;
	}






}
