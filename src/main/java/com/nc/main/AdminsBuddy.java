package com.nc.main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.quartz.SchedulerException;
import org.xml.sax.SAXException;

import com.nc.config.GlobalConfig;
import com.nc.config.XmlConfig;
import com.nc.events.EventCollector;
import com.nc.events.GlobalEvent;
import com.nc.http.HttpUi;
import com.nc.scenario.Scenario;
import com.nc.scenario.ScenarioScheduler;
import com.nc.utils.GlobalLogger;
import com.nc.visual.HostView;
import com.nc.visual.MainPage;
import com.nc.visual.ScenarioView;
import com.nc.ws.WsServer;



public class AdminsBuddy {
	public static void main(String[] args) throws SecurityException, IOException, SchedulerException, ConfigurationException {
		if(args.length != 1){
			System.out.println("Invalid arguments. Usage: java -jar adminsbuddy-0.8.jar path_to_xml_configuration_file");
			System.out.println("Program terminated");
			return;
		}
		
		if(!new File(args[0]).exists()){
			System.out.println("File " + args[0] + " does not exist");
			System.out.println("Program terminated");
			return;
		}
		
		
		try {
			GlobalConfig.setConfig(new XmlConfig(args[0]));
		} catch (XPathExpressionException | ConfigurationException | SAXException e) {
			System.out.println("Invalid configuration: " + e.getMessage());
			System.out.println("Program terminated");
			return;
		} 
		
		GlobalLogger.init(GlobalConfig.getConfig().getLoggingLevel(), GlobalConfig.getConfig().getLoggingFilePath());
		
	
		int httpPort = GlobalConfig.getConfig().getHttpPort();
		if (httpPort > 0) {
			HttpUi.getInstance().start(httpPort);
			System.out.println("You can check the status of running scenarios here: http://localhost:" + httpPort);
		}		
		
	
		int wsPort = GlobalConfig.getConfig().getWsPort();
		if (wsPort > 0) {
			WsServer.startServer(wsPort, GlobalConfig.getConfig().isWsPublic());
			System.out.println(
					String.format("%s web service started at port %d",
					GlobalConfig.getConfig().isWsPublic() ? "Public" : "Local",
					wsPort));
		}

		
		EventCollector.INSTANCE.registerView(new MainPage());

		//list of hosts is not changed: create host views
		GlobalConfig.getConfig().getHosts().forEach(h -> new HostView(h));
		GlobalConfig.getConfig().getScenarios().forEach(s -> EventCollector.INSTANCE.registerView(new ScenarioView(s)));
		
		start(GlobalConfig.getConfig().getScenarios());
		
	}	
	
	
	private static void start(List<Scenario> scenarios)
			throws SchedulerException {
		for (Scenario sc : scenarios) {
			ScenarioScheduler.INSTANCE.scheduleScenario(sc);
			EventCollector.INSTANCE.registerGlobalEvent(sc.getId(),
					GlobalEvent.SCENARIO_SCHEDULED);
		}
	}
	


}
