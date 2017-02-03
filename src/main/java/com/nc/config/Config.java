package com.nc.config;


import java.util.List;
import java.util.logging.Level;

import com.nc.host.Host;
import com.nc.scenario.Scenario;


public interface Config {
	public int getHttpPort();
	public List<Scenario> getScenarios();
	public List<Host> getHosts();
	public Level getLoggingLevel();
	public String getLoggingFilePath();
}
