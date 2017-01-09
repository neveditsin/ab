package com.nc.http.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class HtmlTable implements HtmlElement{
	 private final List<String> headers;
	 private final Map<String, List<HtmlElement>> rows;
	 private final String id;
	 private final HtmlCssStyle style;
	 
	 public static HtmlTable newInsertionOrderedHtmlTable(String id, List<String> headers, HtmlCssStyle style){
		 return new HtmlTable(Collections.synchronizedMap(new LinkedHashMap<>()), id, headers, style);
	 }
	 
	 public static HtmlTable newUnorderedHtmlTable(String id, List<String> headers, HtmlCssStyle style){
		 return new HtmlTable(new ConcurrentHashMap<>(), id, headers, style);
	 }
	 
	 private HtmlTable(Map<String,List<HtmlElement>> rows, String id, List<String> headers, HtmlCssStyle style){
		 this.headers = headers;
		 this.id = id;
		 this.style = style;
		 this.rows = rows;
	 }
	 
	 public void addRow(String rowKey, List<HtmlElement> row){
		 if(row.size() != headers.size()){
			 throw new IllegalArgumentException("row size must be " + headers.size());			 
		 }
		 
		 if(rows.put(rowKey, row) != null){
			 throw new IllegalArgumentException("rowKeys must be unique: " + rowKey);				 
		 }
	 }
	 

	 /**
	  * Generates a rowKey from rows content 
	  * It's not recommended to use this function
	  * if your table contains multiple duplicate rows
	  */
	public void addRow(List<HtmlElement> row){
		if(row.size() != headers.size()){
			 throw new IllegalArgumentException("row size must be " + headers.size());			 
		}
		
		String fakeKey = null;
		do{
			fakeKey = fakeKey(row);
		} while(rows.containsKey(fakeKey));
		
		rows.put(fakeKey, row);		 
	 }
	 
	private String fakeKey(List<HtmlElement> row) {
		return row.stream()
				.map(HtmlElement::getHtml)
				.reduce("",
						(a, b) -> a + b
								+ new Random().nextInt(Integer.MAX_VALUE));
	}
	 
	 public void updateRow(String rowKey, List<HtmlElement> row){
		 if(row.size() != headers.size()){
			 throw new IllegalArgumentException("row size must be " + headers.size());			 
		 }
		 rows.put(rowKey, row);
	 }
	 
	 public synchronized void deleteRows(){
		 rows.clear();
	 }
	 
	 @Override
	 public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table class=\"" + style.getId() + "\">\r\n");
		sb.append("  <tr>\r\n");
		// append headers
		for (String hdr : headers) {
			sb.append("    <th>").append(hdr).append("</th>\r\n");
		}
		sb.append("  </tr>\r\n");

		// append rows
		sb.append("  <tr>\r\n");
		for (List<HtmlElement> row : rows.values()) {
			sb.append("  <tr>\r\n");
			for (HtmlElement td : row) {
				sb.append("    <td>").append(td.getHtml()).append("</td>\r\n");
			}

			sb.append("  </tr>\r\n");
		}
		sb.append("</table>\r\n");

		return sb.toString();
	}
	
	@Override
	public List<HtmlCssStyle> getStyles(){		
		List<HtmlCssStyle> styles = new ArrayList<>();
		styles.add(style);
		for (List<HtmlElement> row : rows.values()) {
			for (HtmlElement td : row) {
				styles.addAll(td.getStyles());
			}
		}
		return styles;
	}

	@Override
	public String getScript() {
		StringBuilder sb = new StringBuilder();
		rows.values().stream().flatMap(List::stream).map(HtmlElement::getScript)
				.filter(s -> s != null)
				.forEach(s -> sb.append("\r\n").append(s));
		return sb.toString();
	}

	@Override
	public String getId() {
		return id;
	}
	 
}
