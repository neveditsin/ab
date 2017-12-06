package com.nc.mailbox;

public interface Mailbox {
	public String getId();
	
	//IMAP-related parameters
	public String getImapHostAddress();
	public String getInboxFolderName();
	
	//SMTP-related parameters
	public String getSmtpHostAddress();
	public int getSmtpPort();
	public boolean isSmtpUseSsl();
	public boolean isSmtpUseTls();
	
	//user parameters
	/*
	 * Example: name@host.com
	 */
	public String getEmailAddress();
	/*
	 * Example: Name Lastname
	 */
	public String getSenderName();
	/*
	 * Your email password
	 */
	public String getEmailPassword();

}
