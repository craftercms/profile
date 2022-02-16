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

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.LogoutSuccessHandler;
import org.craftercms.security.exception.SecurityProviderException;
import org.craftercms.security.utils.RedirectUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation for {@link org.craftercms.security.authentication.impl.LogoutSuccessHandlerImpl}, which
 * redirects to a target URL.
 *
 * @author avasquez
 */
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    protected String targetUrl;

    @Required
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    protected String getTargetUrl() {
        return targetUrl;
    }

    /**
     * Redirects to the target URL.
     *
     * @param context           the request context
     */
    @Override
    public void handle(RequestContext context) throws SecurityProviderException, IOException {
        RedirectUtils.redirect(context.getRequest(), context.getResponse(), getTargetUrl());
    }

}
