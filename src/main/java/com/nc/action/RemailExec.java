package com.nc.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.nc.events.Event;
import com.nc.events.Event.EventType;
import com.nc.utils.EmailUtils;
import com.nc.utils.EmailUtils.IncomingEmailMessage;
import com.nc.utils.EmailUtils.MailBoxIsEmptyException;
import com.nc.utils.GlobalLogger;

public class RemailExec implements Action {
	public RemailExec(String email, String password, String imapHostAddress, String inboxFolderName, String trustedSenderEmail,
			String smtpSenderName, String requiredSubject, String smtpHostAddress, int smtpPort, boolean smtpUseSsl, boolean smtpUseTls) {
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
		this.requiredSubject = requiredSubject;
	}

	private final String email;	
	private final String password;	
	private final String imapHostAddress;
	private final String inboxFolderName;	
	private final String trustedSenderEmail;
	private final String requiredSubject;
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
			IncomingEmailMessage m = null;

			try {
				m = EmailUtils.ReadLastMessageAndDelete(email, password, imapHostAddress, inboxFolderName, true);
			} catch (MailBoxIsEmptyException empty) {
				// nothing to do
				return new Event(EventType.SUCCESS);
			} 

		
			// if sender is invalid, do not send any response, just ignore the message
			if (false == checkSender(trustedSenderEmail, m.getSender())) {
				GlobalLogger.warning("INVALID SENDER: " + m.getSender());
				return new Event(EventType.FAILURE, "INVALID SENDER");
			}


			// skip the message
			if (m.getSubject() == null || !m.getSubject().equals(requiredSubject)) {
				GlobalLogger.warning("MESSAGE SKIPPED: Invalid Subject");
				EmailUtils.SendEmail(email, password, smtpSenderName,
						Arrays.asList(new String[] { trustedSenderEmail }), "Invalid Subject",
						requiredSubject + ":RESP", smtpHostAddress, smtpPort, smtpUseSsl, smtpUseTls);
				return new Event(EventType.FAILURE, "Invalid Subject");
			}

			String message = m.getMessage();
			Map<String, String> paramMap;
			
			try {
				paramMap = parseMessage(message);
			}
			catch(Exception e) {
				GlobalLogger.error(e.toString());
				EmailUtils.SendEmail(email, password, smtpSenderName,
						Arrays.asList(new String[] { trustedSenderEmail }), e.toString(),
						requiredSubject + ":RESP", smtpHostAddress, smtpPort, smtpUseSsl, smtpUseTls);
				return new Event(EventType.EXCEPTION, e.toString());
			}


			LocalExec le = new LocalExec(paramMap.get(FIELD_COMMAND), -1L, -1L,
					Boolean.parseBoolean(paramMap.getOrDefault(FIELD_IS_DAEMON, "false")));
			Event result = le.exec();

			EmailUtils.SendEmail(email, password, smtpSenderName, Arrays.asList(new String[] { trustedSenderEmail }),
					result.toString(), requiredSubject + ":RESP", smtpHostAddress, smtpPort, smtpUseSsl, smtpUseTls);

			return result;
		} catch (Exception e) {
			GlobalLogger.error(e.toString());
			return new Event(EventType.EXCEPTION, e.toString());
		}
	}

	private boolean checkSender(String trustedSender, String sender) {
		return sender.matches(String.format("^.*<%s>$", trustedSender));
	}

	private Map<String, String> parseMessage(String message) throws Exception {
		try {
			Map<String, String> paramsMap = new HashMap<>();

			JSONObject jo = new JSONObject(preprocess(message));

			MESSAGE_FIELDS.forEach(mf -> {
				if (jo.has(mf)) {
					paramsMap.put(mf, jo.getString(mf));
				}
			});

			// check mandatory fields
			if (!paramsMap.containsKey(FIELD_COMMAND)) {
				throw new Exception("Message does not contain the mandatory field 'command'");
			}

			return paramsMap;
		} catch (JSONException e) {
			GlobalLogger.error(e.toString());
			throw new JSONException("Message Malformed: " + message + "\nException: " + e.toString());
		}
	}
	
	private static String preprocess(String s) {
		//replace all html tags if present		
		
		return s.replaceAll("</?div>", "")
		        .replaceAll("</?br ?/?>", "\n")
		        .replaceAll("(?s)<html>.*</html>","")
		        .trim();	
	}
		
}
