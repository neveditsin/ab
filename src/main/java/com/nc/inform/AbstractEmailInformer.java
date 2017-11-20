package com.nc.inform;

import java.util.Collection;

import org.apache.commons.mail.EmailException;

import com.nc.utils.EmailUtils;
import com.nc.utils.GlobalLogger;

public abstract class AbstractEmailInformer extends AbstractInformer {
	@Override
	public String toString() {
		return this.getClass().getName() + "[email=" + email + ", password="
				+ password + ", senderName=" + senderName + ", hostName="
				+ smtpHostAddress + ", smtpPort=" + smtpPort + ", useSslSmtp="
				+ useSslSmtp + ", useTls=" + useTls + ", recipients="
				+ recipients + "]";
	}


	private final String email;
	private final String password;
	private final String senderName;
	private final String smtpHostAddress;
	private final int smtpPort;
	private final boolean useSslSmtp;
	private final boolean useTls;

	
	private final Collection<String> recipients;


	public AbstractEmailInformer(String id, String email, String password,
			String senderName, String smtpHostAddress, int smtpPort,
			boolean useSslSmtp, boolean useTls, Collection<String> recipients) {
		super(id);
		this.email = email;
		this.password = password;
		this.senderName = senderName;
		this.smtpHostAddress = smtpHostAddress;
		this.smtpPort = smtpPort;
		this.useSslSmtp = useSslSmtp;
		this.useTls = useTls;
		this.recipients = recipients;
	}


	@Override
	public void inform(Info message) throws Exception {
		if (recipients == null || recipients.isEmpty()) {
			throw new Exception("Recipients are empty");
		}
		try {
			EmailUtils.SendEmail(email, password, senderName, recipients, message.getFullDescription(),
					message.getShortMessage(), smtpHostAddress, smtpPort, useSslSmtp, useTls);
		} catch (EmailException e) {
			GlobalLogger.error("Error sending email: ", e);
			throw new Exception(e);
		}
	}
}
