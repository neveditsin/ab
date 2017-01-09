package com.nc.scenario;


import java.util.List;

import com.nc.host.Host;
import com.nc.inform.Informer;

public interface Scenario {
	List<Host> getHosts();
	List<Informer> getInformers();
	List<State> getStates();
	int getInterval();
	String getId();
	void start();
	void stop();
}
