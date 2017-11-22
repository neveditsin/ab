package com.nc.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.utils.EmailUtils;
import com.nc.utils.EmailUtils.IncomingEmailMessage;
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
	private final static Set<String> MESSAGE_FIELDS;
	static {
		MESSAGE_FIELDS = new HashSet<>();
		MESSAGE_FIELDS.add(FIELD_COMMAND);
	}
	
	
	@Override
	public Event exec() {
		try {
			IncomingEmailMessage m = EmailUtils.ReadLastMessageAndDelete(email, password, imapHostAddress,
					inboxFolderName, true);

			String message = m.getMessage();
			Map<String, String> paramMap;
			paramMap = parseMessage(message);

			LocalExec le = new LocalExec(paramMap.get(FIELD_COMMAND), -1L, -1L, false);
			Event result = le.exec();

			EmailUtils.SendEmail(email, password, smtpSenderName, Arrays.asList(new String[] { trustedSenderEmail }),
					result.getEventInfo(), "RESP", smtpHostAddress, smtpPort, smtpUseSsl, smtpUseTls);
			
			return result;

		} catch (Exception e) {
			return new Event(EventType.EXCEPTION, e.toString());
		}
	}




	private Map<String, String> parseMessage(String message) throws Exception {
		try {
			String[] params = message.split("\\r?\\n");
			Map<String, String> paramsMap = new HashMap<>();
			for(String param : params) {
				String[] pv = param.split(":");
				if(MESSAGE_FIELDS.contains(pv[0].trim().toLowerCase())){
					paramsMap.put(pv[0], pv[1]);
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
