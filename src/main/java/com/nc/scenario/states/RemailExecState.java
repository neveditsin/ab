package com.nc.scenario.states;


import java.util.Map;


import javax.naming.ConfigurationException;

import com.nc.action.RemailExec;
import com.nc.events.Event;

import com.nc.host.Host;
import com.nc.mailbox.Mailbox;
import com.nc.mailbox.Mailboxes;


public class RemailExecState extends AbstractState {
	
	@StateParameter(isOptional = false, xmlName = "mailbox_id")
	private final String mailboxId;

	@StateParameter(isOptional = false, xmlName = "trusted_sender_email")
	private final String trustedEmail;
	
	@StateParameter(isOptional = false, xmlName = "required_subject")
	private final String requiredSubject;
	
	private Mailbox mb;
	
	protected RemailExecState(String seq, Map<Event, String> transitions, String scenarioId,
			Map<String, Object> parameters) throws ConfigurationException {
		super(seq, transitions, scenarioId, parameters);
		
		trustedEmail = (String) parameters.get("trusted_sender_email");
		requiredSubject = (String) parameters.get("required_subject");
		mailboxId  = (String) parameters.get("mailbox_id");
		
		mb = Mailboxes.getMailbox(mailboxId);
		if (mb == null) {
			throw new ConfigurationException("Scenario '" + scenarioId + "'. State '" + seq + "': Mailbox with id '"
					+ mailboxId + "' not found");
		}
	}

	@Override
	public StateType getType() {
		return StateType.REMAIL_EXEC;
	}

	@Override
	public Event run(Host h, Event lastEvent) throws ConfigurationException {
		return new RemailExec(mb.getEmailAddress(),
				mb.getEmailPassword(),
				mb.getImapHostAddress(),
				mb.getInboxFolderName(), 
				trustedEmail,
				mb.getSenderName(),
				requiredSubject,
				mb.getSmtpHostAddress(), 
				mb.getSmtpPort(), 
				mb.isSmtpUseSsl(),
				mb.isSmtpUseTls())
				.exec();
	}

}
