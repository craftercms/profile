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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.authentication.AuthenticationRequiredHandler;
import org.craftercms.security.authorization.AccessDeniedHandler;
import org.craftercms.security.exception.AccessDeniedException;
import org.craftercms.security.exception.AuthenticationRequiredException;
import org.craftercms.security.exception.CrafterSecurityException;
import org.craftercms.security.exception.InvalidCookieException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles certain security exceptions:
 *
 * <ul>
 *     <li>If it's an {@link AuthenticationRequiredException}, the {@link AuthenticationRequiredHandler} is used.</li>
 *     <li>If it's an {@link AccessDeniedException}, and the user is anonymous, the {@link AuthenticationRequiredHandler} is used.
 *     If not, the {@link AccessDeniedHandler} is used.</li>
 *     <li>If it's an {@link InvalidCookieException}, a 400 error is sent.</li>
 * </ul>
 *
 * @author Alfonso Vásquez
 */
public class SecurityExceptionProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(SecurityExceptionProcessor.class);

    protected AuthenticationRequiredHandler authenticationRequiredHandler;
    protected AccessDeniedHandler accessDeniedHandler;

    @Required
    public void setAuthenticationRequiredHandler(AuthenticationRequiredHandler authenticationRequiredHandler) {
        this.authenticationRequiredHandler = authenticationRequiredHandler;
    }

    @Required
    public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }

    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        try {
            processorChain.processRequest(context);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            CrafterSecurityException se = findSecurityException(e);
            if (se != null) {
                handleSecurityException(se, context);
            } else {
                throw e;
            }
        }
    }

    public CrafterSecurityException findSecurityException(Exception topException) {
        Throwable[] exceptionChain = ExceptionUtils.getThrowables(topException);
        for (Throwable e : exceptionChain) {
            if (e instanceof CrafterSecurityException) {
                return (CrafterSecurityException) e;
            }
        }

        return null;
    }

    protected void handleSecurityException(CrafterSecurityException e, RequestContext context) throws CrafterSecurityException,
            IOException {
        if (e instanceof AuthenticationRequiredException) {
            handleAuthenticationRequiredException((AuthenticationRequiredException) e, context);
        } else if (e instanceof AccessDeniedException) {
            handleAccessDeniedException((AccessDeniedException) e, context);
        } else if (e instanceof InvalidCookieException) {
            handleInvalidCookieException((InvalidCookieException) e, context);
        } else {
            throw e;
        }
    }

    protected void handleAuthenticationRequiredException(AuthenticationRequiredException e, RequestContext context)
            throws CrafterSecurityException, IOException {
        logger.warn("Authentication is required", e);

        authenticationRequiredHandler.onAuthenticationRequired(e, context);
    }

    protected void handleAccessDeniedException(AccessDeniedException e, RequestContext context) throws CrafterSecurityException,
            IOException {
        UserProfile profile = context.getAuthenticationToken().getProfile();

        // If user is anonymous, authentication is required
        if (profile.isAnonymous()) {
            try {
                // Throw ex just to initialize stack trace
                throw new AuthenticationRequiredException("Anonymous user: authentication needed to access a resource", e);
            } catch (AuthenticationRequiredException ae) {
                logger.warn("Authentication is required", ae);

                authenticationRequiredHandler.onAuthenticationRequired(ae, context);
            }
        } else {
            logger.warn("Access denied to user '" + profile.getUserName() + "'", e);

            accessDeniedHandler.onAccessDenied(e, context);
        }
    }

    protected void handleInvalidCookieException(InvalidCookieException e, RequestContext context) throws CrafterSecurityException,
            IOException {
        logger.warn("Invalid security cookie in request", e);

        context.getResponse().sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

}
