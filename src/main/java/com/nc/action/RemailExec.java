package com.nc.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.mail.EmailException;

import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.utils.EmailUtils;
import com.nc.utils.EmailUtils.IncomingEmailMessage;
import com.nc.utils.EmailUtils.InvalidContentTypeException;
import com.nc.utils.EmailUtils.MailBoxIsEmptyException;
import com.nc.utils.GlobalLogger;

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
	
	private final static String FIELD_COMMAND = "command";
	private final static String FIELD_IS_DAEMON = "is_daemon";
	private final static Set<String> MESSAGE_FIELDS;
	static {
		MESSAGE_FIELDS = new HashSet<>();
		MESSAGE_FIELDS.add(FIELD_COMMAND);
		MESSAGE_FIELDS.add(FIELD_IS_DAEMON);
	}
	
	
	@Override
	public Event exec() {
		try {
			IncomingEmailMessage m;
			try {
				m = EmailUtils.ReadLastMessageAndDelete(email, password, imapHostAddress,
						inboxFolderName, true);
			} catch (MailBoxIsEmptyException | InvalidContentTypeException e1) {
				//nothing to do
				return new Event(EventType.SUCCESS);
			}
			
			if (!m.getSubject().equals("REQ")) {
				//skip the message
				GlobalLogger.info("MESSAGE SKIPPED: SUBJECT INVALID");
				return new Event(EventType.SUCCESS);
			}
			
			if (false == checkSender(trustedSenderEmail, m.getSender())) {
				GlobalLogger.error("INVALID SENDER: " + m.getSender());
				return new Event(EventType.FAILURE, "INVALID SENDER");
			}

			String message = m.getMessage();
			Map<String, String> paramMap;
			paramMap = parseMessage(message);

			LocalExec le = new LocalExec(paramMap.get(FIELD_COMMAND), -1L, -1L,
					Boolean.parseBoolean(paramMap.getOrDefault(FIELD_IS_DAEMON, "false")));
			Event result = le.exec();

			try {
				EmailUtils.SendEmail(email, password, smtpSenderName, Arrays.asList(new String[] { trustedSenderEmail }),
						result.toString(), "RESP", smtpHostAddress, smtpPort, smtpUseSsl, smtpUseTls);
			} catch (EmailException e) {
				GlobalLogger.error(e.toString());
				return new Event(EventType.EXCEPTION, e.toString());
			}

			
			return result;

		} catch (Exception e) {
			return new Event(EventType.EXCEPTION, e.toString());
		}
	}



	private boolean checkSender(String trustedSender, String sender) {
		//TODO
		return true;
	}

	private Map<String, String> parseMessage(String message) throws Exception {
		try {
			String[] params = message.split("\\r?\\n");
			Map<String, String> paramsMap = new HashMap<>();
			for(String param : params) {
				String[] pv = param.split("=");
				String p = pv[0].trim().toLowerCase();
				String v = pv[1].trim();				
				if(MESSAGE_FIELDS.contains(p)){
					paramsMap.put(p, v);
				}
			}

			// check mandatory fields
			if (!paramsMap.containsKey(FIELD_COMMAND)) {
				throw new Exception("Message does not contain the mandatory field 'command'");
			}
			
			return paramsMap;
		} catch (Exception e) {
			GlobalLogger.error(e.toString());
			throw new Exception("Message Malformed: " + message);
		}

	}
	
}
