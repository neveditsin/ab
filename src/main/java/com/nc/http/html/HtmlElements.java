package com.nc.http.html;

import java.util.Arrays;
import java.util.List;

public class HtmlElements {
	public static HtmlElement newSimpleElementFromString(String html){
		return new HtmlElement(){

			@Override
			public String getHtml() {
				return html;
			}

			@Override
			public List<HtmlCssStyle> getStyles() {
				return Arrays.asList(HtmlCssStyle.NO_STYLE());
			}

			@Override
			public String getScript() {
				return null;
			}

			@Override
			public String getId() {
				return null;
			}			
		};
	}
}
