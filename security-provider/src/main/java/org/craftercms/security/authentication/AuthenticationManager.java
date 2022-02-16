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

import org.craftercms.profile.api.Profile;
import org.craftercms.security.exception.AuthenticationException;

/**
 * Manages authentication.
 *
 * @author avasquez
 */
public interface AuthenticationManager {

    /**
     * Authenticates a user.
     *
     * @param tenant    the tenant's name the user profile belongs to
     * @param username  the user's username
     * @param password  the user's password
     *
     * @return the authentication object, which contains the ticket and the user's profile
     */
    Authentication authenticateUser(String tenant, String username, String password) throws AuthenticationException;

    /**
     * Authenticates a user.
     *
     * @param tenants   the tenant chain to try authentication with
     * @param username  the user's username
     * @param password  the user's password
     *
     * @return the authentication object, which contains the ticket and the user's profile
     */
    Authentication authenticateUser(String[] tenants, String username, String password) throws AuthenticationException;

    /**
     * Authenticates a user just with it's profile ID. Use only when the user has already being identified.
     *
     * @param profile the user's profile
     *
     * @return the authentication object, which contains the ticket and the user's profile
     */
    Authentication authenticateUser(Profile profile) throws AuthenticationException;

    /**
     * Authenticates a user just with it's profile ID. Use only when the user has already being identified.
     *
     * @param profile       the user's profile
     * @param remembered    if the authentication was done through remember me.
     *
     * @return the authentication object, which contains the ticket and the user's profile
     */
    Authentication authenticateUser(Profile profile, boolean remembered) throws AuthenticationException;

    /**
     * Returns the authentication associated to the given ticket ID
     *
     * @param ticket        the authentication ticket
     * @param reloadProfile if the cached profile should be reloaded
     *
     * @return the authentication object associated to the ticket ID, or null if no authentication was found
     * for the ticket ID (anonymous user)
     */
    Authentication getAuthentication(String ticket, boolean reloadProfile) throws AuthenticationException;

    /**
     * Invalidates the given authentication.
     *
     * @param authentication the authentication to invalidate
     */
    void invalidateAuthentication(Authentication authentication) throws AuthenticationException;

}
