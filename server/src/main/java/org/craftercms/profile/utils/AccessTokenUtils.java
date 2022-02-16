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

package org.craftercms.profile.utils;

import javax.servlet.http.HttpServletRequest;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.AccessToken;

/**
 * Utility methods for {@link org.craftercms.profile.api.AccessToken}s.
 *
 * @author avasquez
 */
public class AccessTokenUtils {

    public static final String ACCESS_TOKE_ATTRIBUTE_NAME = "accessToken";

    public static AccessToken getCurrentToken() {
        return getAccessToken(RequestContext.getCurrent().getRequest());
    }

    public static void setCurrentToken(AccessToken accessToken) {
        setAccessToken(RequestContext.getCurrent().getRequest(), accessToken);
    }

    public static AccessToken getAccessToken(HttpServletRequest request) {
        return (AccessToken)request.getAttribute(ACCESS_TOKE_ATTRIBUTE_NAME);
    }

    public static void setAccessToken(HttpServletRequest request, AccessToken accessToken) {
        request.setAttribute(ACCESS_TOKE_ATTRIBUTE_NAME, accessToken);
    }

    private AccessTokenUtils() {
    }

}
