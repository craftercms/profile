package org.craftercms.security.impl.processors;

import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.security.api.AuthenticationService;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.authentication.ForgotPasswordFailureHandler;
import org.craftercms.security.authentication.ForgotPasswordSuccessHandler;
import org.craftercms.security.exception.PasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ForgotPasswordProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(ForgotPasswordProcessor.class);

    public static final String DEFAULT_FORGOT_PASSWORD_URL = "/crafter-security-forgot-password";
    public static final String DEFAULT_FORGOT_PASSWORD_METHOD = "POST";
    public static final String DEFAULT_USERNAME_PARAM = "username";

    public static final String DEFAULT_FORGOT_PASSOWRD_URL_PARAM = "changePasswordUrl";

    protected String forgotPasswordUrl;
    protected String forgotPasswordMethod;
    protected String usernameParameter;

    protected String forgotPassUrlParameter;

    protected AuthenticationService authenticationService;
    protected ForgotPasswordSuccessHandler forgotPasswordSuccessHandler;
    protected ForgotPasswordFailureHandler forgotPasswordFailureHandler;

    public ForgotPasswordProcessor() {
        this.forgotPasswordUrl = DEFAULT_FORGOT_PASSWORD_URL;
        this.forgotPasswordMethod = DEFAULT_FORGOT_PASSWORD_METHOD;
        this.usernameParameter = DEFAULT_USERNAME_PARAM;

        this.forgotPassUrlParameter = DEFAULT_FORGOT_PASSOWRD_URL_PARAM;
    }

    public String getForgotPasswordUrl() {
        return forgotPasswordUrl;
    }

    public void setForgotPasswordUrl(String forgotPasswordUrl) {
        this.forgotPasswordUrl = forgotPasswordUrl;
    }

    public String getForgotPasswordMethod() {
        return forgotPasswordMethod;
    }

    public void setForgotPasswordMethod(String forgotMethod) {
        this.forgotPasswordMethod = forgotMethod;
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public String getForgotPassUrlParameter() {
        return forgotPassUrlParameter;
    }

    public void setForgotPassUrlParameter(String forgotPassUrlParameter) {
        this.forgotPassUrlParameter = forgotPassUrlParameter;
    }

    /*
     * (non-Javadoc)
     * @see org.craftercms.security.api.RequestSecurityProcessor#processRequest(org.craftercms.security.api
     * .RequestContext, org.craftercms.security.api.RequestSecurityProcessorChain)
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();

        if (isForgotPasswordRequest(request)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Processing login request");
            }

            if (StringUtils.isEmpty(context.getTenantName())) {
                throw new PasswordException("Request context doesn't contain a tenant name");
            }

            String username = getUsername(request);
            String tenant = context.getTenantName();
            String changePassworUrl = getChangePasswordUrl(request, context);

            if (StringUtils.isEmpty(username)) {
                throw new PasswordException("Request  doesn't contain a username");
            }

            if (StringUtils.isEmpty(changePassworUrl)) {
                throw new PasswordException("Request doesn't contain a changePasswordUrl");
            }

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Authenticating user '" + username + "' for tenant '" + context.getTenantName() + "'");
                }

                UserProfile profile = authenticationService.forgotPassword(changePassworUrl, username, tenant);

                if (profile == null) {
                    throw new PasswordException("Username " + username + " was not found");
                }

                forgotPasswordSuccessHandler.onForgotPasswordSuccess(profile, context);

            } catch (PasswordException e) {
                logger.error(e.getMessage());
                forgotPasswordFailureHandler.onForgotPasswordFailure(e, context);
            } catch (Exception e) {
                logger.error(e.getMessage());
                forgotPasswordFailureHandler.onForgotPasswordFailure(e, context);
            }
        } else {
            processorChain.processRequest(context);
        }
    }

    /**
     * Returns the value of the username parameter from the request.
     */
    protected String getUsername(HttpServletRequest request) {
        return request.getParameter(usernameParameter);
    }

    /**
     * Returns the value of the forgotPassUrlParameter parameter from the request.
     */
    protected String getChangePasswordUrl(HttpServletRequest request, RequestContext context) {
        String uriResetPassword = request.getParameter(this.forgotPassUrlParameter);
        String url = uriResetPassword;
        try {
            if (!isAbsolute(uriResetPassword)) {
                url = createUrlResetPassword(context, uriResetPassword);
            }
        } catch (URISyntaxException e) {
            logger.warn("Reset Password URI Syntax Exception");
        }

        return url;
    }

    private String createUrlResetPassword(RequestContext context, String uriResetPassword) {
        String url = uriResetPassword;
        try {
            int index = context.getRequest().getRequestURL().indexOf(context.getRequest().getRequestURI());
            if (index >= 0) {
                String baseUri = context.getRequest().getRequestURL().substring(0, index);
                if (baseUri.endsWith("/") && uriResetPassword.startsWith("/")) {
                    url = baseUri + uriResetPassword.substring(1);
                } else if (baseUri.endsWith("/")) {
                    url = baseUri + uriResetPassword;
                } else if (uriResetPassword.startsWith("/")) {
                    url = baseUri + uriResetPassword;
                } else {
                    url = baseUri + "/" + uriResetPassword;
                }
            }
        } catch (Exception e) {
            this.logger.error("Error generating the reset password url: " + e.getMessage());
        }
        return url;
    }

    protected boolean isForgotPasswordRequest(HttpServletRequest request) {
        return request.getRequestURI().equals(request.getContextPath() + forgotPasswordUrl) && request.getMethod()
            .equals(forgotPasswordMethod);
    }

    /**
     * Sets the {@link AuthenticationService}, to perform the login against.
     */
    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public ForgotPasswordSuccessHandler getForgotPasswordSuccessHandler() {
        return forgotPasswordSuccessHandler;
    }

    public void setForgotPasswordSuccessHandler(ForgotPasswordSuccessHandler forgotPasswordSuccessHandler) {
        this.forgotPasswordSuccessHandler = forgotPasswordSuccessHandler;
    }

    public ForgotPasswordFailureHandler getForgotPasswordFailureHandler() {
        return forgotPasswordFailureHandler;
    }

    public void setForgotPasswordFailureHandler(ForgotPasswordFailureHandler forgotPasswordFailureHandler) {
        this.forgotPasswordFailureHandler = forgotPasswordFailureHandler;
    }

    private boolean isAbsolute(String uri) throws URISyntaxException {
        URI u = new URI(uri);
        boolean result = false;
        if (u.isAbsolute()) {
            return true;
        }
        return result;
    }

}
