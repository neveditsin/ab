package com.nc.inform;


public interface Informer {
	String getId();
	void inform(Info message) throws Exception;
}
