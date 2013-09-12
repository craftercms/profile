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
import org.craftercms.profile.security.util.crypto.ProfileCipher;
import org.craftercms.profile.security.util.crypto.CipherPasswordChangeToken;
import org.craftercms.profile.services.MailService;
import org.craftercms.profile.services.PasswordService;
import org.craftercms.profile.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

//import org.apache.log4j.Logger;

/**
 * Implements the functionality for password management.
 *
 * @author Alvaro Gonzalez
 */
@Service
public class PasswordServiceImpl implements PasswordService {

    protected final Log log = LogFactory.getLog(getClass());

    private String profileCipherKey;

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
        //ProfileCipher profileCipher = new SimpleDesCipher(profileCipherKey);
        String encryptedToken = cipherPasswordChangeToken.encrypt(getRawValue(tenantName, username));

        try {
            encryptedToken = URLEncoder.encode(encryptedToken, "UTF-8");
        } catch (UnsupportedEncodingException e) {
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

    private String getRawValue(String tenantName, String username) {
        return tenantName + ProfileCipher.DELIMITER + username + ProfileCipher.DELIMITER + DateFormat
            .getDateTimeInstance().format(new Date());
    }

    private String[] splitTokens(String tokens) {
        String[] data = new String[3];
        String[] result = StringUtils.split(tokens, ProfileCipher.DELIMITER);
        data[0] = result[0];
        result = StringUtils.split(result[1], ProfileCipher.DELIMITER);
        data[1] = result[0];
        if (result.length > 1) {
            data[2] = result[1];
        }
        return data;
    }

    /*
     * (non-Javadoc)
     * @see org.craftercms.profile.services.PasswordService#changePassword(java.lang.String, java.lang.String)
     */
    @Override
    public Profile resetPassword(String password, String token) throws CipherException, NoSuchProfileException,
        ParseException, ExpiryDateException {
        try {
            token = URLDecoder.decode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CipherException("Unsupported decode error");
        }
        //token = decodeToken(token);
        if (log.isDebugEnabled()) {
            log.debug("Change password process started");
        }
        //ProfileCipher profileCipher = new SimpleDesCipher(profileCipherKey);
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

    private String decodeToken(String token) throws CipherException {
        try {
            return URLDecoder.decode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CipherException("Token " + token + " has unsupported encoding");
        }
    }

    @Value("#{ssrSettings['crafter.profile.cipher.key']}")
    public void setProfileCipherKey(String key) {
        this.profileCipherKey = key;
    }

    @Value("#{ssrSettings['crafter.profile.password.reset.mail.ftl']}")
    public void setResetEmailTemplateFtl(String resetEmailTemplate) {
        this.resetEmailTemplate = resetEmailTemplate;
    }

    @Value("#{ssrSettings['crafter.profile.password.reset.mail.subject']}")
    public void setResetEmailSubject(String resetEmailSubject) {
        this.resetEmailSubject = resetEmailSubject;
    }

    @Value("#{ssrSettings['crafter.profile.mail.from.address']}")
    public void setMailFrom(String mailFrom) {
        this.fromAddress = mailFrom;
    }

    @Value("#{ssrSettings['crafter.profile.password.change.token.expiry']}") //TODO: change to minutes
    public void setPasswordChangeTokenExpiry(String value) {
        if (value != null && value.equals("")) {
            this.expiryMinutes = Integer.parseInt(value);
        }
    }

    private boolean isValidTokenDate(String dateStr) throws ParseException {
        Date tokenDate = DateFormat.getDateTimeInstance().parse(dateStr);
        Calendar expiryDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();

        expiryDate.setTime(tokenDate);
        expiryDate.add(Calendar.MINUTE, this.expiryMinutes); //TODO: change to minutes

        return currentDate.before(expiryDate);
    }

}
