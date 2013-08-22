package org.craftercms.profile.management.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.profile.management.model.ForgotPassword;
import org.craftercms.profile.management.model.PasswordChange;
import org.craftercms.profile.management.model.ProfileUserAccountForm;
import org.craftercms.profile.management.services.PasswordChangeService;
import org.craftercms.profile.management.services.impl.ProfileDAOServiceImpl;
import org.craftercms.profile.management.services.impl.ProfileServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PasswordChangeController {
	
	@Autowired
	private PasswordChangeService passwordChangeService;
	
	@Autowired
	private ProfileDAOServiceImpl profileDAOServiceImpl;
	
	
	@RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
    public ModelAndView forgotPassword() {
		 ModelAndView mav = new ModelAndView();
		 
		 ForgotPassword forgoter = new ForgotPassword();
		 
		 mav.setViewName("forgotpassword");
	     mav.addObject("forgoter", forgoter);
		 
		 return mav;
    }
	
	@RequestMapping(value = "/forgeting-password", method = RequestMethod.POST)
    public ModelAndView forgotPassword(@ModelAttribute("forgoter") ForgotPassword forgoter,
    		BindingResult bindingResult, Model model) throws AppAuthenticationFailedException {
		 ModelAndView mav = new ModelAndView();
		 validateForgetPassword(forgoter,bindingResult);
		 if (!bindingResult.hasErrors()) {
			 passwordChangeService.forgotPassword(forgoter.getTenantName(), forgoter.getUsername());
			 Profile p = this.profileDAOServiceImpl.getUser(forgoter.getUsername(), forgoter.getTenantName());
			 mav.addObject("profile", p);
			 mav.setViewName("forgetsucceed");
			 return mav;
		 } else {
			 mav.setViewName("forgotpassword");
		     mav.addObject("forgoter", forgoter);
			 return mav;
		 }
    }
	
	

	@RequestMapping(value = "/changepassword", method = RequestMethod.GET)
    public ModelAndView changePassword(@RequestParam(required=false) String token) {
		 ModelAndView mav = new ModelAndView();
		 
		 PasswordChange changer = new PasswordChange();
		 changer.setToken(token);
		 mav.setViewName("changepassword");
	     mav.addObject("changer", changer);
		 
		 return mav;
    }
	
	@RequestMapping(value = "/changing-password", method = RequestMethod.POST)
    public ModelAndView changingPassword(@ModelAttribute("changer") PasswordChange changer,
    		BindingResult bindingResult, Model model) throws AppAuthenticationFailedException {
		 ModelAndView mav = new ModelAndView();
		 validateNewPassword(changer,bindingResult);
		 if (!bindingResult.hasErrors()) {
			 passwordChangeService.changePassword(changer.getToken(), changer.getNewpass());
			 mav.setViewName("login");
			 return mav;
		 } else {
			 mav.setViewName("changepassword");
		     mav.addObject("changer", changer);
			 
			 return mav;
		 }
		 
    }

	
	private void validateNewPassword(PasswordChange changer, BindingResult bindingResult) {
        if (!changer.getNewpass().equals(changer.getConfirmPass())) {
            bindingResult.rejectValue("newpass", "user.validation.fields.errors.change.password", null, "user.validation.fields.errors.change.password");
        }
        if (changer.getNewpass()==null || changer.getNewpass().isEmpty()) {
			bindingResult.rejectValue("newpass", "user.validation.error.empty.or.whitespace", null, "user.validation.error.empty.or.whitespace");
		}
		if (changer.getConfirmPass()==null || changer.getConfirmPass().equals("")) {
			bindingResult.rejectValue("confirmPass", "user.validation.error.empty.or.whitespace", null, "user.validation.error.empty.or.whitespace");
		}
        
    }
	
	private void validateForgetPassword(ForgotPassword forgoter,
			BindingResult bindingResult) throws AppAuthenticationFailedException {
		if (forgoter.getTenantName()==null || forgoter.getTenantName().equals("")) {
			bindingResult.rejectValue("tenantName", "user.validation.error.empty.or.whitespace", null, "user.validation.error.empty.or.whitespace");
		}
		if (forgoter.getUsername()==null || forgoter.getUsername().equals("")) {
			bindingResult.rejectValue("username", "user.validation.error.empty.or.whitespace", null, "user.validation.error.empty.or.whitespace");
		}
		if (!bindingResult.hasErrors()) {
			Profile p = this.profileDAOServiceImpl.getUser(forgoter.getUsername(), forgoter.getTenantName());
			if (p == null) {
				bindingResult.rejectValue("username", "forgot.validation.fields.errors.user.no.exist", null, "forgot.validation.fields.errors.user.no.exist");
			} else if (p.getEmail() == null || p.getEmail().isEmpty()) {
				bindingResult.rejectValue("username", "forgot.validation.fields.errors.email.no.exist", null, "forgot.validation.fields.errors.user.no.exist");
			}
		}
		
	}
}
