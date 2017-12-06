package com.nc.mailbox;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Mailboxes {
	private Mailboxes() {}
	
	private static Map<String, Mailbox> mailboxes = new ConcurrentHashMap<>();
	
	public static Mailbox getMailbox(String mailboxId) {
		return mailboxes.get(mailboxId);		
	}
	
	private static Mailbox buildMailbox(String id,
			String imapHostAddress,
			String inboxFolderName,
			String smtpHostAddress,
			int smtpPort,
			boolean isSmtpUseSsl,
			boolean isSmtpUseTls,
			String emailAddress,
			String senderName,
			String emailPassword
			) {
		Mailbox m = new Mailbox(){

			@Override
			public String getImapHostAddress() {
				return imapHostAddress;
			}

			@Override
			public String getInboxFolderName() {
				return inboxFolderName;
			}

			@Override
			public String getSmtpHostAddress() {
				return smtpHostAddress;
			}

			@Override
			public int getSmtpPort() {
				return smtpPort;
			}

			@Override
			public boolean isSmtpUseSsl() {
				return isSmtpUseSsl;
			}

			@Override
			public boolean isSmtpUseTls() {
				return isSmtpUseTls;
			}

			@Override
			public String getEmailAddress() {
				return emailAddress;
			}

			@Override
			public String getSenderName() {
				return senderName;
			}

			@Override
			public String getEmailPassword() {
				return emailPassword;
			}
			
			@Override
			public String toString() {
				return "Mailbox '" + id + "' [imapHostAddress=" + imapHostAddress + ", inboxFolderName="
						+ inboxFolderName + ", smtpHostAddress=" + smtpHostAddress + ", smtpPort=" + smtpPort
						+ ", isSmtpUseSsl=" + isSmtpUseSsl + ", isSmtpUseTls=" + isSmtpUseTls + ", emailAddress="
						+ emailAddress + ", senderName=" + senderName + ", emailPassword=" + emailPassword + "]";
			}

			@Override
			public String getId() {
				return id;
			}
			
		};
		mailboxes.put(id, m);
		return m;	
	}
	
	
	
	
	public static class MailboxFromProviderBuilder{
		private String id;
		
		private String imapHostAddress;
		private String inboxFolderName;
		
		private String smtpHostAddress;
		private int smtpPort;
		private boolean isSmtpUseSsl;
		private boolean isSmtpUseTls;
		
		private String emailAddress;
		private String senderName;
		private String emailPassword;		
		
		
		public MailboxFromProviderBuilder setId(String id) {
			this.id = id;
			return this;
		}

		@MailboxParameter(xmlName = "email_address")
		public MailboxFromProviderBuilder setEmailAddress(String emailAddress) {
			this.emailAddress = emailAddress;
			return this;
		}


		@MailboxParameter(xmlName = "sender_name")
		public MailboxFromProviderBuilder setSenderName(String senderName) {
			this.senderName = senderName;
			return this;
		}

		@MailboxParameter(xmlName = "password")
		public MailboxFromProviderBuilder setEmailPassword(String emailPassword) {
			this.emailPassword = emailPassword;
			return this;
		}

		public MailboxFromProviderBuilder setProvider(EmailProvider p) {
			this.imapHostAddress = p.getImapHostAddress();
			this.inboxFolderName = p.getInboxFolderName();
			this.smtpHostAddress = p.getSmtpHostAddress();
			this.smtpPort = p.getSmtpPort();
			this.isSmtpUseSsl = p.isSmtpUseSsl();
			this.isSmtpUseTls = p.isSmtpUseTls();
			return this;
		}
		
		
		public Mailbox build() {
			return Mailboxes.buildMailbox(id,
					imapHostAddress,
					inboxFolderName,
					smtpHostAddress,
					smtpPort,
					isSmtpUseSsl,
					isSmtpUseTls,
					emailAddress, 
					senderName,
					emailPassword);
		}

	}
		
	public static class MailboxFromParametersBuilder{
		private String id;
		
		private String imapHostAddress;
		private String inboxFolderName;
		
		private String smtpHostAddress;
		private int smtpPort;
		private boolean isSmtpUseSsl;
		private boolean isSmtpUseTls;
		
		private String emailAddress;
		private String senderName;
		private String emailPassword;		
		
		
		public MailboxFromParametersBuilder setId(String id) {
			this.id = id;
			return this;
		}		
		
		
		@MailboxParameter(xmlName = "smtp_host_address")
		public MailboxFromParametersBuilder setImapHostAddress(String imapHostAddress) {
			this.imapHostAddress = imapHostAddress;
			return this;
		}

		@MailboxParameter(xmlName = "imap_inbox_folder_name")
		public MailboxFromParametersBuilder setInboxFolderName(String inboxFolderName) {
			this.inboxFolderName = inboxFolderName;
			return this;
		}


		@MailboxParameter(xmlName = "smtp_host_address")
		public MailboxFromParametersBuilder setSmtpHostAddress(String smtpHostAddress) {
			this.smtpHostAddress = smtpHostAddress;
			return this;
		}

		@MailboxParameter(xmlName = "smtp_port")
		public MailboxFromParametersBuilder setSmtpPort(String smtpPort) {
			this.smtpPort = Integer.parseInt(smtpPort);
			return this;
		}


		@MailboxParameter(xmlName = "smtp_use_ssl")
		public MailboxFromParametersBuilder setSmtpUseSsl(String isSmtpUseSsl) {
			this.isSmtpUseSsl = Boolean.parseBoolean(isSmtpUseSsl);
			return this;
		}

		@MailboxParameter(xmlName = "smtp_use_tls")
		public MailboxFromParametersBuilder setSmtpUseTls(String isSmtpUseTls) {
			this.isSmtpUseTls = Boolean.parseBoolean(isSmtpUseTls);
			return this;
		}


		@MailboxParameter(xmlName = "email_address")
		public MailboxFromParametersBuilder setEmailAddress(String emailAddress) {
			this.emailAddress = emailAddress;
			return this;
		}


		@MailboxParameter(xmlName = "sender_name")
		public MailboxFromParametersBuilder setSenderName(String senderName) {
			this.senderName = senderName;
			return this;
		}

		@MailboxParameter(xmlName = "password")
		public MailboxFromParametersBuilder setEmailPassword(String emailPassword) {
			this.emailPassword = emailPassword;
			return this;
		}


		
		
		public Mailbox build() {
			return Mailboxes.buildMailbox(id,
					imapHostAddress,
					inboxFolderName,
					smtpHostAddress,
					smtpPort,
					isSmtpUseSsl,
					isSmtpUseTls,
					emailAddress, 
					senderName,
					emailPassword);
		}

	}
		
		
		
		

	
		

}
