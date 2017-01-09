package com.nc.http.html;

public class HtmlCssStyle {

	private final static HtmlCssStyle NO_STYLE = new HtmlCssStyle("NO_STYLE", "");
	
	private final String id;
	private final String style;
	
	public HtmlCssStyle(String id, String style) {
		super();
		this.id = id;
		this.style = style;
	}


	public String getId() {
		return id;
	}
	
	public String cssStyle() {
		return style;
	}
	
	public static HtmlCssStyle NO_STYLE(){
		return NO_STYLE;
	}


	@Override
	public int hashCode() {
		return id.hashCode();
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HtmlCssStyle other = (HtmlCssStyle) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	

}
