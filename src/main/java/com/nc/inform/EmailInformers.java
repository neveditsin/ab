package com.nc.inform;

import java.util.List;

import javax.naming.ConfigurationException;

import com.nc.utils.EmailProvider;

public class EmailInformers {
	public static Informer getEmailInformer(String provider, String id, String email, String password,
			String senderName, List<String> recipients) throws ConfigurationException {

		EmailProvider p = EmailProvider.fromString(provider);
		if (p == null) {
			throw new ConfigurationException(
					"Email provider \"" + provider + "\" is not recognized. Please check the configuration");
		}

		return new ConfigurableEmailInformer(id, email, password, senderName, p.getSmtpHostAddress(), p.getSmtpPort(),
				p.isSmtpUseSsl(), p.isSmtpUseTls(), recipients);

	}
}
