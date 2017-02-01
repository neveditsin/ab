package com.nc.action;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.nc.events.Event;
import com.nc.events.Event.EventType;

public class Pinger implements Action{	
	private final String address;
	private final int timeout;
	

	public Pinger(String address, int timeout) {
		super();
		this.address = address;
		this.timeout = timeout;
	}

	@Override
	public Event exec() {		
		InetAddress inet = null;
		try {
			inet = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			return new Event(EventType.EXCEPTION, e.toString());
		}
		try {
			if(!inet.isReachable(timeout > 0? timeout : 5000)){
				return new Event(EventType.FAILURE, address + " is unreachable");
			} else {
				return new Event(EventType.SUCCESS);
			}
		} catch (IOException e) {
			return new Event(EventType.EXCEPTION, e.toString());
		}
	}

}
