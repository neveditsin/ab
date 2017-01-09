package com.nc.config;

public class GlobalConfig {
	private static Config cfg;
	
	public static synchronized void setConfig(Config cfg){
		GlobalConfig.cfg = cfg;
	}
	
	public static synchronized Config getConfig(){
		return cfg;
	}
}
