package com.nc.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.naming.ConfigurationException;

public class GlobalLogger {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private final static String DEFAULT_LOG_FILE = "adminbuddy"; 
	
	public static void init(Level l, String path) throws SecurityException, IOException, ConfigurationException{		
		LOGGER.setLevel(l);
		
		StringBuilder tb = new StringBuilder(".");
		tb.append(Calendar.getInstance().get(Calendar.YEAR)).append("_")
				.append(Calendar.getInstance().get(Calendar.MONTH) + 1).append("_")
				.append(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
				.append("_").append(Calendar.getInstance().get(Calendar.HOUR))
				.append(Calendar.getInstance().get(Calendar.MINUTE))
				.append(".log");
		Handler h = null;
		
		Path tmp = Paths.get(DEFAULT_LOG_FILE + ".tmp");
		if (path == null || path.length() < 1) {
			System.out.print("Log file path is not set. ");
			
			try{				
				if(Files.createFile(tmp) != null){
					Files.delete(tmp);
					System.out
							.println("Use default directory: "
									+ tmp.toAbsolutePath().getParent()
									+ File.separator);
					h = newFileHandler(DEFAULT_LOG_FILE + tb.toString());
				}
			} catch(Exception e){
				System.out.println("Unable to create a file in the default directory. Use stderr for logging ");
				h = newStderrHandler();
			}			
		} else{
			//path is set
			if (Files.isWritable(Paths.get(path))) {
				h = newFileHandler(path + File.separator + DEFAULT_LOG_FILE + tb.toString());
			} else{
				throw new ConfigurationException(
						"The provided log file path is invalid or not writable. Please check write permissions");
			}
			
		}
		
		

        LOGGER.addHandler(h);
	}
	
	private static Handler newFileHandler(String path) throws SecurityException, IOException{
		Handler h = new FileHandler(path);
        h.setFormatter(new SimpleFormatter());
        return h;
	}
	
	private static Handler newStderrHandler() {
		Handler h = new StreamHandler();
        h.setFormatter(new SimpleFormatter());
        return h;
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
