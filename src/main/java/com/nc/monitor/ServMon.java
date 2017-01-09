package com.nc.monitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.naming.ConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.nc.config.GlobalConfig;
import com.nc.config.XmlConfig;
import com.nc.events.EventCollector;
import com.nc.http.HttpUi;
import com.nc.scenario.Scenario;
import com.nc.utils.GlobalLogger;
import com.nc.visual.HostView;
import com.nc.visual.MainPage;
import com.nc.visual.ScenarioView;



public class ServMon {
	public static void main(String[] args) throws SecurityException, IOException {
		if(args.length != 1){
			System.out.println("Invalid arguments. Usage: java -jar servmon path_to_xml_configuration_file");
			System.out.println("Program terminated");
			return;
		}
		
		if(!new File(args[0]).exists()){
			System.out.println("File " + args[0] + " does not exist");
			System.out.println("Program terminated");
			return;
		}
		
		System.out.println("Config path: "+ args[0]);
		
		try {
			GlobalConfig.setConfig(new XmlConfig(args[0]));
		} catch (XPathExpressionException | ConfigurationException | SAXException e) {
			System.out.println("Invalid configuration: " + e.getMessage());
			System.out.println("Program terminated");
			return;
		} 
		
		GlobalLogger.init();
		
	
		int httpPort = GlobalConfig.getConfig().getHttpPort();
		if (httpPort > 0) {
			HttpUi.getInstance().start(httpPort);
		}		
		
		
		
		EventCollector.INSTANCE.registerView(new MainPage());

		//list of hosts is not changed: create host views
		GlobalConfig.getConfig().getHosts().forEach(h -> new HostView(h));
		GlobalConfig.getConfig().getScenarios().forEach(s -> EventCollector.INSTANCE.registerView(new ScenarioView(s)));
		
		start(GlobalConfig.getConfig().getScenarios());
		
	}	
	
	
	private static void start(List<Scenario> scenarios){
		ExecutorService es = Executors.newFixedThreadPool(scenarios.size());
		List<Future<?>> results = new ArrayList<>();
		for(Scenario sc: scenarios){
			results.add(es.submit(() -> sc.start()));
		}
		
		for(Future<?> res : results){
			try {		
				try {
					res.get(15, TimeUnit.SECONDS);
				} catch (TimeoutException e) {		
					//ignore
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
				res.cancel(true);
			}
		}

		
		
	}

}
