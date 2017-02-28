package com.nc.visual;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;







import com.google.common.collect.Multimap;
import com.nc.config.GlobalConfig;
import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.events.GlobalEvent;
import com.nc.events.StateEvent;
import com.nc.host.Host;
import com.nc.http.HttpUi;
import com.nc.http.PlainHtmlHandler;
import com.nc.http.html.HtmlCssStyle;
import com.nc.http.html.HtmlElement;
import com.nc.http.html.HtmlElements;
import com.nc.http.html.HtmlLink;
import com.nc.http.html.HtmlListOfElements;
import com.nc.http.html.HtmlTable;
import com.nc.scenario.Scenario;


public class MainPage implements View{
	private final PlainHtmlHandler handler;
	private final HtmlCssStyle tableStyle;
	private final List<Scenario> scenarios = GlobalConfig.getConfig().getScenarios();
	private final Map<String, List<HtmlLink>> hosts;
	private final HtmlTable scenarioTable;
	private final AtomicReference<Boolean> ignoreUpdates = new AtomicReference<Boolean>(false);
	
	{
		hosts = scenarios.stream().collect(
				Collectors.groupingBy(Scenario::getId, Collectors.mapping(
						Scenario::getHosts, Collectors.collectingAndThen(
								Collectors.toList(),
								l -> l.stream()
										.flatMap(List::stream)
										.map(h -> new HtmlLink(h.getId(),
												"host_" + h.getId()))
										.collect(Collectors.toList())))));
	}
	
	{
		tableStyle = new HtmlCssStyle(
				"responstable",
				".responstable {\r\n"
						+ "  margin: 1em 0;\r\n"
						+ "  width: 75%;\r\n"
						+ "  overflow: hidden;\r\n"
						+ "  background: #FFF;\r\n"
						+ "  color: #024457;\r\n"
						+ "  border-radius: 10px;\r\n"
						+ "  border: 1px solid #167F92;\r\n"
						+ "}\r\n"
						+ ".responstable tr {\r\n"
						+ "  border: 1px solid #D9E4E6;\r\n"
						+ "}\r\n"
						+ ".responstable tr:nth-child(odd) {\r\n"
						+ "  background-color: #EAF3F3;\r\n"
						+ "}\r\n"
						+ ".responstable th {\r\n"
						+ "  display: none;\r\n"
						+ "  border: 1px solid #FFF;\r\n"
						+ "  background-color: #167F92;\r\n"
						+ "  color: #FFF;\r\n"
						+ "  padding: 1em;\r\n"
						+ "}\r\n"
						+ ".responstable th:first-child {\r\n"
						+ "  display: table-cell;\r\n"
						+ "  text-align: center;\r\n"
						+ "}\r\n"
						+ ".responstable th:nth-child(2) {\r\n"
						+ "  display: table-cell;\r\n"
						+ "}\r\n"
						+ ".responstable th:nth-child(2) span {\r\n"
						+ "  display: none;\r\n"
						+ "}\r\n"
						+ ".responstable th:nth-child(2):after {\r\n"
						+ "  content: attr(data-th);\r\n"
						+ "}\r\n"
						+ "@media (min-width: 480px) {\r\n"
						+ "  .responstable th:nth-child(2) span {\r\n"
						+ "    display: block;\r\n"
						+ "  }\r\n"
						+ "  .responstable th:nth-child(2):after {\r\n"
						+ "    display: none;\r\n"
						+ "  }\r\n"
						+ "}\r\n"
						+ ".responstable td {\r\n"
						+ "  display: block;\r\n"
						+ "  word-wrap: break-word;\r\n"
						+ "  max-width: 7em;\r\n"
						+ "}\r\n"
						+ ".responstable td:first-child {\r\n"
						+ "  display: table-cell;\r\n"
						+ "  text-align: center;\r\n"
						+ "  border-right: 1px solid #D9E4E6;\r\n"
						+ "}\r\n"
						+ "@media (min-width: 480px) {\r\n"
						+ "  .responstable td {\r\n"
						+ "    border: 1px solid #D9E4E6;\r\n"
						+ "  }\r\n"
						+ "}\r\n"
						+ ".responstable th, .responstable td {\r\n"
						+ "  text-align: left;\r\n"
						+ "  margin: .5em 1em;\r\n"
						+ "}\r\n"
						+ "@media (min-width: 480px) {\r\n"
						+ "  .responstable th, .responstable td {\r\n"
						+ "    display: table-cell;\r\n"
						+ "    padding: 1em;\r\n"
						+ "  }\r\n"
						+ "}\r\n"
						+ "\r\n"
						+ "body {\r\n"
						+ "  padding: 0 2em;\r\n"
						+ "  font-family: Arial, sans-serif;\r\n"
						+ "  color: #024457;\r\n"
						+ "  background: #f2f2f2;\r\n"
						+ "}\r\n"
						+ "\r\n"
						+ "h1 {\r\n"
						+ "  font-family: Verdana;\r\n"
						+ "  font-weight: normal;\r\n"
						+ "  color: #024457;\r\n"
						+ "}\r\n"
						+ "h1 span {\r\n"
						+ "  color: #167F92;\r\n"
						+ "}\r\n");	
	}
	
