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

package org.craftercms.profile.repositories.impl;

import com.mongodb.MongoException;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.craftercms.commons.mongo.AbstractJongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.PersistentLogin;
import org.craftercms.profile.repositories.PersistentLoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link org.craftercms.profile.api.PersistentLogin}.
 *
 * @author avasquez
 */
public class PersistentLoginRepositoryImpl extends AbstractJongoRepository<PersistentLogin>
    implements PersistentLoginRepository {

    private static final Logger logger = LoggerFactory.getLogger(PersistentLoginRepositoryImpl.class);

    public static final String KEY_FIND_BY_PROFILE_ID_AND_TOKEN = "profile.persistentLogin.byProfileIdAndToken";
    public static final String KEY_REMOVE_TOKENS_OLDER_THAN_QUERY = "profile.persistentLogin.removeOlderThan";

    @Override
    public PersistentLogin findByProfileIdAndToken(String profileId, String token) throws MongoDataException {
        try {
            return getCollection().findOne(getQueryFor(KEY_FIND_BY_PROFILE_ID_AND_TOKEN)).as(PersistentLogin.class);
        } catch (MongoException ex) {
            String msg = "Unable to find persistent login by profile ID '" + profileId + "' and token '" + token + "'";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    @Override
    public void removeOlderThan(long seconds) throws MongoDataException {
        long millis = TimeUnit.SECONDS.toMillis(seconds);
        Date limit = new Date(System.currentTimeMillis() - millis);

        remove(getQueryFor(KEY_REMOVE_TOKENS_OLDER_THAN_QUERY), limit);
    }

}
