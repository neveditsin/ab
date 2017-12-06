package com.nc.inform;

import java.util.Collection;

import org.apache.commons.mail.EmailException;

import com.nc.mailbox.Mailbox;
import com.nc.utils.EmailUtils;
import com.nc.utils.GlobalLogger;

public class EmailInformer extends AbstractInformer {


	public EmailInformer(String id, Mailbox mailbox, Collection<String> recipients) {
		super(id);
		this.recipients = recipients;
		this.mailbox = mailbox;
	}


	private final Mailbox mailbox;	
	private final Collection<String> recipients;


	@Override
	public void inform(Info message) throws Exception {
		if (recipients == null || recipients.isEmpty()) {
			throw new Exception("Recipients are empty");
		}
		try {
			EmailUtils.SendEmail(mailbox.getEmailAddress(), 
					mailbox.getEmailPassword(),
					mailbox.getSenderName(),
					recipients,
					message.getFullDescription(),
					message.getShortMessage(),
					mailbox.getSmtpHostAddress(),
					mailbox.getSmtpPort(), 
					mailbox.isSmtpUseSsl(),
					mailbox.isSmtpUseTls());
		} catch (EmailException e) {
			GlobalLogger.error("Error sending email: ", e);
			throw new Exception(e);
		}
	}


	@Override
	public String toString() {
		return "EmailInformer [mailbox=" + mailbox + ", recipients=" + recipients + "]";
	}
	
	
	
}



