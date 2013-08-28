package org.craftercms.profile.controllers.rest;

import java.text.ParseException;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.ExpiryDateException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.services.PasswordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Manage the password services request
 * 
 * @author Alvaro Gonzalez
 *
 */
@Controller
@RequestMapping("/api/2/password")
public class PasswordRestController {
	
	@Autowired
	private PasswordService passwordService;
	
	/**
	 * This service will send an email to the user profile owner so that the password could be changed.
	 * 
	 * @param request Current HTTP request
	 * 
	 * @param changePasswordUrl The url that will be added to the email so that the user will use it to change the password
	 * 
	 * @param username that forgot the password.
	 * 
	 * @param tenantName current tenant name
	 *  
	 * @throws AuthenticationException If an authentication error ocurred
	 * 
	 * @throws CipherException If an error ocurred during the encryption process.
	 * 
	 * @throws MailException If an error ocurred during the emailing process
	 * 
	 * @throws NoSuchProfileException If there is not a username registered with the value sent as argument.
	 */
	@RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
	@ModelAttribute
	public void forgotPassword(HttpServletRequest request, @RequestParam(required = true)String changePasswordUrl, String username, String tenantName) throws AuthenticationException, CipherException, MailException, NoSuchProfileException {
		passwordService.forgotPassword(changePasswordUrl, username, tenantName);
	}
	
	/**
	 * Updates the password with the new value sent by argument.
	 * 
	 * @param request Current request
	 * 
	 * @param token Security token sent to the email account of the profile account. This token contain username, tenantName and Date that was generated
	 * 
	 * @param password Clear text new password value
	 * 
	 * @throws AuthenticationException If authentication error occurred
	 * 
	 * @throws CipherException If an error occurred during the decryption process.
	 * 
	 * @throws NoSuchProfileException If the profile was not found in the database
	 * 
	 * @throws ParseException If an error occurred when the date of the token is parsed 
	 * 
	 * @throws ExpiryDateException If the token has already expired
	 */
	@RequestMapping(value = "/change-password", method = RequestMethod.POST)
	@ModelAttribute
	public void changePassword(HttpServletRequest request, String token, String newPassword) throws AuthenticationException, CipherException, MailException, NoSuchProfileException, ParseException, ExpiryDateException {
		passwordService.changePassword(newPassword, token);
	}

}
