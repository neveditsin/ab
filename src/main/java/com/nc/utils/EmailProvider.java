package com.nc.utils;

import java.util.HashMap;
import java.util.Map;

public enum EmailProvider {
	GMAIL("gmail", "imap.gmail.com", "INBOX", "smtp.gmail.com", 587, false, true),
	OUTLOOK_OFFICE365("outlook", "outlook.office365.com", "INBOX", "smtp.office365.com", 587, false, true),
	YANDEX("yandex", "imap.yandex.ru", "INBOX", "smtp.yandex.ru", 465, true, false);

	private static final Map<String, EmailProvider> MAP = new HashMap<>();
	private final String name;
	static {
		for (EmailProvider e : EmailProvider.values()) {
			MAP.put(e.name, e);
		}
	}	
	public static EmailProvider fromString(String emailProvider) {
		return MAP.get(emailProvider.toLowerCase());
	}	
	
	
	
	public String getImapHostAddress() {
		return imapHostAddress;
	}


	public String getInboxFolderName() {
		return inboxFolderName;
	}


	public String getSmtpHostAddress() {
		return smtpHostAddress;
	}


	public int getSmtpPort() {
		return smtpPort;
	}


	public boolean isSmtpUseSsl() {
		return smtpUseSsl;
	}


	public boolean isSmtpUseTls() {
		return smtpUseTls;
	}



	private final String imapHostAddress;
	private final String inboxFolderName;
	private final String smtpHostAddress;
	private final int smtpPort;
	private final boolean smtpUseSsl;
	private final boolean smtpUseTls;
	
	
	private EmailProvider(String name, String imapHostAddress, String inboxFolderName, String smtpHostAddress,
			int smtpPort, boolean smtpUseSsl, boolean smtpUseTls) {
		this.name = name;
		this.imapHostAddress = imapHostAddress;
		this.inboxFolderName = inboxFolderName;
		this.smtpHostAddress = smtpHostAddress;
		this.smtpPort = smtpPort;
		this.smtpUseSsl = smtpUseSsl;
		this.smtpUseTls = smtpUseTls;
	}


	

}
