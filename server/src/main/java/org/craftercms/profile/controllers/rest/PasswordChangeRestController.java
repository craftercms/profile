package org.craftercms.profile.controllers.rest;

import java.text.ParseException;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.ExpiryDateException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.services.PasswordChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/2/password")
public class PasswordChangeRestController {
	
	@Autowired
	private PasswordChangeService passwordChangeService;
	
	
	@RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
	@ModelAttribute
	public void forgotPassword(HttpServletRequest request, @RequestParam(required = true)String changePasswordUrl, String username, String tenantName) throws AuthenticationException, CipherException, MailException, NoSuchProfileException {
		passwordChangeService.forgotPassword(changePasswordUrl, username, tenantName);
	}
	
	@RequestMapping(value = "/change-password", method = RequestMethod.POST)
	@ModelAttribute
	public void resetPassword(HttpServletRequest request, String token, String newPassword) throws AuthenticationException, CipherException, MailException, NoSuchProfileException, ParseException, ExpiryDateException {
		passwordChangeService.resetPassword(newPassword, token);
	}

}
