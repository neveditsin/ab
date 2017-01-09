package com.nc.inform;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpSMSInformer implements Informer {
	private int sentNum;
	private final int limit;	

	public HttpSMSInformer(int limit) {
		super();
		this.limit = limit;
	}


	@Override
	public void inform(Info message) throws Exception {
		if(sentNum >= limit){
			throw new Exception("limit exceeded for today");
		}
		String txt = message.getFullDescription().length() < 159? message.getFullDescription() : message.getShortMessage().substring(0, 159);		
		txt = txt.replace(" ", "_");
		URL u = null;
	    HttpURLConnection conn = null;
		String http = "http://sms.ru/sms/send?api_id=a96892a8-43d2-6ac4-91cb-c749a85bfff9&to=79513478877&text=" + txt;		
		u = new URL(http);		
		conn = (HttpURLConnection)u.openConnection();	
		conn.setReadTimeout(600000);
		conn.connect();	
		sentNum++;
		
		
		StringBuilder content;
		
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
            content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) 
            content.append(inputLine + "\n");   
        }		
		
	}


	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

}
