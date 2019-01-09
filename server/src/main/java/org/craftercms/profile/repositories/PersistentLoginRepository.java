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

package org.craftercms.profile.repositories;

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.PersistentLogin;

/**
 * DB repository for {@link org.craftercms.profile.api.PersistentLogin}s.
 *
 * @author avasquez
 */
public interface PersistentLoginRepository extends CrudRepository<PersistentLogin> {

    /**
     * Returns the login associated to the given profile ID and token.
     *
     * @param profileId the profile's ID
     * @param token     the token
     *
     * @return the login
     */
    PersistentLogin findByProfileIdAndToken(String profileId, String token) throws MongoDataException;

    /**
     * Removes logins with timestamps older than the specified number of seconds.
     *
     * @param seconds the number of seconds
     */
    void removeOlderThan(long seconds) throws MongoDataException;

}
