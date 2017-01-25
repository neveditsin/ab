package com.nc.events;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import com.nc.scenario.states.State;
import com.nc.utils.GlobalLogger;
import com.nc.visual.UpdateOn;
import com.nc.visual.View;

public enum EventCollector {
	INSTANCE;
	
	private final Map<String, Multimap<Host, StateEvent>> em = new ConcurrentHashMap<>();
	private final Set<String> scenariosToInform = new ConcurrentSkipListSet<>();
	private final List<View> views = new ArrayList<>();
	private final ExecutorService viewUpdaters = Executors.newSingleThreadExecutor();
	
	private static Multimap<Host, StateEvent> EMPTY_MMAP = LinkedListMultimap.<Host, StateEvent> create();
	
	
	public void registerView(View v){
		views.add(v);
	}
	
	
	/**
	 * The registered event will cause updating of all registered views
	 * where UpdateOn parameter is set to STATE_EXECUTED.
	 * Events map for the current scenario will also be updated.
	 * @param scenarioId - current scenario ID
	 * @param host - current or last host. null will cause NPE
	 * @param state - current or last state
	 * @param event - event to be registered
	 */
	public void registerEvent(String scenarioId, Host host, State state, Event event){
		em.putIfAbsent(scenarioId, Multimaps.newMultimap(new ConcurrentHashMap<>(), CopyOnWriteArrayList::new));		
		em.get(scenarioId).put(host, new StateEvent(state, event));
		
		
		viewUpdaters.execute(() -> views.stream()
				.filter(v -> v.getUpdateCondition().equals(
						UpdateOn.STATE_EXECUTED))
				.forEach(
						vw -> vw.updateView(scenarioId, em.get(scenarioId))));
		

	}
	
	public void scenarioStart(String scenarioId){
		viewUpdaters.execute(() -> views.stream()
				.filter(v -> v.getUpdateCondition().equals(
						UpdateOn.SCENARIO_START))
				.forEach(vw -> vw.updateView(scenarioId, EMPTY_MMAP)));
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
		
		// make sure all views are updated correctly and then clear the
		// corresponding events (multi)map
		viewUpdaters
				.execute(() -> {
					views.stream()
							.filter(v -> v.getUpdateCondition().equals(UpdateOn.SCENARIO_FINISH))
							.forEach(vw -> vw.updateView(scenarioId,
											em.get(scenarioId)));
					em.get(scenarioId).clear();
				});
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

		try {
			fullDesc.append("Instance IP: ").append(InetAddress.getLocalHost()).append("\n");
		} catch (UnknownHostException e1) {	
			GlobalLogger.warning(e1.toString());
		}
		
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
