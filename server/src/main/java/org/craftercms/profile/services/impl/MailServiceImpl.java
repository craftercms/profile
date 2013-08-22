package org.craftercms.profile.services.impl;

import java.util.Map;

import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.services.MailService;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class MailServiceImpl implements MailService {

	//private static final Logger log = Logger.getLogger(MailServiceImpl.class);

	private String mailSmtpAuth;
	private String mailSmtpStarttlsEnable;
	private String mailSmtpHost;
	private String mailSmtpPort;
	private String mailUsername;
	private String mailPassword;

	private String pathToTemplates;

	private String mailFrom;
	

	/* (non-Javadoc)
	 * @see com.mastercard.prehire.service.api.MailService#sendMailTLS(java.lang.String, java.lang.String, java.lang.String, java.util.Map, java.lang.String, java.lang.String)
	 */
	public void sendMailTLS(String subject, String text, String template,  Map<String, Object> templateArgs, 
			String toAddress, String fromAddress) throws MailException {

		Properties props = new Properties();
		props.put("mail.smtp.auth", mailSmtpAuth);
		props.put("mail.smtp.starttls.enable", mailSmtpStarttlsEnable);
		props.put("mail.smtp.host", mailSmtpHost);
		props.put("mail.smtp.port", mailSmtpPort);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailUsername,
								mailPassword);
					}
				});

		try {

			//log.info("Sending email to " + toAddress + ", with subject "
			//		+ subject + ", and text " + text);

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress == null ? mailFrom : fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(toAddress));
			message.setSubject(subject);
			
			if (StringUtils.isNotEmpty(template)) {
				// get the template as a string
				String templateAsString = getTemplateAsString(template);
				
				// read template from file system 
				String output = process(new Configuration(), templateAsString, templateArgs);
				message.setContent(output, "text/html");
				
			} else {
				message.setText(text);
			}
			
			Transport.send(message);

		} catch (MessagingException e) {
			//log.info("Error thrown sending email: " + e);
			throw new RuntimeException(e);
		}

	}
	

	
	/* Get the template as a string from the filename passed in
	 * 
	 * @param template
	 * @return
	 * @throws MessagingException
	 */
	private String getTemplateAsString(String template) throws MessagingException {
		
		// get the template as a string 
		InputStream inputStream = this.getClass().getResourceAsStream(
				new StringBuilder("/").append(pathToTemplates).append("/").append(template).toString());
		
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(inputStream, writer, "UTF-8");
		} catch (IOException e) {
			//log.error("Error reading template " + template, e);
			throw new MessagingException("Error reading template " + template, e);
		}
		
		return writer.toString();
	}

	/* Process the template passed in
	 * 
	 * @param cfg
	 * @param template
	 * @param input
	 * @return
	 */
	private String process(final Configuration cfg, final String template, final Object input) {
        String rc = null;
 
       try {
           final Template temp = new Template("TemporaryTemplate", new StringReader(template), cfg);
 
           final Writer out = new StringWriter();
	       temp.process(input, out);
	       rc = out.toString();
	       out.close();
       }
       catch (final Exception e) {
           //log.error("Problem",e);
       }
 
       return rc;
    }
	
	@Value("#{ssrSettings['mail.smtp.auth']}")
	public void setMailSmtpAuth(String mailSmtpAuth) {
		this.mailSmtpAuth = mailSmtpAuth;
	}

	@Value("#{ssrSettings['mail.smtp.starttls.enable']}")
	public void setMailSmtpStarttlsEnable(String mailSmtpStarttlsEnable) {
		this.mailSmtpStarttlsEnable = mailSmtpStarttlsEnable;
	}

	@Value("#{ssrSettings['mail.smtp.host']}")
	public void setMailSmtpHost(String mailSmtpHost) {
		this.mailSmtpHost = mailSmtpHost;
	}

	@Value("#{ssrSettings['mail.smtp.port']}")
	public void setMailSmtpPort(String mailSmtpPort) {
		this.mailSmtpPort = mailSmtpPort;
	}

	@Value("#{ssrSettings['mail.username']}")
	public void setMailUsername(String mailUsername) {
		this.mailUsername = mailUsername;
	}

	@Value("#{ssrSettings['mail.password']}")
	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}
	
	@Value("#{ssrSettings['path.to.templates']}")
	public void setPathToTemplates(String pathToTemplates) {
		this.pathToTemplates = pathToTemplates;
	}

	@Value("#{ssrSettings['mail.from']}")
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

}