	public MainPage(){
		scenarioTable = HtmlTable.newUnorderedHtmlTable("scenarioTable",
				Arrays.asList(new String[] { "Scenario", "Status", "Hosts" }),
				tableStyle);
		handler = new PlainHtmlHandler(init(), "Admin Buddy");
		HttpUi.getInstance().setPage("/", handler);		
	}
	
	@Override
	public void updateView(String scenarioId, Multimap<Host, StateEvent> hse){
		if (ignoreUpdates.get() == true)
			return;
		update(scenarioId, hse);
		handler.update();
	}
	
	
	private List<HtmlElement> init() {
		List<HtmlElement> l = new CopyOnWriteArrayList<>();
		l.add(HtmlElements
				.newSimpleElementFromString("<h1>Status of running scenarios</h1>"));

		for (Scenario sc : scenarios) {
			scenarioTable.addRow(sc.getId(), Arrays.asList(
					new HtmlLink(sc.getId(), "scenario_" + sc.getId()),
					HtmlElements.newSimpleElementFromString("Loaded"),
					new HtmlListOfElements(hosts.get(sc.getId()))));
		}
		l.add(scenarioTable);
		return l;
		
	}
	private void update(String scenarioId, Multimap<Host, StateEvent> hse){
		

		if (hse.values().stream().map(StateEvent::getEvent)
				.map(Event::getEventType)
				.filter(et -> et.equals(EventType.ABNORMAL_TERMINATION))
				.count() > 0) {
			scenarioTable
					.updateRow(scenarioId,
							Arrays.asList(new HtmlLink(scenarioId, "scenario_" + scenarioId),
									HtmlElements
											.newSimpleElementFromString("TERMINATED ABNORMALLY: see log for details"),
									new HtmlListOfElements(hosts
											.get(scenarioId))));
			return;
		}

		List<HtmlElement> status = hse
				.values()
				.stream()
				.map(StateEvent::getEvent)
				.map(Event::getEventType)
				.filter(et -> et.equals(EventType.EXCEPTION)
						|| et.equals(EventType.FAILURE))
				.collect(Collectors.groupingBy(Function.identity(),
								Collectors.counting()))
				.entrySet()
				.stream()
				.map(e -> HtmlElements.newSimpleElementFromString(e.getKey()
						+ "S: " + e.getValue())).collect(Collectors.toList());

		scenarioTable.updateRow(scenarioId, Arrays.asList(
				new HtmlLink(scenarioId, "scenario_" + scenarioId),
				status.isEmpty() ? HtmlElements
						.newSimpleElementFromString("FINE")
						: new HtmlListOfElements(status),
				new HtmlListOfElements(hosts.get(scenarioId))));

	}

	@Override
	public UpdateOn getUpdateCondition() {
		return UpdateOn.STATE_EXECUTED;
	}

	@Override
	public void updateViewWithGlobalEvent(String scenarioId, GlobalEvent ge) {
		if(ge.equals(GlobalEvent.SCENARIO_UNSCHEDULED)){
			ignoreUpdates.set(true);
		} else if(ge.equals(GlobalEvent.SCENARIO_SCHEDULED)){
			ignoreUpdates.set(false);
		}
		
		scenarioTable.updateRow(scenarioId, Arrays.asList(
				new HtmlLink(scenarioId, "scenario_" + scenarioId),
				HtmlElements.newSimpleElementFromString(ge.toString()),
				new HtmlListOfElements(hosts.get(scenarioId))));
		handler.update();		
	}



}
