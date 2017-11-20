package com.nc.scenario.states;


import java.util.Map;


import javax.naming.ConfigurationException;

import com.nc.events.Event;

import com.nc.host.Host;


public class EmailExecState extends AbstractState {
	@StateParameter(isOptional = false, xmlName = "email_box")
	private final String email;
	
	@StateParameter(isOptional = false, xmlName = "email_box_password")
	private final String password;
	
	@StateParameter(isOptional = false, xmlName = "imap_host_address")
	private final String hostName;

	@StateParameter(isOptional = false, xmlName = "imap_inbox_folder_name")
	private final String inboxFolderName;
	
	@StateParameter(isOptional = false, xmlName = "trusted_sender_email")
	private final String trustedSenderEmail;
	
	@StateParameter(isOptional = false, xmlName = "smtp_sender_name")
	private final String smtpSenderName;
	
	@StateParameter(isOptional = false, xmlName = "smtp_host_address")
	private final String smtpHostAddress;
		
	@StateParameter(isOptional = false, xmlName = "smtp_port")
	private final int smtpPort;
	
	@StateParameter(isOptional = false, xmlName = "smtp_use_ssl")
	private final boolean smtpUseSsl;
	
	@StateParameter(isOptional = false, xmlName = "smtp_use_tls")
	private final boolean smtpUseTls;
	
	protected EmailExecState(String seq, Map<Event, String> transitions, String scenarioId,
			Map<String, Object> parameters) throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
		
		email = (String) parameters.get("email_box");
		password = (String) parameters.get("email_box_password");
		hostName = (String) parameters.get("imap_host_address");
		inboxFolderName = (String) parameters.get("imap_inbox_folder_name");
		trustedSenderEmail = (String) parameters.get("trusted_sender_email");
		smtpSenderName = (String) parameters.get("smtp_sender_name");
		
		smtpHostAddress = (String) parameters.get("smtp_host_address");
		
		smtpPort = Integer.parseInt((String) parameters.get("smtp_port"));
		smtpUseSsl = Boolean.parseBoolean((String) parameters.get("smtp_use_ssl"));
		smtpUseTls = Boolean.parseBoolean((String) parameters.get("smtp_use_tls"));
		
		
	}

	@Override
	public StateType getType() {
		return StateType.REMAIL_EXEC;
	}

	@Override
	public Event run(Host h, Event lastEvent) throws ConfigurationException {

		return lastEvent;
	}

}
