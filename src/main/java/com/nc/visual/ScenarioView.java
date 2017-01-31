package com.nc.visual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.nc.events.Event;
import com.nc.events.StateEvent;
import com.nc.events.Event.EventType;
import com.nc.host.Host;
import com.nc.http.HttpUi;
import com.nc.http.PlainHtmlHandler;
import com.nc.http.html.HtmlCssStyle;
import com.nc.http.html.HtmlElement;
import com.nc.http.html.HtmlElements;
import com.nc.http.html.HtmlListOfElements;
import com.nc.http.html.HtmlTable;
import com.nc.scenario.Scenario;
import com.nc.scenario.states.State;

public class ScenarioView implements View{
	private final PlainHtmlHandler handler;
	private final HtmlCssStyle tableStyle = new HtmlCssStyle(
			"responstable",
			".responstable {\r\n"
					+ "  margin: 1em 0;\r\n"
					+ "  width: 33%;\r\n"
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
	
	private final Scenario sc;
	private final HtmlTable scenarioStatisticsTable;
	private final AtomicInteger execCount = new AtomicInteger(0);
	private final AtomicInteger failExecCount = new AtomicInteger(0);
	
	private static final String N_EXEC = "Number of Executions";
	private static final String FAIL_EXEC = "Unsuccessful Excecutions";
	private static final String STARTED_AT = "Started at";
	private static final String INTERVAL = "Exectution Interval";

	
	public ScenarioView(Scenario sc){
		this.sc = sc;
		
		//init static content
		List<HtmlElement> staticContent = new ArrayList<>();
		staticContent.add(HtmlElements.newSimpleElementFromString("<h2>Scenario \"" + sc.getId() + "\": State table</h2>"));
		List<State> states = sc.getStates();
		HtmlTable stateTable = HtmlTable.newInsertionOrderedHtmlTable("stateTable", Arrays.asList(new String[] {
				"State", "Type", "Event -> Next State"}), tableStyle);
		states.forEach(st -> stateTable.addRow(Arrays.asList(
				HtmlElements.newSimpleElementFromString(st.getSeq()),
				HtmlElements.newSimpleElementFromString(st.getType().toString()),
				new HtmlListOfElements(st
						.getTransitions()
						.entrySet()
						.stream()
						.map(tr -> HtmlElements.newSimpleElementFromString(tr.getKey() + " -> " + tr.getValue()))
						.collect(Collectors.toList()))))
		);

		staticContent.add(stateTable);
		
		
		//stat
		staticContent.add(HtmlElements.newSimpleElementFromString("<br>"));
		staticContent.add(HtmlElements.newSimpleElementFromString("<br>"));
		staticContent.add(HtmlElements.newSimpleElementFromString("<h3>Statistics: </h3>"));
		scenarioStatisticsTable = HtmlTable.newInsertionOrderedHtmlTable("statisticsTable", Arrays.asList(new String[] {
				"Item", "Value"}), tableStyle);
		
		scenarioStatisticsTable.addRow(STARTED_AT, 
				Arrays.asList(HtmlElements.newSimpleElementFromString(STARTED_AT),
						HtmlElements.newSimpleElementFromString("Not started yet")));
		
		scenarioStatisticsTable.addRow(INTERVAL, 
				Arrays.asList(HtmlElements.newSimpleElementFromString(INTERVAL),
						HtmlElements.newSimpleElementFromString(sc.getSchedule().getHumanReadable())));
		
		scenarioStatisticsTable.addRow(N_EXEC, 
				Arrays.asList(HtmlElements.newSimpleElementFromString(N_EXEC),
						HtmlElements.newSimpleElementFromString(Integer.toString(execCount.get()))));
		
		scenarioStatisticsTable.addRow(FAIL_EXEC, 
				Arrays.asList(HtmlElements.newSimpleElementFromString(FAIL_EXEC),
						HtmlElements.newSimpleElementFromString(Integer.toString(failExecCount.get()))));

		
		staticContent.add(scenarioStatisticsTable);
		
		
		handler = new PlainHtmlHandler(staticContent, "Scenario \"" + sc.getId() + "\"");
		HttpUi.getInstance().setPage("/scenario_" + sc.getId(), handler);				
		
	}
	
	
	@Override
	public void updateView(String scenarioId, Multimap<Host, StateEvent> hse){
		if(scenarioId.equals(sc.getId())){
			update(scenarioId, hse);
			handler.update();
		}
	}
	
	
	private void update(String scenarioId, Multimap<Host, StateEvent> hse) {
		if (execCount.getAndIncrement() == 0) {
			scenarioStatisticsTable.updateRow(STARTED_AT,
					Arrays.asList(HtmlElements
							.newSimpleElementFromString(STARTED_AT), HtmlElements
							.newSimpleElementFromString(new Date().toString())));
		}

		scenarioStatisticsTable.updateRow(N_EXEC, Arrays.asList(HtmlElements
				.newSimpleElementFromString(N_EXEC), HtmlElements
				.newSimpleElementFromString(Integer.toString(execCount.get()))));
		
		
		long fails = hse
				.values()
				.stream()
				.map(StateEvent::getEvent)
				.map(Event::getEventType)
				.filter(et -> et.equals(EventType.EXCEPTION) || et.equals(EventType.FAILURE) || et.equals(EventType.ABNORMAL_TERMINATION) ).count();
		
		if (fails > 0) {			
			scenarioStatisticsTable.updateRow(FAIL_EXEC, Arrays.asList(
					HtmlElements.newSimpleElementFromString(FAIL_EXEC),
					HtmlElements.newSimpleElementFromString(Integer
							.toString(failExecCount.incrementAndGet()))));
		}
		


	}


	@Override
	public UpdateOn getUpdateCondition() {
		return UpdateOn.SCENARIO_FINISH;
	}


}
