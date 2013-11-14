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
package org.craftercms.security.authentication.impl;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.SecurityConstants;
import org.craftercms.security.authentication.BaseHandler;
import org.craftercms.security.authentication.LoginFailureHandler;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.AuthenticationSystemException;
import org.craftercms.security.exception.CrafterSecurityException;
import org.craftercms.security.exception.UserAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link LoginFailureHandler}:
 * <p/>
 * <ol>
 * <li>Saves authentication exception in session for later use.</li>
 * <li>Redirects to target URL, if there's one, and if not, sends 401 UNAUTHORIZED error.</li>
 * </ol>
 *
 * @author Alfonso Vásquez
 */
/**
 * @author Alvaro Gonzalez
 *
 */
public class LoginFailureHandlerImpl extends BaseHandler implements LoginFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginFailureHandlerImpl.class);

    protected String targetUrl;
    
    public LoginFailureHandlerImpl() {
    	super();
    }
    
    /**
     * 
     * Sets the URL to redirect to.
     * 
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     * Saves the authentication exception in the session,
     * under the {@link SecurityConstants#AUTHENTICATION_SYSTEM_EXCEPTION_ATTRIBUTE}
     * or the {@link SecurityConstants#USER_AUTHENTICATION_EXCEPTION_ATTRIBUTE}, depending on the exception type,
     * and then redirects to
     * the target URL or sends a 401 if there's no target URL.
     *
     * @param e       the exception that caused the login to fail.
     * @param context the request context
     * @throws CrafterSecurityException
     * @throws IOException
     */
    public void onLoginFailure(AuthenticationException e, RequestContext context) throws CrafterSecurityException,
        IOException {
        saveException(e, context);

        if (isRedirectRequired && StringUtils.isNotEmpty(targetUrl)) {
            redirectToTargetUrl(context);
        } else {
            sendError(e, context);
        }
    }

    /**
     * Saves the authentication exception in the session,
     * under the {@link SecurityConstants#AUTHENTICATION_SYSTEM_EXCEPTION_ATTRIBUTE}
     * or the {@link SecurityConstants#USER_AUTHENTICATION_EXCEPTION_ATTRIBUTE}, depending on the exception type.
     */
    protected void saveException(AuthenticationException e, RequestContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("Saving authentication exception in session for use after redirect");
        }

        HttpSession session = context.getRequest().getSession();
        if (e instanceof AuthenticationSystemException) {
            session.setAttribute(SecurityConstants.AUTHENTICATION_SYSTEM_EXCEPTION_ATTRIBUTE, e);
        } else if (e instanceof UserAuthenticationException) {
            session.setAttribute(SecurityConstants.USER_AUTHENTICATION_EXCEPTION_ATTRIBUTE, e);
        }
    }

    /**
     * Redirects to the target URL.
     */
    protected void redirectToTargetUrl(RequestContext context) throws IOException {
        String redirectUrl = context.getRequest().getContextPath() + targetUrl;

        if (logger.isDebugEnabled()) {
            logger.debug("Redirecting to URL: " + redirectUrl);
        }

        context.getResponse().sendRedirect(redirectUrl);
    }

    /**
     * Sends a 401 UNAUTHORIZED error.
     */
    protected void sendError(AuthenticationException e, RequestContext context) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Sending 401 UNAUTHORIZED error");
        }
        context.getResponse().setContentType("application/json");
        context.getResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

    
}
