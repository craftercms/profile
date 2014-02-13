package org.craftercms.profile.services.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.ExpiryDateException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.exceptions.ProfileException;
import org.craftercms.profile.security.util.crypto.CipherPasswordChangeToken;
import org.craftercms.profile.services.MailService;
import org.craftercms.profile.services.PasswordService;
import org.craftercms.profile.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Implements the functionality for password management.
 *
 * @author Alvaro Gonzalez
 */
@Service
public class PasswordServiceImpl implements PasswordService {

    protected final Log log = LogFactory.getLog(getClass());
    @Autowired
    private ProfileService profileService;

    @Autowired
    private MailService mailService;

    private String resetEmailSubject;

    private String resetEmailTemplate;

    private String fromAddress;

    private int expiryMinutes = 60;

    @Autowired
    private CipherPasswordChangeToken cipherPasswordChangeToken;

    /*
     * (non-Javadoc)
     * @see org.craftercms.profile.services.PasswordService#forgotPassword(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public Profile forgotPassword(String changePasswordUrl, String username,
                                  String tenantName) throws CipherException, MailException, NoSuchProfileException {
        if (log.isDebugEnabled()) {
            log.debug("Starting forget process");
        }
        Profile profile = profileService.getProfileByUserName(username, tenantName);
        if (profile == null) {
            log.warn("Profile " + username + " doesn't exist for tenant " + tenantName);
            throw new NoSuchProfileException("Profile " + username + " doesn't exist for tenant " + tenantName);
        }
        if (profile.getEmail() == null || profile.getEmail().equals("")) {
            log.warn("Profile " + username + " doesn't have an email to complete the reset password process.");
            throw new MailException("Profile " + username + " doesn't have an email to complete the reset password " +
                "process.");
        }
        String encryptedToken = cipherPasswordChangeToken.encrypt(getRawValue(tenantName, username));

        try {
            encryptedToken = URLEncoder.encode(encryptedToken, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        	log.warn("Unsupported Ending error");
            throw new CipherException("Unsupported Ending error");
        }
        if (log.isDebugEnabled()) {
            log.debug("Token generated and encrypted");
        }
        String textMail = changePasswordUrl + "?token=" + encryptedToken;
        if (changePasswordUrl != null && changePasswordUrl.endsWith("?")) {
            textMail = changePasswordUrl + "token=" + encryptedToken;
        }
        Map<String, Object> templateArgs = new HashMap<String, Object>();
        templateArgs.put("changePasswordLink", textMail);
        if (log.isDebugEnabled()) {
            log.debug("Mailing the url to change the password");
        }
        mailService.sendMailTLS(resetEmailSubject, textMail, resetEmailTemplate, templateArgs, profile.getEmail(),
            fromAddress);
        if (log.isDebugEnabled()) {
            log.debug("Email sent to change the password");
        }
        return profile;
    }

    /*
     * (non-Javadoc)
     * @see org.craftercms.profile.services.PasswordService#changePassword(java.lang.String, java.lang.String)
     */
    @Override
    public Profile resetPassword(String password, String token) throws CipherException, NoSuchProfileException, ParseException, ExpiryDateException, ProfileException {
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

        String[] data = splitTokens(tokens);
        if (data != null && data.length > 2 && profileService.getProfileByUserName(data[1], data[0]) == null) {
            log.warn("Profile " + data[1] + " doesn't exist for tenant " + data[0]);
            throw new NoSuchProfileException("Profile " + data[1] + " doesn't exist for tenant " + data[0]);
        } else if (data == null || data.length < 2) {
            log.warn("Error decrypting the token " + token);
            throw new CipherException("Error decrypting the token " + token);
        } else if (!isValidTokenDate(data[2])) {
            log.warn("Token date is expired " + data[2]);
            throw new ExpiryDateException("Token date is expired " + data[2]);
        }
        Profile profile = profileService.getProfileByUserNameWithAllAttributes(data[1], data[0]);
        profile = profileService.updateProfile(profile.getId().toString(), profile.getUserName(), password,
            profile.getActive(), profile.getTenantName(), profile.getEmail(), profile.getAttributes(),
            profile.getRoles());
        if (log.isDebugEnabled()) {
            log.debug("Password changed successfuly");
        }
        return profile;
    }

    /**
     * Sets the reset email template that will be used to email the url.
     * 
     * @param resetEmailTemplate
     */
    @Value("#{ssrSettings['crafter.profile.password.reset.mail.ftl']}")
    public void setResetEmailTemplateFtl(String resetEmailTemplate) {
        this.resetEmailTemplate = resetEmailTemplate;
    }

    /**
     * Sets the email subject to be sent whenever an email for resenting the password is sent.
     * 
     * @param resetEmailSubject Subject of the reset email
     */
    @Value("#{ssrSettings['crafter.profile.password.reset.mail.subject']}")
    public void setResetEmailSubject(String resetEmailSubject) {
        this.resetEmailSubject = resetEmailSubject;
    }

    /**
     * Sets the mail account will be used to email the url to reset the password
     * 
     * @param mailFrom Email account used to email the url to reset the password.
     */
    @Value("#{ssrSettings['crafter.profile.mail.from.address']}")
    public void setMailFrom(String mailFrom) {
        this.fromAddress = mailFrom;
    }

    /**
     * Sets the expiration time (minutes) for the token. Default value is 60 minutes
     * 
     * @param value New expiry token in minutes
     */
    @Value("#{ssrSettings['crafter.profile.password.change.token.expiry']}") 
    public void setPasswordChangeTokenExpiry(String value) {
        if (value != null && value.equals("")) {
            this.expiryMinutes = Integer.parseInt(value);
        }
    }

    /**
     * Checks is the token has not expired.
     * 
     * @param dateStr creation date of this token. It shouldn't be older than the expiry time(minutes)
     * 
     * @return <code>true</code> if the dateStr is not older than the expiry value in minutes, <code>false</code> otherwise
     * 
     * @throws ParseException If an error occurs when the date is parsed.
     */
    private boolean isValidTokenDate(String dateStr) throws ParseException {
        Date tokenDate = DateFormat.getDateTimeInstance().parse(dateStr);
        Calendar expiryDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();

        expiryDate.setTime(tokenDate);
        expiryDate.add(Calendar.MINUTE, this.expiryMinutes); 

        return currentDate.before(expiryDate);
    }
    
    private String getRawValue(String tenantName, String username) {
        return tenantName + CipherPasswordChangeToken.SEP + username + CipherPasswordChangeToken.SEP + DateFormat
            .getDateTimeInstance().format(new Date());
    }

    /**
     * Split token using | character to split them
     * 
     * @param tokens Token value
     * 
     * @return the array containing each value of the token
     */
    private String[] splitTokens(String tokens) {
        String[] data = new String[3];
        String[] result = StringUtils.split(tokens, CipherPasswordChangeToken.SEP);
        data[0] = result[0];
        result = StringUtils.split(result[1], CipherPasswordChangeToken.SEP);
        data[1] = result[0];
        if (result.length > 1) {
            data[2] = result[1];
        }
        return data;
    }

}
