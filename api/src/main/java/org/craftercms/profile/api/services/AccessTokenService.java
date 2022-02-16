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

package org.craftercms.profile.api.services;

import java.util.List;

import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.exceptions.ProfileException;

/**
 * Service for managing access tokens. In order to call any method of this API, a client must use a master access
 * token.
 *
 * @author avasquez
 */
public interface AccessTokenService {

    /**
     * Creates a new access token.
     *
     * @param token the token to create
     *
     * @return the created token
     */
    AccessToken createToken(AccessToken token) throws ProfileException;

    /**
     * Returns the token for the given ID.
     *
     * @param id the ID of the token
     *
     * @return the token, or null if not found
     */
    AccessToken getToken(String id) throws ProfileException;

    /**
     * Returns all the access tokens in the DB.
     *
     * @return all the access tokens
     */
    List<AccessToken> getAllTokens() throws ProfileException;

    /**
     * Deletes the token with the given ID.
     *
     * @param id the ID of the token
     */
    void deleteToken(String id) throws ProfileException;

}
