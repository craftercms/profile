/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
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

import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationRequiredHandler;
import org.craftercms.security.authorization.AccessDeniedHandler;
import org.craftercms.security.exception.AccessDeniedException;
import org.craftercms.security.exception.AuthenticationRequiredException;
import org.craftercms.security.exception.SecurityProviderException;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Handles certain security exceptions:
 * <p/>
 * <ul>
 * <li>If it's an {@link AuthenticationRequiredException}, the {@link AuthenticationRequiredHandler} is used.</li>
 * <li>If it's an {@link AccessDeniedException}, and the user is anonymous, the {@link AuthenticationRequiredHandler}
 * is used. If not, the {@link AccessDeniedHandler} is used.</li>
 * </ul>
 *
 * @author Alfonso Vásquez
 */
public class SecurityExceptionProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(SecurityExceptionProcessor.class);

    protected AuthenticationRequiredHandler authenticationRequiredHandler;
    protected AccessDeniedHandler accessDeniedHandler;

    /**
     * Sets the {@link AuthenticationRequiredHandler}, to handle any {@link AuthenticationRequiredException}s thrown.
     */
    @Required
    public void setAuthenticationRequiredHandler(AuthenticationRequiredHandler authenticationRequiredHandler) {
        this.authenticationRequiredHandler = authenticationRequiredHandler;
    }

    /**
     * Sets the {@link AccessDeniedHandler}, to handle any {@link AccessDeniedException}s thrown.
     */
    @Required
    public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }

    /**
     * Catches any exception thrown by the processor chain. If the exception is an instance of a {@link
     * SecurityProviderException}, the exception is handled to see if authentication is required
     * ({@link AuthenticationRequiredException}), or if access to the resource is denied
     * ({@link AccessDeniedException}).
     *
     * @param context        the context which holds the current request and response
     * @param processorChain the processor chain, used to call the next processor
     * @throws Exception
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        try {
            processorChain.processRequest(context);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            SecurityProviderException se = findSecurityException(e);
            if (se != null) {
                handleSecurityProviderException(se, context);
            } else {
                throw e;
            }
        }
    }

    public SecurityProviderException findSecurityException(Exception topException) {
        Throwable[] exceptionChain = ExceptionUtils.getThrowables(topException);
        for (Throwable e : exceptionChain) {
            if (e instanceof SecurityProviderException) {
                return (SecurityProviderException)e;
            }
        }

        return null;
    }

    protected void handleSecurityProviderException(SecurityProviderException e, RequestContext context) throws
            SecurityProviderException, IOException {
        if (e instanceof AuthenticationRequiredException) {
            handleAuthenticationRequiredException(context, (AuthenticationRequiredException)e);
        } else if (e instanceof AccessDeniedException) {
            handleAccessDeniedException(context, (AccessDeniedException)e);
        } else {
            throw e;
        }
    }

    protected void handleAuthenticationRequiredException(RequestContext context, AuthenticationRequiredException e)
            throws SecurityProviderException, IOException {
        logger.debug("Authentication is required", e);

        authenticationRequiredHandler.handle(context, e);
    }

    /**
     * Handles the specified {@link AccessDeniedException}, by calling the {@link AccessDeniedHandler}.
     */
    protected void handleAccessDeniedException(RequestContext context, AccessDeniedException e) throws
            SecurityProviderException, IOException {
        Authentication auth = SecurityUtils.getAuthentication(context.getRequest());
        // If user is anonymous, authentication is required
        if (auth == null) {
            try {
                // Throw ex just to initialize stack trace
                throw new AuthenticationRequiredException("Authentication required to access the resource", e);
            } catch (AuthenticationRequiredException ae) {
                logger.debug("Authentication is required", ae);

                authenticationRequiredHandler.handle(context, ae);
            }
        } else {
            logger.debug("Access denied to user '" + auth.getProfile().getUsername() + "'", e);

            accessDeniedHandler.handle(context, e);
        }
    }

}
