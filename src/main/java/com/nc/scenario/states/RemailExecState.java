package com.nc.scenario.states;


import java.util.Map;


import javax.naming.ConfigurationException;

import com.nc.action.RemailExec;
import com.nc.events.Event;

import com.nc.host.Host;
import com.nc.utils.EmailProvider;


public class RemailExecState extends AbstractState {
	//TODO change to email_box
	
	@StateParameter(isOptional = false, xmlName = "email_box")
	private final String email;
	
	@StateParameter(isOptional = false, xmlName = "email_box_password")
	private final String password;
	
	@StateParameter(isOptional = false, xmlName = "email_sender_name")
	private final String senderName;
	
	@StateParameter(isOptional = false, xmlName = "email_provider")
	private final String provider;
	
	@StateParameter(isOptional = false, xmlName = "trusted_sender_email")
	private final String trustedEmail;
	
	private EmailProvider ep;
	
	protected RemailExecState(String seq, Map<Event, String> transitions, String scenarioId,
			Map<String, Object> parameters) throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
		
		email = (String) parameters.get("email_box");
		password = (String) parameters.get("email_box_password");
		provider = (String) parameters.get("email_provider");	
		trustedEmail = (String) parameters.get("trusted_sender_email");
		senderName = (String) parameters.get("email_sender_name");
		
		ep = EmailProvider.fromString(provider);
		if (ep == null) {
			throw new ConfigurationException("email provider '" + provider + "' not found");
		}
	}

	@Override
	public StateType getType() {
		return StateType.REMAIL_EXEC;
	}

	@Override
	public Event run(Host h, Event lastEvent) throws ConfigurationException {
		return new RemailExec(email,
				password,
				ep.getImapHostAddress(),
				ep.getInboxFolderName(), 
				trustedEmail,
				senderName, 
				ep.getSmtpHostAddress(), 
				ep.getSmtpPort(), 
				ep.isSmtpUseSsl(),
				ep.isSmtpUseTls())
				.exec();
	}

}
