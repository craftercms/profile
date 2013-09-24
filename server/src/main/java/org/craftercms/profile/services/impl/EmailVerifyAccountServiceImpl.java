package org.craftercms.profile.services.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.ExpiryDateException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.security.util.crypto.CipherPasswordChangeToken;
import org.craftercms.profile.services.MailService;
import org.craftercms.profile.services.ProfileService;

import org.craftercms.profile.services.VerifyAccountService;
import org.craftercms.profile.util.TokenHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Encapsulate the email verification account
 * @author Alvaro Gonzalez
 *
 */
@Service
public class EmailVerifyAccountServiceImpl implements VerifyAccountService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private static final String VERIFY_EMAIL_SUBJECT = "Crafter Profile Verification Account";
	private static final String VERIFY_EMAIL_TEMPLATE = "verify-email.ftl";
	private static final String VERIFY_EMAIL_FROM = "craftercmsprofile@gmail.com";
	private static final String VERIFY_DEFAULT_BASE_URL = "/verify-account";
	private static final int VERIFY_EXPIRY_TIME = 60;
    
    @Autowired
    private MailService mailService;
    @Autowired
    private ProfileService profileService;

    private String verifyEmailSubject;

    private String verifyEmailTemplate;
    
    private String verifyDefaultUrl;

    private String fromAddress;

    private int expiryMinutes;

    @Autowired
    private CipherPasswordChangeToken cipherPasswordChangeToken;
    
    public EmailVerifyAccountServiceImpl() {
    	verifyEmailSubject = VERIFY_EMAIL_SUBJECT;
    	verifyEmailTemplate = VERIFY_EMAIL_TEMPLATE;
    	verifyDefaultUrl = VERIFY_DEFAULT_BASE_URL;
    	fromAddress = VERIFY_EMAIL_FROM;
    	expiryMinutes = VERIFY_EXPIRY_TIME;
    }

	/* (non-Javadoc)
	 * @see org.craftercms.profile.services.VerifyAccountService#sendVerifyNotification(org.craftercms.profile.domain.Profile, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void sendVerifyNotification(Profile profile, String verifyAccountUrl, HttpServletRequest request) throws CipherException, MailException, NoSuchProfileException {
		if (log.isDebugEnabled()) {
            log.debug("Starting verification process");
        }
		if (profile == null) {
			log.warn("Profile instance is required to send the verification email");
            throw new NoSuchProfileException("Profile is required to send the verification email");
		}
		String encryptedToken = cipherPasswordChangeToken.encrypt(TokenHelper.getInstance().getRawValue(profile.getTenantName(), profile.getUserName()));
		
		try {
            encryptedToken = URLEncoder.encode(encryptedToken, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        	log.warn("Unsupported Ending error");
            throw new CipherException("Unsupported Ending error");
        }
        if (log.isDebugEnabled()) {
            log.debug("Token generated and encrypted");
        }
        String textMail = addTokenToUrl(verifyAccountUrl, encryptedToken, request);
        
        Map<String, Object> templateArgs = new HashMap<String, Object>();
        templateArgs.put("verificationLink", textMail);
        if (log.isDebugEnabled()) {
            log.debug("Mailing the url to verify the account");
        }
        mailService.sendMailTLS(verifyEmailSubject, textMail, verifyEmailTemplate, templateArgs, profile.getEmail(),
            fromAddress);
        if (log.isDebugEnabled()) {
            log.debug("Email sent to verify the account");
        }
		
	}
	
	/* (non-Javadoc)
	 * @see org.craftercms.profile.services.VerifyAccountService#verifyAccount(java.lang.String)
	 */
	@Override
	public Profile verifyAccount(String token) throws CipherException,
			NoSuchProfileException, ParseException, ExpiryDateException {
		
		try {
            token = URLDecoder.decode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        	log.warn("Unsupported decode error UTF-8");
            throw new CipherException("Unsupported decode error");
        }
        if (log.isDebugEnabled()) {
            log.debug("Change password process started");
        }
        // tenant | username | date
        String tokens = cipherPasswordChangeToken.decrypt(token);

        String[] data = TokenHelper.getInstance().splitTokens(tokens);
        if (data == null || data.length < 2) {
            log.warn("Error decrypting the token " + token);
            throw new CipherException("Error decrypting the token " + token);
        } else if (!TokenHelper.getInstance().isValidTokenDate(data[2],this.expiryMinutes)) {
            log.warn("Token date is expired " + data[2]);
            throw new ExpiryDateException("Token date is expired " + data[2]);
        }
        Profile profile = profileService.getProfileByUserNameWithAllAttributes(data[1], data[0]);
        if (profile == null) {
        	log.warn("Profile " + data[1] + "was not found for tenant " + data[0]);
            throw new NoSuchProfileException("Profile " + data[1] + "was not found for tenant " + data[0]);
        }
        
        profile.setVerify(true);
        profile.setActive(true);
        profile = profileService.updateProfile(profile);
        return profile;
		
	}
	
	private String addTokenToUrl(String verifyAccountUrl, String encryptedToken, HttpServletRequest request) {
		String url = verifyAccountUrl;
		if (verifyAccountUrl == null || verifyAccountUrl.equals("")) {
			url = TokenHelper.getInstance().createBaseUrl(request, this.verifyDefaultUrl);
		}
		String textMail = url + "?token=" + encryptedToken;
        if (url.endsWith("?")) {
            textMail = url + "token=" + encryptedToken;
        }
        return textMail;
	}

	/**
     * Sets the reset email template that will be used to email the url.
     * 
     * @param resetEmailTemplate
     */
    @Value("#{ssrSettings['crafter.profile.verify.mail.ftl']}")
    public void setVerifyEmailTemplateFtl(String verifyEmailTemplate) {
    	if (verifyEmailSubject != null && verifyEmailSubject.equals("")) {
    		this.verifyEmailTemplate = verifyEmailTemplate;
    	}
    }

    /**
     * Sets the email subject to be sent whenever an email for resenting the password is sent.
     * 
     * @param resetEmailSubject Subject of the reset email
     */
    @Value("#{ssrSettings['crafter.profile.verify.mail.subject']}")
    public void setVerifyEmailSubject(String verifyEmailSubject) {
    	if (verifyEmailSubject != null && verifyEmailSubject.equals("")) {
    		this.verifyEmailSubject = verifyEmailSubject;
    	}
    }

    /**
     * Sets the mail account will be used to email the url to reset the password
     * 
     * @param mailFrom Email account used to email the url to reset the password.
     */
    @Value("#{ssrSettings['crafter.profile.mail.from.address']}")
    public void setMailFrom(String mailFrom) {
    	if (mailFrom != null && mailFrom.equals("")) {
    		this.fromAddress = mailFrom;
    	}
    }

    /**
     * Sets the expiration time (minutes) for the token. Default value is 60 minutes
     * 
     * @param value New expiry token in minutes
     */
    @Value("#{ssrSettings['crafter.profile.verify.token.expiry']}") 
    public void setPasswordChangeTokenExpiry(String value) {
        if (value != null && value.equals("")) {
            this.expiryMinutes = Integer.parseInt(value);
        }
    }

	

}
