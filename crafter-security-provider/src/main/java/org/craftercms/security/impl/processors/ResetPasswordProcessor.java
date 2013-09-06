package org.craftercms.security.impl.processors;

import javax.servlet.http.HttpServletRequest;

import org.craftercms.profile.exceptions.PasswordException;
import org.craftercms.security.api.AuthenticationService;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.authentication.ResetPasswordFailureHandler;
import org.craftercms.security.authentication.ResetPasswordSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Request reset the password of one profile. Manage errors in case that the new password and confirm password doesn't match or are blank
 *
 * @author Alvaro Gonzalez
 */
public class ResetPasswordProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(ResetPasswordProcessor.class);

    public static final String DEFAULT_FORGOT_PASSWORD_URL = "/crafter-security-reset-password";
    public static final String DEFAULT_FORGOT_PASSWORD_METHOD = "POST";
    public static final String DEFAULT_PASSWORD_PARAM = "newPassword";
    public static final String DEFAULT_CONFIRM_PASSWORD_PARAM = "confirmPassword";
    public static final String DEFAULT_TOKEN_PARAM = "token";

    protected String changePasswordUrl;
    protected String changePasswordMethod;
    protected String newPasswordParameter;
    protected String confirmPasswordParameter;
    protected String tokenParameter;
    protected String forgotPassUrlParameter;

    protected AuthenticationService authenticationService;
    protected ResetPasswordSuccessHandler resetPasswordSuccessHandler;
    protected ResetPasswordFailureHandler resetPasswordFailureHandler;

    public ResetPasswordProcessor() {
        this.changePasswordUrl = DEFAULT_FORGOT_PASSWORD_URL;
        this.changePasswordMethod = DEFAULT_FORGOT_PASSWORD_METHOD;
        this.newPasswordParameter = DEFAULT_PASSWORD_PARAM;
        this.confirmPasswordParameter = DEFAULT_CONFIRM_PASSWORD_PARAM;
        this.tokenParameter = DEFAULT_TOKEN_PARAM;
    }

    public String getChangePasswordUrl() {
        return changePasswordUrl;
    }

    public void setChangePasswordUrl(String changePasswordUrl) {
        this.changePasswordUrl = changePasswordUrl;
    }

    public String getChangePasswordMethod() {
        return changePasswordMethod;
    }

    public void setChangePasswordMethod(String forgotMethod) {
        this.changePasswordMethod = forgotMethod;
    }

    public String getNewPasswordParameter() {
        return newPasswordParameter;
    }

    public void setNewPasswordParameter(String newPasswordParameter) {
        this.newPasswordParameter = newPasswordParameter;
    }

    public String getConfirmPasswordParameter() {
        return confirmPasswordParameter;
    }

    public void setConfirmPasswordParameter(String confirmPasswordParameter) {
        this.confirmPasswordParameter = confirmPasswordParameter;
    }

    /*
     * (non-Javadoc)
     * @see org.craftercms.security.api.RequestSecurityProcessor#processRequest(org.craftercms.security.api.RequestContext, org.craftercms.security.api.RequestSecurityProcessorChain)
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();

        if (isChangePasswordRequest(request)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Processing login request");
            }


            String password = getPassword(request);
            String confirmPassword = getCofirmPassword(request);
            String token = getToken(request);

            if (password == null) {
                password = "";
            }
            if (token == null) {
                token = "";
            }
            try {
                if (password == null || confirmPassword == null || password.equals("") || confirmPassword.equals("")) {
                    throw new PasswordException("Password and Confirm password are required values");
                }
                if (!password.equals(confirmPassword)) {
                    throw new PasswordException("Password and Confirm password must match");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("ResetPassword request for token " + token);
                }

                UserProfile profile = authenticationService.resetPassword(password, token);

                resetPasswordSuccessHandler.onResetPasswordSuccess(profile, context);

            } catch (Exception e) {
                logger.error(e.getMessage());
                resetPasswordFailureHandler.onResetPasswordFailure(e, context, token);
            }
        } else {
            processorChain.processRequest(context);
        }
    }

    /**
     * Returns the value of the password parameter from the request.
     */
    protected String getPassword(HttpServletRequest request) {
        return request.getParameter(newPasswordParameter);
    }

    /**
     * Returns the value of the password parameter from the request.
     */
    protected String getCofirmPassword(HttpServletRequest request) {
        return request.getParameter(confirmPasswordParameter);
    }

    /**
     * Returns the value of the token parameter from the request.
     */
    protected String getToken(HttpServletRequest request) {
        return request.getParameter(tokenParameter);
    }


    protected boolean isChangePasswordRequest(HttpServletRequest request) {
        return request.getRequestURI().equals(request.getContextPath() + changePasswordUrl) && request.getMethod()
            .equals(changePasswordMethod);
    }

    /**
     * Sets the {@link AuthenticationService}, to perform the login against.
     */
    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public ResetPasswordSuccessHandler getResetPasswordSuccessHandler() {
        return resetPasswordSuccessHandler;
    }

    public void setResetPasswordSuccessHandler(ResetPasswordSuccessHandler resetPasswordSuccessHandler) {
        this.resetPasswordSuccessHandler = resetPasswordSuccessHandler;
    }

    public ResetPasswordFailureHandler getResetPasswordFailureHandler() {
        return resetPasswordFailureHandler;
    }

    public void setResetPasswordFailureHandler(ResetPasswordFailureHandler resetPasswordFailureHandler) {
        this.resetPasswordFailureHandler = resetPasswordFailureHandler;
    }

}
