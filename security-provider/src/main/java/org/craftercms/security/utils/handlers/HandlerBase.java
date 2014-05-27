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
package org.craftercms.security.utils.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Base for login, logout and access denied handlers.
 *
 * @author avasquez
 */
public class HandlerBase {

    private static final Logger logger = LoggerFactory.getLogger(HandlerBase.class);

    private static final String URL_HTTP_PREFIX = "http://";

    protected void redirectToUrl(HttpServletRequest request, HttpServletResponse response,
                                 String url) throws IOException {
        String redirectUrl;

        if (url.startsWith(URL_HTTP_PREFIX)) {
            redirectUrl = url;
        } else {
            redirectUrl = request.getContextPath() + url;
        }

        logger.debug("Redirecting to URL: {}", redirectUrl);

        response.sendRedirect(redirectUrl);
    }

}
