package org.craftercms.profile.management.web;

import javax.servlet.http.HttpServletRequest;

import org.craftercms.profile.client.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.client.impl.domain.Profile;
import org.craftercms.profile.management.model.ForgotPassword;
import org.craftercms.profile.management.model.PasswordChange;
import org.craftercms.profile.management.services.PasswordChangeService;
import org.craftercms.profile.management.services.impl.ProfileDAOServiceImpl;
import org.craftercms.profile.management.services.impl.ProfileServiceManager;
import org.craftercms.security.api.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Change password requests
 *
 * @author Alvaro Gonzalez
 */
@Controller
public class ChangePasswordController {

    @Autowired
    private PasswordChangeService passwordChangeService;

    @Autowired
    private ProfileDAOServiceImpl profileDAOServiceImpl;

    /**
     * Request the forgot password form
     *
     * @return <code>ModelAndView</code> instance.
     */
    @RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
    public ModelAndView forgotPassword() {
        ModelAndView mav = new ModelAndView();

        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setTenantName(ProfileServiceManager.getCrafterProfileAppTenantName());
        forgotPassword.setChangePasswordUrl(passwordChangeService.getCrafterProfileChangePasswordUrl());
        mav.setViewName("forgot-password");
        mav.addObject("forgotPassword", forgotPassword);

        return mav;
    }

    /**
     * Post to manage the forget password request. Validates that the username
     * entered is valid.
     *
     * @param forgotPassword <code>ForgotPassword</code> instance that contains the
     *                       admin-console tenant name and the username entered
     * @param bindingResult  Binding result for <code>ForgotPassword</code> instance
     * @param model          <code>Model</code> instance
     * @return redirect to a status message page
     * @throws AppAuthenticationFailedException
     *          If an appToken exception occurred
     */
    @RequestMapping(value = "/forgeting-password", method = RequestMethod.POST)
    public ModelAndView forgotPassword(@ModelAttribute("forgotPassword") ForgotPassword forgotPassword,
                                       BindingResult bindingResult,
                                       Model model) throws AppAuthenticationFailedException {
        ModelAndView mav = new ModelAndView();
        forgotPassword.setTenantName(ProfileServiceManager.getCrafterProfileAppTenantName());
        validateForgetPassword(forgotPassword, bindingResult);
        if (!bindingResult.hasErrors()) {
            passwordChangeService.forgotPassword(forgotPassword.getTenantName(), forgotPassword.getUsername());
            Profile p = this.profileDAOServiceImpl.getUser(forgotPassword.getUsername(),
                forgotPassword.getTenantName());
            mav.addObject("profile", p);
            mav.setViewName("forgetsucceed");
            return mav;
        } else {
            mav.setViewName("forgotpassword");
            mav.addObject("forgotPassword", forgotPassword);
            return mav;
        }
    }

    /**
     * Post to manage the forget password request. Validates that the username
     * entered is valid.
     *
     * @param forgotPassword <code>ForgotPassword</code> instance that contains the
     *                       admin-console tenant name and the username entered
     * @param bindingResult  Binding result for <code>ForgotPassword</code> instance
     * @param model          <code>Model</code> instance
     * @return redirect to a status message page
     * @throws AppAuthenticationFailedException
     *          If an appToken exception occurred
     */
    @RequestMapping(value = "/forgot-success", method = RequestMethod.GET)
    public ModelAndView forgotPassword(Model model, HttpServletRequest request) throws
        AppAuthenticationFailedException {
        ModelAndView mav = new ModelAndView();
        UserProfile profile = (UserProfile)request.getSession().getAttribute("profileForgotPassword");

        mav.setViewName("forgot-success");
        mav.addObject("profile", profile);
        return mav;
    }

    /**
     * Initial Change password request.
     *
     * @param token . Security token sent by email
     * @return <code>ModelAndView</code> instance with the changepassword form
     */
    @RequestMapping(value = "/reset-password", method = RequestMethod.GET)
    public ModelAndView changePassword(@RequestParam(required = false) String token) {
        ModelAndView mav = new ModelAndView();

        PasswordChange passwordChange = new PasswordChange();
        passwordChange.setToken(token);
        mav.setViewName("reset-password");
        mav.addObject("passwordChange", passwordChange);

        return mav;
    }

    /**
     * Post to manage the change password process.Validates that both new
     * password and confirm password are the same and are not blanket
     *
     * @param <code>PasswordChange</code> instance that contains the new
     *                                    password and the confirm password values
     * @param bindingResult               Binding result for <code>PasswordChange</code> instance
     * @param model                       <code>Model</code> instance
     * @return redirect to login page
     * @throws AppAuthenticationFailedException
     *          If an appToken exception occurred
     */
    @RequestMapping(value = "/changing-password", method = RequestMethod.POST)
    public ModelAndView changingPassword(@ModelAttribute("passwordChange") PasswordChange passwordChange,
                                         BindingResult bindingResult,
                                         Model model) throws AppAuthenticationFailedException {
        ModelAndView mav = new ModelAndView();
        validateChangePassword(passwordChange, bindingResult);
        if (!bindingResult.hasErrors()) {
            passwordChangeService.changePassword(passwordChange.getToken(), passwordChange.getNewpass());
            mav.setViewName("login");
            return mav;
        } else {
            mav.setViewName("changepassword");
            mav.addObject("passwordChange", passwordChange);

            return mav;
        }

    }

    /**
     * Validates change password values
     *
     * @param passwordChange Values to be validated
     * @param bindingResult  Binding result for <code>PasswordChange</code> instance
     */
    private void validateChangePassword(PasswordChange passwordChange, BindingResult bindingResult) {
        if (!passwordChange.getNewpass().equals(passwordChange.getConfirmPass())) {
            bindingResult.rejectValue("newpass", "user.validation.fields.errors.change.password", null,
                "user.validation.fields.errors.change.password");
        }
        if (passwordChange.getNewpass() == null || passwordChange.getNewpass().isEmpty()) {
            bindingResult.rejectValue("newpass", "user.validation.error.empty.or.whitespace", null,
                "user.validation.error.empty.or.whitespace");
        }
        if (passwordChange.getConfirmPass() == null || passwordChange.getConfirmPass().equals("")) {
            bindingResult.rejectValue("confirmPass", "user.validation.error.empty.or.whitespace", null,
                "user.validation.error.empty.or.whitespace");
        }

    }

    /**
     * Validates forgot password values
     *
     * @param forgotPassword Values to be validated
     * @param bindingResult  Binding result for <code>ForgotPassword</code> instance
     */
    private void validateForgetPassword(ForgotPassword forgotPassword, BindingResult bindingResult) throws
        AppAuthenticationFailedException {
        if (forgotPassword.getUsername() == null || forgotPassword.getUsername().equals("")) {
            bindingResult.rejectValue("username", "user.validation.error.empty.or.whitespace", null,
                "user.validation.error.empty.or.whitespace");
        }
        if (!bindingResult.hasErrors()) {
            Profile p = this.profileDAOServiceImpl.getUser(forgotPassword.getUsername(),
                forgotPassword.getTenantName());
            if (p == null) {
                bindingResult.rejectValue("username", "forgot.validation.fields.errors.user.no.exist", null,
                    "forgot.validation.fields.errors.user.no.exist");
            } else if (p.getEmail() == null || p.getEmail().isEmpty()) {
                bindingResult.rejectValue("username", "forgot.validation.fields.errors.email.no.exist", null, "forgot.validation.fields.errors.user.no.exist");
            }
        }

    }
}
