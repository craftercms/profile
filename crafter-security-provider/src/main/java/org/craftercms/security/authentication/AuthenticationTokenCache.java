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

import org.craftercms.security.api.RequestContext;

/**
 * Utility cache for authentication tokens.
 *
 * @author Alfonso Vásquez
 */
public interface AuthenticationTokenCache {

    /**
     * Returns an authentication token for the request:
     * <p/>
     * <ol>
     * <li>If the profile is considered outdated, the token is returned with profile outdated = true and is removed
     * from cache.</li>
     * <li>If a ticket was found in request, but no token cached for it, a new token is returned with just the ticket
     * .</li>
     * <li>If no ticket was found, null is returned.</li>
     * </ol>
     */
    AuthenticationToken getToken(RequestContext context);

    /**
     * Saves the authentication token in cache, updating also the profile-outdated-after date.
     */
    void saveToken(RequestContext context, AuthenticationToken authToken);

    /**
     * Removes the authentication token from the cache.
     */
    void removeToken(RequestContext context, AuthenticationToken authToken);

}
