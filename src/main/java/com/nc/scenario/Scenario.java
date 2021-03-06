package com.nc.scenario;


import java.util.List;




import com.nc.host.Host;
import com.nc.inform.Informer;
import com.nc.scenario.states.State;

public interface Scenario{
	List<Host> getHosts();
	List<Informer> getInformers();
	List<State> getStates();
	ScenarioSchedule getSchedule();
	String getId();
	void run() throws Throwable;
}
