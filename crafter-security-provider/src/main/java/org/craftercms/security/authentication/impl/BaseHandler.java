/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Base for login and logout handlers.
 *
 * @author avasquez
 */
public class BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(BaseHandler.class);

    protected void redirectToUrl(HttpServletRequest request, HttpServletResponse response,
                                 String relativeUrl) throws IOException {
        String redirectUrl = request.getContextPath() + relativeUrl;

        if (logger.isDebugEnabled()) {
            logger.debug("Redirecting to URL: " + redirectUrl);
        }

        response.sendRedirect(redirectUrl);
    }

}
