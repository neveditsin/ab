package com.nc.http.html;

import java.util.List;
import java.util.stream.Collectors;

public class HtmlListOfElements implements HtmlElement {
	private final String html;
	private final String id;
	private final String scripts;
	private final List<HtmlCssStyle> styles;
	

	public HtmlListOfElements(String id, List<? extends HtmlElement> elements) {
		super();
		this.id = id;
		html = elements.stream().map(s -> "<li>" + s.getHtml() + "</li>")
				.reduce("<ul>", (a, b) -> a + "\r\n" + b) + "</ul>";
		scripts = elements.stream().map(HtmlElement::getScript).filter(s -> s != null)
				.reduce("", (a, b) -> a + "\r\n" + b);
		styles = elements.stream().map(HtmlElement::getStyles)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}
	
	public HtmlListOfElements(List<? extends HtmlElement> elements) {
		this("", elements);
	}

	@Override
	public String getHtml() {
		return html;
	}

	@Override
	public List<HtmlCssStyle> getStyles() {
		return styles;
	}

	@Override
	public String getScript() {
		return scripts;
	}

	@Override
	public String getId() {
		return id;
	}
	

}
