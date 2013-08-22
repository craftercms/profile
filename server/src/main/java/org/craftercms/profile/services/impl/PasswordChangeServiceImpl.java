package org.craftercms.profile.services.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.ExpiryDateException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.security.util.crypto.ProfileCipher;
import org.craftercms.profile.services.MailService;
import org.craftercms.profile.services.PasswordChangeService;
import org.craftercms.profile.services.ProfileService;
import org.craftercms.profile.security.util.crypto.impl.SimpleDesCipher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PasswordChangeServiceImpl implements PasswordChangeService {
	
	private String profileCipherKey;
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private MailService mailService;
	
	private String emailSubject;
	
	private String emailTemplate;
	private String emailText;
	
	private String fromAddress;
	
	private int expiryDays = 3;
	
	@Override
	public void forgotPassword(String changePasswordUrl, String username, String tenantName) throws CipherException, MailException, NoSuchProfileException {
		Profile profile = profileService.getProfileByUserName(username, tenantName);
		if (profile == null) {
			throw new NoSuchProfileException("Profile " + username + " was not found");
		}
		ProfileCipher profileCipher = new SimpleDesCipher(profileCipherKey);
		 
		String encryptedToken = profileCipher.encrypt(getRawValue(tenantName, username).getBytes());
		String textMail = changePasswordUrl + "?token=" + encryptedToken;
		if (changePasswordUrl != null && changePasswordUrl.endsWith("?")) {
			textMail = changePasswordUrl + "token=" + encryptedToken;
		} 
		Map<String, Object> templateArgs = new HashMap<String, Object>();
		templateArgs.put("changePasswordLink", textMail);
		mailService.sendMailTLS(emailSubject, textMail, emailTemplate, templateArgs, profile.getEmail(), fromAddress);

	}

	private String getRawValue(String tenantName, String username) {
		return tenantName + ProfileCipher.DELIMITER + username + ProfileCipher.DELIMITER + DateFormat.getDateTimeInstance().format(new Date());
	}
	
	private String[] splitTokens(String tokens) {
		String[] data = new String[3];
		String[] result = StringUtils.split(tokens, ProfileCipher.DELIMITER);
		data[0] = result[0];
		result = StringUtils.split(result[1], ProfileCipher.DELIMITER);
		data[1] = result[0];
		if (result.length > 1)
			data[2] = result[1];
		return data;
	}

	@Override
	public void resetPassword(String password, String token) throws CipherException, NoSuchProfileException, ParseException, ExpiryDateException {
		ProfileCipher profileCipher = new SimpleDesCipher(profileCipherKey);
		// tenant | username | date
		String tokens = profileCipher.decrypt(token);
		
		String[] data = splitTokens(tokens);
		if (data != null && data.length > 2 && profileService.getProfileByUserName(data[1], data[0]) == null) {
			throw new NoSuchProfileException("Profile " + data[1] + " was not found");
		} else if (data == null || data.length < 2) {
			throw new CipherException("Error decrypting the token");
		} else if (!isValidTokenDate(data[2])) {
			throw new ExpiryDateException("Token date is expired.");
		}
		Profile profile = profileService.getProfileByUserNameWithAllAttributes(data[1], data[0]);
		profileService.updateProfile(profile.getId().toString(), profile.getUserName(), password, profile.getActive(), profile.getTenantName(), profile.getEmail(), profile.getAttributes(), profile.getRoles());
	}
	
	@Value("#{ssrSettings['crafter.profile.cipher.key']}")
    public void setProfileCipherKey(String key) {
    	this.profileCipherKey = key;
    }
    
    @Value("#{ssrSettings['crafter.profile.password.reset.mail.ftl']}")
	public void setPasswordResetFtl(String passwordResetFtl) {
		this.emailTemplate = passwordResetFtl;
	}

	@Value("#{ssrSettings['crafter.profile.password.reset.mail.subject']}")
	public void setPasswordResetSubject(String passwordResetSubject) {
		this.emailSubject = passwordResetSubject;
	}
	@Value("#{ssrSettings['crafter.profile.mail.from.address']}")
	public void setMailFrom(String mailFrom) {
		this.fromAddress = mailFrom;
	}
	
	@Value("#{ssrSettings['crafter.profile.password.change.token.expiry']}")
	public void setPasswordChangeTokenExpiry(String value) {
		if (value != null && value.equals("")) {
			this.expiryDays = Integer.parseInt(value);
		}
	}
	
	private boolean isValidTokenDate(String dateStr) throws ParseException {
		Date tokenDate = DateFormat.getDateTimeInstance().parse(dateStr);
		Calendar expiryDate = Calendar.getInstance();
		expiryDate.setTime(tokenDate);
		expiryDate.add(Calendar.DATE, this.expiryDays);
		return new Date().before(expiryDate.getTime());
	}

}
