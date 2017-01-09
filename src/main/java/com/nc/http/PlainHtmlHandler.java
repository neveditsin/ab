package com.nc.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nc.http.html.HtmlCssStyle;
import com.nc.http.html.HtmlElement;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class PlainHtmlHandler implements HttpHandler{

	private String response; 
	private String title; 
	private List<HtmlElement> elements;
	
	public PlainHtmlHandler(final List<HtmlElement> els, String title) {
		super();
		this.elements = els;
		this.title = title;
		
		response = buildHtml(new ArrayList<HtmlElement>(els));
	}


	@Override
	public void handle(HttpExchange he) throws IOException {
		he.sendResponseHeaders(200, response.length());
		try (OutputStream os = he.getResponseBody()) {
			os.write(response.getBytes());
		}

	}
	
	
	/**
	 * @param els
	 */
	public void update(final List<HtmlElement> els){
		response = buildHtml(new ArrayList<HtmlElement>(els));
	}
	
	/**
	 * updates Html page using existing HtmlElement list
	 */
	public void update(){
		response = buildHtml(new ArrayList<HtmlElement>(elements));
	}
	
	
	private String buildHtml(final List<HtmlElement> els){
		StringBuilder resp = new StringBuilder();
		resp.append("<!DOCTYPE html>\r\n");
		resp.append("<html>\r\n");
		resp.append("<head>\r\n");
		
		resp.append("<title>");
		resp.append(title);
		resp.append("</title>");
		
		resp.append("<style>\r\n");
		Set<HtmlCssStyle> sm = new HashSet<>();
		for (HtmlElement e : els) {
			List<HtmlCssStyle> styles = e.getStyles();
			for (HtmlCssStyle s : styles) {
				if (sm.add(s) == true) {
					// if there were no styles with this id
					resp.append(s.cssStyle());
				}
			}
		}
		resp.append("\r\n</style>\r\n");
		resp.append("</head>\r\n");

		resp.append("<body>\r\n");
		for (HtmlElement e : els) {
			resp.append(e.getHtml());
		}
		
		resp.append("<script>\r\n");
		
		els.stream().map(HtmlElement::getScript).filter(s -> s != null)
				.forEach(resp::append);

		resp.append("\r\n</script>\r\n");
		
		resp.append("\r\n</body>\r\n");
		resp.append("</html>\r\n");
		
		return resp.toString();
	}	


}
