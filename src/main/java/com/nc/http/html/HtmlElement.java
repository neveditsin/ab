package com.nc.http.html;

import java.util.List;

public interface HtmlElement {
	public String getHtml();
	public List<HtmlCssStyle> getStyles();
	public String getScript();
	public String getId();
}
