package com.nc.scenario.states;


import java.util.Map;


import javax.naming.ConfigurationException;

import com.nc.events.Event;

import com.nc.host.Host;


public class EmailExecState extends AbstractState {
	//TODO change to email_box
	
	@StateParameter(isOptional = false, xmlName = "email_box")
	private final String email;
	
	@StateParameter(isOptional = false, xmlName = "email_box_password")
	private final String password;
	
	@StateParameter(isOptional = false, xmlName = "email_provider")
	private final String provider;
	
	@StateParameter(isOptional = false, xmlName = "trusted_sender_email")
	private final String trustedEmail;
	
	protected EmailExecState(String seq, Map<Event, String> transitions, String scenarioId,
			Map<String, Object> parameters) throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
		
		email = (String) parameters.get("email_box");
		password = (String) parameters.get("email_box_password");
		provider = (String) parameters.get("email_provider");	
		trustedEmail = (String) parameters.get("trusted_sender_email");	
	}

	@Override
	public StateType getType() {
		return StateType.REMAIL_EXEC;
	}

	@Override
	public Event run(Host h, Event lastEvent) throws ConfigurationException {
		return null;
	}

}
