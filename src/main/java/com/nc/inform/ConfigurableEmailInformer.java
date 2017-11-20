package com.nc.inform;

import java.util.List;

public class ConfigurableEmailInformer extends AbstractEmailInformer {

	public ConfigurableEmailInformer(String id, String email, String password,
			String senderName, String smptHostAddress, int smtpPort,
			boolean useSslSmtp, boolean useTls, List<String> recipients) {

		super(id, email, password, senderName, smptHostAddress, smtpPort, useSslSmtp,
				useTls, recipients);
	}

}
