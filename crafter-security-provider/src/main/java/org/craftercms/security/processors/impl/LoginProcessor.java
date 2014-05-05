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
package org.craftercms.security.processors.impl;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationManager;
import org.craftercms.security.authentication.LoginFailureHandler;
import org.craftercms.security.authentication.LoginSuccessHandler;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.AuthenticationSystemException;
import org.craftercms.security.exception.BadCredentialsException;
import org.craftercms.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
    protected AuthenticationManager authenticationManager;
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
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Required
    public void setLoginSuccessHandler(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Required
    public void setLoginFailureHandler(LoginFailureHandler loginFailureHandler) {
        this.loginFailureHandler = loginFailureHandler;
    }

    /**
     * Checks if the request URL matches the {@code loginUrl} and the HTTP method matches the {@code loginMethod}. If
     * it does, it proceeds to login the user using the username/password specified in the parameters.
     *
     * @param context        the context which holds the current request and response
     * @param processorChain the processor chain, used to call the next processor
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();

        if (isLoginRequest(request)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Processing login request");
            }

            String tenant = SecurityUtils.getTenant(request);
            if (StringUtils.isEmpty(tenant)) {
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
                    logger.debug("Authenticating user '" + username + "' for tenant '" + tenant + "'");
                }

                Authentication auth = authenticationManager.authenticateUser(tenant, username, password);

                onLoginSuccess(context, auth);
            } catch (AuthenticationException e) {
                onLoginFailure(context, e);
            }
        } else {
            processorChain.processRequest(context);
        }
    }

    protected boolean isLoginRequest(HttpServletRequest request) {
        return request.getRequestURI().equals(request.getContextPath() + loginUrl) && request.getMethod()
                .equals(loginMethod);
    }

    protected String getUsername(HttpServletRequest request) {
        return request.getParameter(usernameParameter);
    }

    protected String getPassword(HttpServletRequest request) {
        return request.getParameter(passwordParameter);
    }

    protected void onLoginSuccess(RequestContext context, Authentication authentication) throws Exception {
        logger.info("Login successful for user '" + authentication.getProfile().getUsername() + "'");

        HttpServletRequest request = context.getRequest();

        clearExceptions(request);

        SecurityUtils.setAuthentication(request, authentication);

        loginSuccessHandler.handle(context, authentication);
    }

    protected void onLoginFailure(RequestContext context, AuthenticationException e) throws Exception {
        logger.warn("Login failed", e);

        saveException(e, context.getRequest());

        loginFailureHandler.handle(context, e);
    }

    protected void saveException(AuthenticationException e, HttpServletRequest request) {
        logger.debug("Saving authentication exception in session for later use");

        HttpSession session = request.getSession(true);
        if (e instanceof AuthenticationSystemException) {
            session.setAttribute(SecurityUtils.AUTHENTICATION_SYSTEM_EXCEPTION_ATTRIBUTE, e);
        } else if (e instanceof BadCredentialsException) {
            session.setAttribute(SecurityUtils.BAD_CREDENTIALS_EXCEPTION_ATTRIBUTE, e);
        }
    }

    protected void clearExceptions(HttpServletRequest request) {
        logger.debug("Removing any authentication exceptions from session, not needed anymore");

        HttpSession session = request.getSession();
        if (session != null) {
            session.removeAttribute(SecurityUtils.AUTHENTICATION_SYSTEM_EXCEPTION_ATTRIBUTE);
            session.removeAttribute(SecurityUtils.BAD_CREDENTIALS_EXCEPTION_ATTRIBUTE);
        }
    }

}
