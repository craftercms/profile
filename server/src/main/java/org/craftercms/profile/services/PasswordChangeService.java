package org.craftercms.profile.services;

import java.text.ParseException;

import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.ExpiryDateException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;

public interface PasswordChangeService {
	
	void resetPassword(String password, String token) throws CipherException, NoSuchProfileException, ParseException, ExpiryDateException;
	void forgotPassword(String changePasswordUrl, String username, String tenantName)
			throws CipherException, MailException, NoSuchProfileException;

}
