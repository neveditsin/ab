package com.nc.inform;

import java.util.List;

import javax.naming.ConfigurationException;

public class EmailInformers {
	public static Informer getEmailInformer(String provider, String id,
			String email, String password, String senderName,
			List<String> recipients) throws ConfigurationException {
		switch (provider) {
			case "outlook":
				return new OutlookEmailInformer(id, email, password, senderName,
					recipients);
			case "gmail":
				return new GmailInformar(id, email, password, senderName,
					recipients);
			default:
				throw new ConfigurationException("Email provider \"" + provider
					+ "\" is not recognized. Please check the configuration");
		}
	}
}
