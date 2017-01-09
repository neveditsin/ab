package com.nc.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.nc.config.GlobalConfig;
import com.nc.host.Host;
import com.nc.inform.Info;
import com.nc.inform.InformUnit;
import com.nc.inform.Informer;
import com.nc.scenario.ScenarioPool;
import com.nc.scenario.State;
import com.nc.utils.GlobalLogger;
import com.nc.visual.UpdateOn;
import com.nc.visual.View;

public enum EventCollector {
	INSTANCE;
	
	private final Map<String, Multimap<Host, StateEvent>> em = new ConcurrentHashMap<>();
	private final Set<String> scenariosToInform = new ConcurrentSkipListSet<>();
	private final List<View> views = new ArrayList<>();
	private static Multimap<Host, StateEvent> EMPTY_MMAP = LinkedListMultimap.<Host, StateEvent> create();
	
	public void registerView(View v){
		views.add(v);
	}
	
	public void registerEvent(String scenarioId, Host host, State state, Event event){
		//TODO consider more efficient concurrent collections?
		em.putIfAbsent(scenarioId, Multimaps.newMultimap(new ConcurrentHashMap<>(), CopyOnWriteArrayList::new));
		em.get(scenarioId).put(host, new StateEvent(state, event));
		
		views.stream()
				.filter(v -> v.getUpdateCondition().equals(
						UpdateOn.STATE_EXECUTED))
				.forEach(
						vw -> vw.updateView(scenarioId, LinkedListMultimap
								.<Host, StateEvent> create(em.get(scenarioId))));

	}
	
	public void scenarioStart(String scenarioId){
		views.stream()
				.filter(v -> v.getUpdateCondition().equals(
						UpdateOn.SCENARIO_RUN))
				.forEach(vw -> vw.updateView(scenarioId, EMPTY_MMAP));
	}
	
	public void onStateInform(String scenarioId, Host host) {
		if (GlobalConfig.getConfig().getInformUnit().equals(InformUnit.HOST)) {
			//inform immediately
			inform(em
					.get(scenarioId)
					.asMap()
					.keySet()
					.stream()
					.filter(h -> h.equals(host))
					.collect(
							Collectors.toMap(Function.identity(),
									p -> em.get(scenarioId).get(p))),
					ScenarioPool.INSTANCE.getScenario(scenarioId).getInformers());
			em.get(scenarioId).get(host).clear();
		} else if (GlobalConfig.getConfig().getInformUnit().equals(InformUnit.SCENARIO)){
			//indicate that it's necessary to run inform for the scenario
			scenariosToInform.add(scenarioId);
		}
	}
	
	public void scenarioFinish(String scenarioId) {
		if (GlobalConfig.getConfig().getInformUnit().equals(InformUnit.SCENARIO)) {
			if (scenariosToInform.remove(scenarioId) == true) {
				//inform state has been executed for this scenario
				inform(em.get(scenarioId).asMap(), ScenarioPool.INSTANCE
						.getScenario(scenarioId).getInformers());
			}			
		}
		em.get(scenarioId).clear();
	}
	

	public void inform(Map<Host,Collection<StateEvent>> execEvents, List<Informer> informers) {
		if (execEvents.isEmpty() || informers.isEmpty()) {
			return;
		}		

		Info msg = getReport(execEvents);

		for (Informer i : informers) {
			try {
				i.inform(msg);
			} catch (Exception e) {
				GlobalLogger.error("Inform failed", e);
			}
		}

	}

	
	private Info getReport(Map<Host,Collection<StateEvent>> execEvents) {
		Info ret = new Info();

		StringBuilder fullDesc = new StringBuilder();
		StringBuilder shortDesc = new StringBuilder();
		//shortDesc.append("TOTAL: ");

		for (Host h : execEvents.keySet()) {
			
			fullDesc.append("\n -").append(h).append(":\n");
			execEvents
					.get(h)
					.stream()
//					.filter(e -> e.getEvent().getEventType().equals(EventType.FAILURE)
//							|| e..getEvent().getEventType().equals(EventType.EXCEPTION))
					.forEach(e -> fullDesc.append("\n\t").append(e));

		}

		ret.setFullDescription(fullDesc.toString());
		ret.setShortMessage(shortDesc.toString());

		return ret;
	}
	
}
