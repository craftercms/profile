package org.craftercms.profile.services;

import java.util.Map;

import org.craftercms.profile.exceptions.MailException;

public interface MailService {

	/**
	 * Send a mail using the configured email settings with the arguments passed
	 * The template will be used for the mail content if it exists, otherwise the text will be
	 * @param subject
	 * @param text
	 * @param template
	 * @param templateArgs
	 * @param toAddress
	 * @param fromAddress
	 * @throws MessagingException
	 */
    void sendMailTLS(String subject, String text, String template, Map<String, Object> templateArgs,
                     String toAddress, String fromAddress) throws MailException;

}
