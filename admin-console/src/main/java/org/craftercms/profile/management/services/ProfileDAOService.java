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
package org.craftercms.profile.management.services;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.craftercms.profile.client.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.client.impl.domain.Profile;

/**
 * @author Sandra O'Keeffe
 */
public interface ProfileDAOService {

    /**
     * Creates a user
     *
     * @param data
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    Profile createUser(Map<String, Serializable> data) throws AppAuthenticationFailedException;

    /**
     * Deletes a user
     *
     * @param profileId
     * @param active    indicates if the user is actived
     */
    void activeUser(String profileId, boolean active) throws AppAuthenticationFailedException;

    /**
     * Updates a user
     *
     * @param data
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    Profile updateUser(Map<String, Serializable> data) throws AppAuthenticationFailedException;

    /**
     * Gets a user by username
     *
     * @param username
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    Profile getUser(String username, String tenantName) throws AppAuthenticationFailedException;

    /**
     * Gets users index and ordered by modified date
     *
     * @param start
     * @param end
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    List<Profile> getUsersByModifiedDate(int start, int end, String tenantName) throws AppAuthenticationFailedException;


    /**
     * Set the app token
     *
     * @throws AppAuthenticationFailedException
     */
    //	void setAppToken() throws AppAuthenticationFailedException;
}
