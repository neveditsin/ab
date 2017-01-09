package com.nc.visual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nc.host.Host;
import com.nc.http.HttpUi;
import com.nc.http.PlainHtmlHandler;
import com.nc.http.html.HtmlCssStyle;
import com.nc.http.html.HtmlElement;
import com.nc.http.html.HtmlElements;
import com.nc.http.html.HtmlTable;

public class HostView {
	private final HtmlCssStyle tableStyle;

	
	public HostView(Host host) {
		// set styles
		tableStyle = new HtmlCssStyle(
				"responstable",
				".responstable {\r\n"
						+ "  margin: 1em 0;\r\n"
						+ "  width: 50%;\r\n"
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

		List<HtmlElement> he = toHtmlView(host);
		HttpUi.getInstance().setPage("/host_" + host.getId(),
				new PlainHtmlHandler(he, "Host \"" + host.getId() + "\""));
	}
	
	private List<HtmlElement> toHtmlView(Host host) {
		HtmlTable ht = HtmlTable.newInsertionOrderedHtmlTable("scenarioTable",
				Arrays.asList(new String[] { "Parameter", "Value" }),
				tableStyle);
		List<HtmlElement> l = new ArrayList<>();
		l.add(HtmlElements.newSimpleElementFromString("<h2>Host \"" + host.getId() + "\" list of parameters</h2>"));
		
		for (String param : host.getParams().keySet()) {
			ht.addRow(Arrays.asList(
					HtmlElements.newSimpleElementFromString(param),
					HtmlElements.newSimpleElementFromString(host.getParams()
							.compute(param, (k, v) -> (v == null) ? "N/A" : v)
							.toString())));
		}

		l.add(ht);
		return l;
	}

}
