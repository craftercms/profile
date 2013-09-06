package org.craftercms.profile.services;

import java.util.Map;

import org.craftercms.profile.exceptions.MailException;

public interface MailService {

    void sendMailTLS(String subject, String text, String template, Map<String, Object> templateArgs,
                     String toAddress, String fromAddress) throws MailException;

}
