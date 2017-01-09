package com.nc.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GlobalLogger {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	public static void init() throws SecurityException, IOException{
		LOGGER.setLevel(Level.FINE);
        Handler h = new FileHandler("servmon.log");
        h.setFormatter(new SimpleFormatter());
      
        LOGGER.addHandler(h);
	}
	

	public static void error(String message, Throwable t){
		LOGGER.log(Level.SEVERE, message, t);
	}
	
	public static void error(String message){
		LOGGER.log(Level.SEVERE, message);
	}
	
	public static void fine(String message){
		LOGGER.log(Level.FINE, message);
	}
	
	public static void warning(String message){
		LOGGER.log(Level.WARNING, message);
	}
	
	public static void info(String message){
		LOGGER.log(Level.INFO, message);
	}

}
