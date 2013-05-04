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
package org.craftercms.security.api;

import org.craftercms.security.exception.AuthenticationException;

/**
 * Provides the authentication entry point for the application.
 *
 * @author Alfonso Vásquez
 */
public interface AuthenticationService {

    /**
     * Returns the profile associated to the ticket, or null if the user is not authenticated or the ticket has expired.
     *
     * @throws AuthenticationException
     *          if a system error occurs
     */
    UserProfile getProfile(String ticket) throws AuthenticationException;

    /**
     * Performs an authentication attempt.
     *
     * @return an authentication ticket
     *
     * @throws AuthenticationException
     *          if authentication fails because of a system error or because of bad credentials
     */
    String authenticate(String tenantName, String username, String password) throws AuthenticationException;

    /**
     * Invalidates the specified ticket, used basically for logout.
     *
     * @throws AuthenticationException
     *          if a system error occurs
     */
    void invalidateTicket(String ticket) throws AuthenticationException;

}
