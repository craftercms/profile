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

import org.apache.commons.lang.StringUtils;
import org.craftercms.security.api.*;
import org.craftercms.security.authentication.LoginFailureHandler;
import org.craftercms.security.authentication.LoginSuccessHandler;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.AuthenticationSystemException;
import org.craftercms.security.exception.CrafterSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Processes login requests.
 *
 * @author Alfonso Vásquez
 */
public class LoginProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(LoginProcessor.class);

    public static final String DEFAULT_LOGIN_URL =      "/crafter-security-login";
    public static final String DEFAULT_LOGIN_METHOD =   "POST";
    public static final String DEFAULT_USERNAME_PARAM = "username";
    public static final String DEFAULT_PASSWORD_PARAM = "password";

    protected String loginUrl;
    protected String loginMethod;
    protected String usernameParameter;
    protected String passwordParameter;

    protected AuthenticationService authenticationService;
    protected LoginSuccessHandler loginSuccessHandler;
    protected LoginFailureHandler loginFailureHandler;

    public LoginProcessor() {
        loginUrl = DEFAULT_LOGIN_URL;
        loginMethod = DEFAULT_LOGIN_METHOD;
        usernameParameter = DEFAULT_USERNAME_PARAM;
        passwordParameter = DEFAULT_PASSWORD_PARAM;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    public void setPasswordParameter(String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Required
    public void setLoginSuccessHandler(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Required
    public void setLoginFailureHandler(LoginFailureHandler loginFailureHandler) {
        this.loginFailureHandler = loginFailureHandler;
    }

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
                    throw new AuthenticationSystemException("Authentication service returned a null profile for recently created " +
                            "ticket '" + ticket + "' for user '" + username + "'");
                }
            } catch (AuthenticationException e) {
                onLoginFailure(e, context);
            }
        } else {
            processorChain.processRequest(context);
        }
    }

    protected boolean isLoginRequest(HttpServletRequest request) {
        return request.getRequestURI().equals(request.getContextPath() + loginUrl) && request.getMethod().equals(loginMethod);
    }

    protected String getUsername(HttpServletRequest request) {
        return request.getParameter(usernameParameter);
    }

    protected String getPassword(HttpServletRequest request) {
        return request.getParameter(passwordParameter);
    }

    protected void onLoginSuccess(String ticket, UserProfile profile, RequestContext context) throws CrafterSecurityException,
            IOException {
        logger.info("Login successful for user '" + profile.getUserName() + "'");

        loginSuccessHandler.onLoginSuccess(ticket, profile, context);
    }

    protected void onLoginFailure(AuthenticationException e, RequestContext context) throws CrafterSecurityException, IOException {
        logger.warn("Login failed", e);

        loginFailureHandler.onLoginFailure(e, context);
    }

}
