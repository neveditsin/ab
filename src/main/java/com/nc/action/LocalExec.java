package com.nc.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.utils.GlobalLogger;

public class LocalExec implements Action {	
	private final String cmd;
	private final Long timeout;
	private final Long forceDestroyIn;
	private final boolean isDaemon;
	
	public LocalExec(String cmd, Long timeout, Long forceDestroyIn, boolean isDaemon) {
		super();
		this.cmd = cmd;
		this.timeout = timeout;
		this.forceDestroyIn = forceDestroyIn;
		this.isDaemon = isDaemon;
	}

	@Override
	public Event exec() {        
        try {
        	Process pr = Runtime.getRuntime().exec(cmd);
        	
			if (isDaemon) {
				// do not check ret and do not wait for termination
				return new Event(EventType.SUCCESS);
			}
			
			if (forceDestroyIn > 0){
				// do not check ret, just destroy the process
				Thread.sleep(forceDestroyIn);
				pr.destroyForcibly();
				return new Event(EventType.SUCCESS);
			}
        	
        	
			if (timeout > 0) {
				if (pr.waitFor(timeout, TimeUnit.MILLISECONDS) == false) {
					Process dp = pr.destroyForcibly();
					if (dp.isAlive()) {
						GlobalLogger
								.warning("Process ["
										+ cmd
										+ "] + could not have been terminated. Please terminate the process manually");
					}
					return new Event(EventType.FAILURE, "timeout");
				}
			}

			int ret = pr.waitFor();
			
			String res = streamToString(pr.getInputStream()).concat(streamToString(pr.getErrorStream()));
			if (ret == 0) {
				return new Event(EventType.SUCCESS, res);				
			} else {
				return new Event(EventType.FAILURE, res);
			}

		} catch (Exception e) {
			return new Event(EventType.EXCEPTION, e.toString());
		}        
	}
	
	private static String streamToString(InputStream is) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(is));            
        StringBuilder sb = new StringBuilder();
		String line;
        while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();			
	}
	



}
