package com.nc.utils;


import java.io.IOException;
import java.util.*;

import javax.mail.*;
import javax.mail.Flags.Flag;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;


public class EmailUtils {
	private static final String CONTENT_TEXTPLAIN = "text/plain";

    public static class InvalidContentTypeException extends Exception{
		public InvalidContentTypeException(String contentType) {
			super(String.format("Invalid content type '%s'. text/plain is expected", contentType));
		}

		private static final long serialVersionUID = 6309049819764135248L;    	
    }
    
    public static class IncomingEmailMessage{
    	private final String message;
    	private final Date sentDate;
		private final String subject;
    	private final String sender;
    	
		public IncomingEmailMessage(String message, Date sentDate, String subject, String sender) {
			super();
			this.message = message;
			this.sentDate = sentDate;
			this.subject = subject;
			this.sender = sender;
		} 
		
    	public String getMessage() {
			return message;
		}
		public Date getSentDate() {
			return sentDate;
		}
		public String getSubject() {
			return subject;
		}
		public String getSender() {
			return sender;
		}

		@Override
		public String toString() {
			return "IncomingEmailMessage [message=" + message + ", sentDate=" + sentDate + ", subject=" + subject
					+ ", sender=" + sender + "]";
		}
		
    }
    
	public static IncomingEmailMessage ReadLastMessageAndDelete(String email, 
			String password, 
			String hostName, 
			String inboxFolderName,
			boolean deleteIfContentTypeIsInvalid) throws MessagingException, InvalidContentTypeException, IOException
			  {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getInstance(props, null);
        Store store = session.getStore();
        store.connect(hostName, email, password);
        Folder inbox = store.getFolder(inboxFolderName);
        inbox.open(Folder.READ_WRITE);
        Message msg = inbox.getMessage(inbox.getMessageCount());
   
        //delete unrecognized message and throw exception
        final String contentType = msg.getContentType();
		if (!(contentType.equalsIgnoreCase(CONTENT_TEXTPLAIN) ||
				contentType.contains(CONTENT_TEXTPLAIN))) {
			System.out.println(Arrays.asList(msg.getFrom()).stream().map(a -> a.toString()).reduce("", (s2, s1) -> s2 + "," + s1));
			System.out.println(msg.getSentDate());
			if (deleteIfContentTypeIsInvalid) {
				msg.setFlag(Flag.DELETED, true);
				inbox.expunge();
			}
			throw new InvalidContentTypeException(contentType);
		}
        
		final String from = Arrays.asList(msg.getFrom()).stream().map(a -> a.toString()).reduce("", (s2, s1) -> s2 + "," + s1);
		
		IncomingEmailMessage ret = new IncomingEmailMessage(msg.getContent().toString(), msg.getSentDate(), msg.getSubject(), from);

		msg.setFlag(Flag.DELETED, true);
        inbox.expunge();
        
        return ret;
    }
    
    
	public static void SendEmail(String email,
			String password,
			String senderName, 
			Collection<String> recipients,
			String message,
			String subject,
			String smtpHostAddress, 
			int smtpPort, 
			boolean useSslSmtp,
			boolean useTls 
			) throws EmailException {
		Email emailMsg = new SimpleEmail();
		emailMsg.setHostName(smtpHostAddress);

		emailMsg.setSmtpPort(smtpPort);
		if (useSslSmtp) {
			emailMsg.setSSLOnConnect(true);
			emailMsg.setSslSmtpPort(Integer.toString(smtpPort));
		}

		emailMsg.setAuthenticator(new DefaultAuthenticator(email, password));
		emailMsg.setStartTLSEnabled(useTls);
		emailMsg.setFrom(email, senderName);

		emailMsg.setSubject(subject);
		emailMsg.setMsg(message);

		for (String r : recipients) {
			emailMsg.addTo(r);
		}

		emailMsg.send();
	}
}
