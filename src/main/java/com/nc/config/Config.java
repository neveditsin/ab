package com.nc.config;


import java.util.List;

import com.nc.host.Host;
import com.nc.inform.InformUnit;
import com.nc.scenario.Scenario;


public interface Config {
	public int getHttpPort();
	public InformUnit getInformUnit();
	public List<Scenario> getScenarios();
	public List<Host> getHosts();
}
