/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.security.impl.processors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.security.api.AuthenticationService;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.authentication.LoginFailureHandler;
import org.craftercms.security.authentication.LoginSuccessHandler;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.AuthenticationSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Processes login requests.
 *
 * @author Alfonso Vásquez
 */
public class LoginProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(LoginProcessor.class);

    public static final String DEFAULT_LOGIN_URL = "/crafter-security-login";
    public static final String DEFAULT_LOGIN_METHOD = "POST";
    public static final String DEFAULT_USERNAME_PARAM = "username";
    public static final String DEFAULT_PASSWORD_PARAM = "password";

    protected String loginUrl;
    protected String loginMethod;
    protected String usernameParameter;
    protected String passwordParameter;

    protected AuthenticationService authenticationService;
    protected LoginSuccessHandler loginSuccessHandler;
    protected LoginFailureHandler loginFailureHandler;

    /**
     * Default constructor.
     */
    public LoginProcessor() {
        loginUrl = DEFAULT_LOGIN_URL;
        loginMethod = DEFAULT_LOGIN_METHOD;
        usernameParameter = DEFAULT_USERNAME_PARAM;
        passwordParameter = DEFAULT_PASSWORD_PARAM;
    }

    /**
     * Sets the login URL this processor should respond to.
     */
    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    /**
     * Sets the HTTP method for the login request this processor should respond to.
     */
    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    /**
     * Sets the password parameter name.
     */
    public void setPasswordParameter(String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    /**
     * Sets the username parameter name.
     */
    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    /**
     * Sets the {@link AuthenticationService}, to perform the login against.
     */
    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Sets the {@link LoginSuccessHandler}, used to handle the request after successful login.
     */
    @Required
    public void setLoginSuccessHandler(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    /**
     * Sets the {@link LoginFailureHandler}, used to handle the request after login failure.
     */
    @Required
    public void setLoginFailureHandler(LoginFailureHandler loginFailureHandler) {
        this.loginFailureHandler = loginFailureHandler;
    }

    /**
     * Checks if the request URL matches the {@code loginUrl} and the HTTP method matches the {@code loginMethod}. If
     * it does, it
     * proceeds to login the user using the username/password specified in the parameters. If the login is
     * successful, the
     * {@link LoginSuccessHandler} is used to handle the request, if not, the {@link LoginFailureHandler} is used
     * instead.
     *
     * @param context        the context which holds the current request and other security info pertinent to the
     *                       request
     * @param processorChain the processor chain, used to call the next processor
     * @throws Exception
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();

        if (isLoginRequest(request)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Processing login request");
            }

            if (StringUtils.isEmpty(context.getTenantName())) {
                throw new IllegalArgumentException("Request context doesn't contain a tenant name");
            }

            String username = getUsername(request);
            String password = getPassword(request);

            if (username == null) {
                username = "";
            }
            if (password == null) {
                password = "";
            }

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Authenticating user '" + username + "' for tenant '" + context.getTenantName() + "'");
                }

                String ticket = authenticationService.authenticate(context.getTenantName(), username, password);
                UserProfile profile = authenticationService.getProfile(ticket);

                if (profile != null) {
                    onLoginSuccess(ticket, profile, context);
                } else {
                    throw new AuthenticationSystemException("Authentication service returned a null profile for " +
                        "recently created " +
                        "ticket '" + ticket + "' for user '" + username + "'");
                }
            } catch (AuthenticationException e) {
                onLoginFailure(e, context);
            }
        } else {
            processorChain.processRequest(context);
        }
    }

    /**
     * Returns true if the request URL matches the {@code loginUrl} and the HTTP method matches the {@code loginMethod}.
     */
    protected boolean isLoginRequest(HttpServletRequest request) {
        return request.getRequestURI().equals(request.getContextPath() + loginUrl) && request.getMethod().equals
            (loginMethod);
    }

    /**
     * Returns the value of the username parameter from the request.
     */
    protected String getUsername(HttpServletRequest request) {
        return request.getParameter(usernameParameter);
    }

    /**
     * Returns the value of the password parameter from the request.
     */
    protected String getPassword(HttpServletRequest request) {
        return request.getParameter(passwordParameter);
    }

    /**
     * Calls the {@link LoginSuccessHandler} to subsequently handle the request on login success.
     */
    protected void onLoginSuccess(String ticket, UserProfile profile, RequestContext context) throws Exception {
        logger.info("Login successful for user '" + profile.getUserName() + "'");

        loginSuccessHandler.onLoginSuccess(ticket, profile, context);
    }

    /**
     * Calls the {@link LoginFailureHandler} to subsequently handle the request on login failure.
     */
    protected void onLoginFailure(AuthenticationException e, RequestContext context) throws Exception {
        logger.warn("Login failed", e);

        loginFailureHandler.onLoginFailure(e, context);
    }

}
