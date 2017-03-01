package com.nc.http;

import java.io.IOException;


import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;



public class HttpUi {
	
	private static int PORT;
	private static HttpUi h = new HttpUi();
	
	public static HttpUi getInstance(){
		return h;
	}
	
	
	private HttpServer server;
	
	private HttpUi() {
	}
	
	
	public synchronized void setPage(String key, HttpHandler context){
		server.getServerConfiguration().addHttpHandler(context, key);
	}
	
	public void start(int port) {
		try {
			PORT = port;
			server = HttpServer.createSimpleServer(null, "0.0.0.0", PORT);
			
			server.start();
		} catch (IOException e) {
			//TODO log failure
			e.printStackTrace();
		}		
	}
	

	public void stop() {
		server.shutdownNow();
	}
	
	public void restart() throws IOException{
		stop();
		server = HttpServer.createSimpleServer(null, "0.0.0.0", PORT);
		start(PORT);		
	}
	

	
	

}
