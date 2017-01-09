package com.nc.http.html;

import java.util.Arrays;
import java.util.List;

public class HtmlLink implements HtmlElement {

	private final String id;
	private final String text;
	private final String url;
	private final HtmlCssStyle style;
	private final String targetTag;


	public HtmlLink(String id, String text, String url, String targetTag, HtmlCssStyle style) {
		super();
		this.id = id;
		this.text = text;
		this.url = url;
		this.targetTag = targetTag;
		this.style = style;
	}
	
	public HtmlLink(String text, String url, HtmlCssStyle style) {
		this(null, text, url, "_blank", style);
	}
	
	public HtmlLink(String text, String url) {
		this(null, text, url, "_blank", HtmlCssStyle.NO_STYLE());
	}

	@Override
	public String getHtml() {
		return "\r\n<a href=\"" + url + "\" target=\"" + targetTag + "\">"
				+ text +"</a>";
	}

	@Override
	public List<HtmlCssStyle> getStyles() {
		return Arrays.asList(style);
	}

	@Override
	public String getScript() {		
		return null;
	}

	@Override
	public String getId() {
		return id;
	}

}
