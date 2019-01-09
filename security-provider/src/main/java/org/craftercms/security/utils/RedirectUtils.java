/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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

package org.craftercms.security.utils;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for redirection.
 *
 * @author avasquez
 */
public class RedirectUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedirectUtils.class);

    /**
     * Redirects to the specified URL. If the URL starts with '/', the request context path is added.
     *
     * @param request   the request
     * @param response  the response
     * @param url       the URL to redirect to
     */
    public static void redirect(HttpServletRequest request, HttpServletResponse response,
                               String url) throws IOException {
        String redirectUrl;

        if (url.startsWith("/")) {
            redirectUrl = request.getContextPath() + url;
        } else {
            redirectUrl = url;
        }

        logger.debug("Redirecting to URL: {}", redirectUrl);

        response.sendRedirect(redirectUrl);
    }

}
