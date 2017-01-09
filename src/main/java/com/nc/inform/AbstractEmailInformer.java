package com.nc.inform;

import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import com.nc.utils.GlobalLogger;

public abstract class AbstractEmailInformer extends AbstractInformer {
	@Override
	public String toString() {
		return this.getClass().getName() + "[email=" + email + ", password="
				+ password + ", senderName=" + senderName + ", hostName="
				+ hostName + ", smtpPort=" + smtpPort + ", useSslSmtp="
				+ useSslSmtp + ", useTls=" + useTls + ", recipients="
				+ recipients + "]";
	}


	private final String email;
	private final String password;
	private final String senderName;
	private final String hostName;
	private final int smtpPort;
	private final boolean useSslSmtp;
	private final boolean useTls;

	
	private final List<String> recipients;


	public AbstractEmailInformer(String id, String email, String password,
			String senderName, String hostName, int smtpPort,
			boolean useSslSmtp, boolean useTls, List<String> recipients) {
		super(id);
		this.email = email;
		this.password = password;
		this.senderName = senderName;
		this.hostName = hostName;
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
			Email emailMsg = new SimpleEmail();
			emailMsg.setHostName(hostName);

			emailMsg.setSmtpPort(smtpPort);
			if (useSslSmtp) {
				emailMsg.setSSLOnConnect(true);
				emailMsg.setSslSmtpPort(Integer.toString(smtpPort));
			}

			emailMsg.setAuthenticator(new DefaultAuthenticator(this.email,
					this.password));
			emailMsg.setStartTLSEnabled(useTls);
			emailMsg.setFrom(email, senderName);

			emailMsg.setSubject(message.getShortMessage());
			emailMsg.setMsg(message.getFullDescription());

			for (String r : recipients) {
				emailMsg.addTo(r);
			}

			emailMsg.send();
		} catch (EmailException e) {
			GlobalLogger.error("Error sending email: ", e);
			throw new Exception(e);
		}
	}
}
