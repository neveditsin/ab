package com.nc.events;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.nc.events.Event.EventType;
import com.nc.host.Host;
import com.nc.inform.Info;
import com.nc.inform.Informer;
import com.nc.scenario.ScenarioPool;
import com.nc.scenario.states.State;
import com.nc.utils.GlobalLogger;
import com.nc.visual.UpdateOn;
import com.nc.visual.View;

public enum EventCollector {
	INSTANCE;
	
	private final Map<String, Multimap<Host, StateEvent>> em = new ConcurrentHashMap<>();
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
	
	public void onStateInform(String scenarioId, Host host,
			EnumSet<EventType> eventFilter) {
		// inform immediately
		inform(scenarioId, em
				.get(scenarioId)
				.asMap()
				.keySet()
				.stream()
				.filter(h -> h.equals(host))
				.collect(
						Collectors.toMap(Function.identity(),
								p -> em.get(scenarioId).get(p))),
				ScenarioPool.INSTANCE.getScenario(scenarioId).getInformers(),
				eventFilter);
		em.get(scenarioId).get(host).clear();

	}
	
	public void scenarioFinish(String scenarioId) {
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
	

	public void inform(String scenarioId, Map<Host, Collection<StateEvent>> execEvents,
			List<Informer> informers, EnumSet<EventType> eventFilter) {
		if (execEvents.isEmpty() || informers.isEmpty()) {
			return;
		}

		Info msg = getReport(scenarioId, execEvents, eventFilter);

		for (Informer i : informers) {
			try {
				i.inform(msg);
			} catch (Exception e) {
				GlobalLogger.error("Inform failed", e);
			}
		}

	}

	
	private Info getReport(String scenarioId, Map<Host,Collection<StateEvent>> execEvents, EnumSet<EventType> eventFilter) {
		Info ret = new Info();

		StringBuilder fullDesc = new StringBuilder();
		StringBuilder shortDesc = new StringBuilder();
		
		

		try {
			fullDesc.append("Instance: ").append(InetAddress.getLocalHost()).append(";\n");
		} catch (UnknownHostException e1) {	
			GlobalLogger.warning(e1.toString());
		}
		
		
		shortDesc.append("Scenario: '").append(scenarioId).append("'. Hosts: ");
		fullDesc.append("Scenario: '").append(scenarioId).append("';\n");
		
		for (Host h : execEvents.keySet()) {
			shortDesc.append("'").append(h.getId()).append("' ");
			fullDesc.append("- Host '").append(h.getId()).append("' (")
					.append(h.toString()).append(");\n");
			fullDesc.append("  Events report:");
			execEvents
					.get(h)
					.stream()
					.filter(e -> eventFilter.contains(e.getEvent().getEventType()))
					.forEach(e -> fullDesc.append("\n\t* ").append(e.getDescriptionWithOutputs()));
		}

		ret.setFullDescription(fullDesc.toString());
		ret.setShortMessage(shortDesc.toString());

		return ret;
	}
	
}
