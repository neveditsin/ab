package com.nc.inform;

import java.util.List;

public class GmailInformar extends AbstractEmailInformer {

	//https://www.google.com/settings/security/lesssecureapps
	public GmailInformar(String id, String email, String password,
			String senderName, List<String> recipients) {
		super(id, 
				email,
				password,
				senderName,
				"smtp.office365.com", 
				587,
				false,
				true,
				recipients);
	}

}
