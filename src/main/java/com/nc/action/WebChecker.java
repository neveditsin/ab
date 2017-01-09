package com.nc.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.nc.events.Event;
import com.nc.events.Event.EventType;

public class WebChecker implements Action {	
	
	private final String url;
	private final String contains;
	
	public WebChecker(String url, String contains) {
		super();
		this.contains = contains;
		this.url = url;
	}


	@Override
	public Event exec() {		
		try {
			String content = getRawHTML(url);
			if (!content.contains(contains)){
				return new Event(EventType.FAILURE, url + " : does not contain " + contains);
			} else{
				return new Event(EventType.SUCCESS);
			}
			
		} catch (IOException e) {
			return new Event(EventType.EXCEPTION, e.toString());
		}
	}



	private static String getRawHTML(String url) throws IOException
	{
		URL u = null;
	    HttpURLConnection conn = null;
		u = new URL(url);		
		conn = (HttpURLConnection)u.openConnection();	
		conn.setReadTimeout(60000);
		
		//TODO to config
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0"); 		
		conn.connect();
		StringBuilder content;
		
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
            content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) 
            content.append(inputLine + "\n");   
        }
		
		return content.toString();		
	}

}
