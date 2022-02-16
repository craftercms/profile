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

package org.craftercms.security.authentication;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.exception.rememberme.RememberMeException;

/**
 * Manages remember me functionality.
 *
 * @author avasquez
 */
public interface RememberMeManager {

    /**
     * Attempts auto login if a remember me cookie is present in the current request
     *
     * @param context the request context
     *
     * @return the authentication if auto login was successful
     */
    Authentication autoLogin(RequestContext context) throws RememberMeException;

    /**
     * Enables remember me for the current authenticated profile, generally by adding a remember me cookie.
     *
     * @param authentication    the authentication object
     * @param context           the request context
     */
    void enableRememberMe(Authentication authentication, RequestContext context) throws RememberMeException;

    /**
     * Disabled remember me for the current authenticated profile, generally by removing remember me cookie.
     *
     * @param context           the request context
     */
    void disableRememberMe(RequestContext context) throws RememberMeException;

}
