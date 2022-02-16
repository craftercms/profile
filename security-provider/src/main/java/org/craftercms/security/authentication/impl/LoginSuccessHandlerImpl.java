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
package org.craftercms.security.authentication.impl;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.LoginSuccessHandler;
import org.craftercms.security.exception.SecurityProviderException;
import org.craftercms.security.utils.RedirectUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

/**
 * Default implementation of {@link LoginSuccessHandler}:
 * <p/>
 * <ol>
 * <li>Deletes any authentication exception saved in the session.</li>
 * <li>Adds the ticket ID and profile last modified cookies to response.</li>
 * <li>Uses the Spring {@link RequestCache} to obtain the previous request before login and redirect to it.</li>
 * </ol>
 *
 * @author Alfonso Vásquez
 */
public class LoginSuccessHandlerImpl implements LoginSuccessHandler {

    protected RequestCache requestCache;
    protected String defaultTargetUrl;
    protected boolean alwaysUseDefaultTargetUrl;

    public LoginSuccessHandlerImpl() {
        super();
        requestCache = new HttpSessionRequestCache();
        alwaysUseDefaultTargetUrl = false;
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    @Required
    public void setDefaultTargetUrl(String defaultTargetUrl) {
        this.defaultTargetUrl = defaultTargetUrl;
    }

    protected String getDefaultTargetUrl() {
        return defaultTargetUrl;
    }

    public void setAlwaysUseDefaultTargetUrl(boolean alwaysUseDefaultTargetUrl) {
        this.alwaysUseDefaultTargetUrl = alwaysUseDefaultTargetUrl;
    }

    protected boolean isAlwaysUseDefaultTargetUrl() {
        return alwaysUseDefaultTargetUrl;
    }

    @Override
    public void handle(RequestContext context, Authentication authentication) throws SecurityProviderException,
            IOException {
        redirectToSavedRequest(context.getRequest(), context.getResponse());
    }

    protected void redirectToSavedRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (!isAlwaysUseDefaultTargetUrl() && savedRequest != null) {
            RedirectUtils.redirect(request, response, savedRequest.getRedirectUrl());
        } else {
            RedirectUtils.redirect(request, response, getDefaultTargetUrl());
        }
    }

}
