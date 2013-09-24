package org.craftercms.profile.services;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.ExpiryDateException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;

/**
 * Manage to verify a new profile account
 * @author Alvaro Gonzalez
 *
 */
public interface VerifyAccountService {
	
	/**
	 * Sends a verification notify to the user so that the account could be verified
	 * 
	 * @param profile The profile account is going to be verify
	 * 
	 * @param verifyAccountUrl The verification url
	 * 
	 * @param request Current Request
	 * 
	 * @throws CipherException If a cipher error occurs
	 * 
	 * @throws MailException If an email error occurs 
	 * 
	 * @throws NoSuchProfileException If there are not profile information
	 */
	void sendVerifyNotification(Profile profile, String verifyAccountUrl, HttpServletRequest request) throws CipherException, MailException, NoSuchProfileException;
	
	/**
	 * Verifies a user account. It uses the token to get the profile username and token and then the account is activated
	 * 
	 * @param token The encription token used to verify the account
	 * 
	 * @return The profile account verified
	 * 
	 * @throws CipherException If a cipher error occurs
	 * 
	 * @throws NoSuchProfileException If there is not such profile account
	 * @throws ExpiryDateException 
	 * @throws ParseException 
	 * 
	 */
	Profile verifyAccount(String token) throws CipherException, NoSuchProfileException, ParseException, ExpiryDateException;

}
