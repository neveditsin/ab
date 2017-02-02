package com.nc.action;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.nc.events.Event;
import com.nc.events.Event.EventType;

public class PortCheck implements Action {
	private final String address;
	private final int port;
	private final int timeout;

	public PortCheck(String address, int port, int timeout) {
		super();
		this.address = address;
		this.port = port;
		this.timeout = timeout;
	}

	@Override
	public Event exec() {
		return check(address, port, timeout);
	}
	
	private static Event check(String address, int port, int timeout) {
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(
					InetAddress.getByName(address), port), timeout);
			socket.close();
			return new Event(EventType.SUCCESS);
		} catch (SocketTimeoutException e) {
			return new Event(EventType.FAILURE, "timeout");
		} catch (IOException e) {
			return new Event(EventType.FAILURE, "connection error: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			return new Event(EventType.EXCEPTION, e.toString());
		}
	}

}
