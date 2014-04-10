/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
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
package org.craftercms.profile.v2.repositories.impl;

import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.v2.repositories.TenantRepository;

/**
 * Default implementation of {@link org.craftercms.profile.v2.repositories.TenantRepository}.
 *
 * @author avasquez
 */
public class TenantRepositoryImpl extends JongoRepository<Tenant> implements TenantRepository {

    public static final String KEY_NAME_INDEX_KEYS =        "profile.tenant.nameIndex";
    public static final String KEY_FIND_BY_NAME_QUERY =     "profile.tenant.byName";
    public static final String KEY_REMOVE_BY_NAME_QUERY =   "profile.tenant.removeByName";

    public TenantRepositoryImpl() throws MongoDataException {
        super();

        getCollection().ensureIndex(KEY_NAME_INDEX_KEYS);
    }

    @Override
    public Tenant findByName(String name) throws MongoDataException {
        return findOne(getQueryFor(KEY_FIND_BY_NAME_QUERY), name);
    }

    @Override
    public void removeByName(String name) throws MongoDataException {
        remove(getQueryFor(KEY_REMOVE_BY_NAME_QUERY), name);
    }

}