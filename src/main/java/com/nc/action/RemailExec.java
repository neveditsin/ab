package com.nc.action;

import java.io.IOException;
import java.util.Arrays;

import javax.mail.MessagingException;

import org.apache.commons.mail.EmailException;

import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.utils.EmailUtils;
import com.nc.utils.EmailUtils.IncomingEmailMessage;
import com.nc.utils.EmailUtils.InvalidContentTypeException;

public class RemailExec implements Action {
	public RemailExec(String email, String password, String imapHostAddress, String inboxFolderName, String trustedSenderEmail,
			String smtpSenderName, String smtpHostAddress, int smtpPort, boolean smtpUseSsl, boolean smtpUseTls) {
		super();
		this.email = email;
		this.password = password;
		this.imapHostAddress = imapHostAddress;
		this.inboxFolderName = inboxFolderName;
		this.trustedSenderEmail = trustedSenderEmail;
		this.smtpSenderName = smtpSenderName;
		this.smtpHostAddress = smtpHostAddress;
		this.smtpPort = smtpPort;
		this.smtpUseSsl = smtpUseSsl;
		this.smtpUseTls = smtpUseTls;
	}

	private final String email;	
	private final String password;
	private final String imapHostAddress;
	private final String inboxFolderName;
	private final String trustedSenderEmail;
	private final String smtpSenderName;
	private final String smtpHostAddress;
	private final int smtpPort;
	private final boolean smtpUseSsl;
	private final boolean smtpUseTls;
	
	@Override
	public Event exec() {
		try {
			IncomingEmailMessage m = EmailUtils.ReadLastMessageAndDelete(email, password, imapHostAddress,
					inboxFolderName, true);

			String command = m.getMessage();

			LocalExec le = new LocalExec(command, -1L, -1L, false);
			Event result = le.exec();

			try {
				EmailUtils.SendEmail(email, password, smtpSenderName,
						Arrays.asList(new String[] { trustedSenderEmail }), result.getEventInfo(), "RESP",
						smtpHostAddress, smtpPort, smtpUseSsl, smtpUseTls);
			} catch (EmailException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (MessagingException | InvalidContentTypeException | IOException e) {
			return new Event(EventType.EXCEPTION, e.toString());
		}
		return null;
	}

}
