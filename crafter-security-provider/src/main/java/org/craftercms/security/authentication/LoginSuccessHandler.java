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
package org.craftercms.security.authentication;

import java.io.IOException;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.exception.CrafterSecurityException;

/**
 * Handles the request after a successful authentication.
 *
 * @author Alfonso Vásquez
 */
public interface LoginSuccessHandler {

    /**
     * Handles the request after a successful authentication.
     *
     * @param ticket  the authentication ticket, product of the recent login
     * @param profile the user profile
     * @param context the request context
     */
    void onLoginSuccess(String ticket, UserProfile profile, RequestContext context) throws CrafterSecurityException,
        IOException;

}
