package com.nc.http;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

public class HttpUi {
	
	private static int PORT;
	private static HttpUi h = new HttpUi();
	
	public static HttpUi getInstance(){
		return h;
	}
	
	
	private HttpServer server;
	
	private HttpUi() {
	}
	
	public synchronized void updatePage(String key, HttpHandler context){
		server.removeContext(key);
		server.createContext(key, context);
	}
	
	public synchronized void setPage(String key, HttpHandler context){
		server.createContext(key, context);
	}
	



	public void start(int port) {
		try {
			PORT = port;
			server = HttpServer.create(new InetSocketAddress(port), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.setExecutor(null);
		server.start();
	}
	
	public void stop() {
		server.stop(0);
	}
	
	public void restart(){
		stop();
		try {
			server = HttpServer.create(new InetSocketAddress(PORT), 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start(PORT);
	}
	

	
	

}
