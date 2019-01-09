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

import org.craftercms.commons.mongo.AbstractJongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.repositories.TenantRepository;

/**
 * Default implementation of {@link org.craftercms.profile.repositories.TenantRepository}.
 *
 * @author avasquez
 */
public class TenantRepositoryImpl extends AbstractJongoRepository<Tenant> implements TenantRepository {

    public static final String KEY_INDEX_KEYS =             "profile.tenant.index.keys";
    public static final String KEY_INDEX_OPTIONS =          "profile.tenant.index.options";
    public static final String KEY_FIND_BY_NAME_QUERY =     "profile.tenant.byName";
    public static final String KEY_REMOVE_BY_NAME_QUERY =   "profile.tenant.removeByName";

    public void init() throws Exception {
        super.init();

        getCollection().ensureIndex(getQueryFor(KEY_INDEX_KEYS), getQueryFor(KEY_INDEX_OPTIONS));
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
