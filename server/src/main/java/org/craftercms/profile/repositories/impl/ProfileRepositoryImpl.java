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
package org.craftercms.profile.repositories.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.AbstractJongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.repositories.ProfileRepository;
import org.jongo.Find;
import org.jongo.FindOne;
import org.jongo.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

/**
 * Default implementation of {@link org.craftercms.profile.repositories.ProfileRepository}.
 *
 * @author avasquez
 */
public class ProfileRepositoryImpl extends AbstractJongoRepository<Profile> implements ProfileRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProfileRepositoryImpl.class);

    public static final String KEY_INDEX_KEYS = "profile.profile.index.keys";
    public static final String KEY_INDEX_OPTIONS = "profile.profile.index.options";
    public static final String KEY_DEFAULT_FIELDS = "profile.profile.defaultFields";
    public static final String KEY_FIND_BY_TENANT_AND_USERNAME_QUERY = "profile.profile.byTenantAndUsername";
    public static final String KEY_COUNT_BY_TENANT_QUERY = "profile.profile.countByTenant";
    public static final String KEY_REMOVE_BY_TENANT_QUERY = "profile.profile.removeByTenant";
    public static final String KEY_FIND_BY_IDS_QUERY = "profile.profile.byIds";
    public static final String KEY_FIND_BY_TENANT_QUERY = "profile.profile.byTenant";
    public static final String KEY_FIND_BY_TENANT_AND_ROLE_QUERY = "profile.profile.byTenantAndRole";
    public static final String KEY_FIND_BY_TENANT_AND_EXISTING_ATTRIB_QUERY = "profile.profile" +
                                                                              ".byTenantAndExistingAttribute";
    public static final String KEY_FIND_BY_TENANT_AND_ATTRIB_VALUE_QUERY = "profile.profile" +
                                                                           ".byTenantAndAttributeValue";
    public static final String KEY_FIND_BY_TENANT_AND_NON_EXISTING_ATTRIB_QUERY = "profile.profile" +
                                                                                  ".byTenantAndNonExistingAttribute";

    public static final String ATTRIBUTE_FIELD_PREFIX = "attributes.";

    public static final String MODIFIER_REMOVE_ROLE = "{$pull: {roles: #}}";
    public static final String MODIFIER_REMOVE_ATTRIBUTE = "{$unset: {attributes.#: ''}}";
    public static final String MODIFIER_UPDATE_ATTRIBUTE = "{$set: {attributes.#: #}}";

    @Override
    public void init() throws Exception {
        super.init();

        getCollection().ensureIndex(getQueryFor(KEY_INDEX_KEYS), getQueryFor(KEY_INDEX_OPTIONS));
    }

    @Override
    public Profile findOneByQuery(String query, String... attributesToReturn) throws MongoDataException {
        try {
            FindOne findOne = getCollection().findOne(query);

            addProjection(findOne, attributesToReturn);

            return findOne.as(Profile.class);
        } catch (MongoException ex) {
            String msg = "Unable to find profile by query '" + query + "'";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    @Override
    public Profile findById(String id, String... attributesToReturn) throws MongoDataException {
        try {
            FindOne findOne = getCollection().findOne(new ObjectId(id));

            addProjection(findOne, attributesToReturn);

            return findOne.as(Profile.class);
        } catch (MongoException ex) {
            String msg = "Unable to find profile by id '" + id + "'";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        } catch (IllegalArgumentException ex) {
            String msg = "Given id '" + id + "' can't be converted to an ObjectId";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    @Override
    public Iterable<Profile> findByQuery(String query, String sortBy, SortOrder sortOrder, Integer start,
                                         Integer count, String... attributesToReturn) throws MongoDataException {
        try {
            Find find = getCollection().find(query);

            addSort(find, sortBy, sortOrder);
            addRange(find, start, count);
            addProjection(find, attributesToReturn);

            return find.as(Profile.class);
        } catch (MongoException ex) {
            String msg = "Unable to find profiles by query " + query;
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    @Override
    public Iterable<Profile> findByIds(List<String> ids, String sortBy, SortOrder sortOrder,
                                       String... attributesToReturn) throws MongoDataException {
        List<ObjectId> objectIds = new ArrayList<>(ids.size());
        for (String id : ids) {
            try {
                objectIds.add(new ObjectId(id));
            } catch (IllegalArgumentException ex) {
                String msg = "Given id '" + id + "' can't be converted to an ObjectId";
                logger.error(msg, ex);
                throw new MongoDataException(msg, ex);
            }
        }

        try {

            String query = getQueryFor(KEY_FIND_BY_IDS_QUERY);
            Find find = getCollection().find(query, objectIds);

            addSort(find, sortBy, sortOrder);
            addProjection(find, attributesToReturn);

            return find.as(Profile.class);
        } catch (MongoException ex) {
            String msg = "Unable to find profiles for ids " + ids;
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    @Override
    public Iterable<Profile> findRange(String tenantName, String sortBy, SortOrder sortOrder, Integer start,
                                       Integer count, String... attributesToReturn) throws MongoDataException {
        try {
            String query = getQueryFor(KEY_FIND_BY_TENANT_QUERY);
            Find find = getCollection().find(query, tenantName);

            addSort(find, sortBy, sortOrder);
            addRange(find, start, count);
            addProjection(find, attributesToReturn);

            return find.as(Profile.class);
        } catch (MongoException ex) {
            String msg = "Unable to find range of profiles for tenant '" + tenantName + "'";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    @Override
    public Iterable<Profile> findByTenantAndRole(String tenantName, String role, String sortBy, SortOrder sortOrder,
                                                 String... attributesToReturn) throws MongoDataException {
        try {
            String query = getQueryFor(KEY_FIND_BY_TENANT_AND_ROLE_QUERY);
            Find find = getCollection().find(query, tenantName, role);

            addSort(find, sortBy, sortOrder);
            addProjection(find, attributesToReturn);

            return find.as(Profile.class);
        } catch (MongoException ex) {
            String msg = "Unable to find profiles for role '" + role + " and tenant '" + tenantName + "'";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    @Override
    public Iterable<Profile> findByTenantAndExistingAttribute(String tenantName, String attributeName, String sortBy,
                                                              SortOrder sortOrder,
                                                              String... attributesToReturn) throws MongoDataException {
        try {
            String query = getQueryFor(KEY_FIND_BY_TENANT_AND_EXISTING_ATTRIB_QUERY);
            Find find = getCollection().find(query, tenantName, attributeName);

            addSort(find, sortBy, sortOrder);
            addProjection(find, attributesToReturn);

            return find.as(Profile.class);
        } catch (MongoException ex) {
            String msg = "Unable to find profiles with attribute " + attributeName + " and tenant '" + tenantName + "'";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    @Override
    public Iterable<Profile> findByTenantAndAttributeValue(String tenantName, String attributeName,
                                                           String attributeValue, String sortBy, SortOrder sortOrder,
                                                           String... attributesToReturn) throws MongoDataException {
        try {
            String query = getQueryFor(KEY_FIND_BY_TENANT_AND_ATTRIB_VALUE_QUERY);
            Find find = getCollection().find(query, tenantName, attributeName, attributeValue);

            addSort(find, sortBy, sortOrder);
            addProjection(find, attributesToReturn);

            return find.as(Profile.class);
        } catch (MongoException ex) {
            String msg = "Unable to find profiles for attribute " + attributeName + " = " + attributeValue +
                " and tenant '" + tenantName + "'";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    @Override
    public Profile findByTenantAndUsername(String tenantName, String username, String... attributesToReturn)
        throws MongoDataException {
        try {
            String query = getQueryFor(KEY_FIND_BY_TENANT_AND_USERNAME_QUERY);
            FindOne findOne = getCollection().findOne(query, tenantName, username);

            if (ArrayUtils.isNotEmpty(attributesToReturn)) {
                findOne = findOne.projection(buildProjectionWithAttributes(attributesToReturn));
            }

            return findOne.as(Profile.class);
        } catch (MongoException ex) {
            String msg = "Unable to find profile by tenant name '" + tenantName + "' and username '" + username + "'";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    @Override
    public long countByTenant(String tenantName) throws MongoDataException {
        return count(getQueryFor(KEY_COUNT_BY_TENANT_QUERY), tenantName);
    }

    @Override
    public void removeAll(String tenantName) throws MongoDataException {
        remove(getQueryFor(KEY_REMOVE_BY_TENANT_QUERY), tenantName);
    }

    @Override
    public void removeRoleFromAll(String tenantName, String role) throws MongoDataException {
        try {
            String query = getQueryFor(KEY_FIND_BY_TENANT_AND_ROLE_QUERY);
            Update update = getCollection().update(query, tenantName, role).multi();

            update.with(MODIFIER_REMOVE_ROLE, role);
        } catch (MongoException ex) {
            String msg = "Unable to remove role '" + role + "' from profiles of tenant '" + tenantName + "'";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    @Override
    public void removeAttributeFromAll(String tenantName, String attributeName) throws MongoDataException {
        try {
            String query = getQueryFor(KEY_FIND_BY_TENANT_AND_EXISTING_ATTRIB_QUERY);
            Update update = getCollection().update(query, tenantName, attributeName).multi();

            update.with(MODIFIER_REMOVE_ATTRIBUTE, attributeName);
        } catch (MongoException ex) {
            String msg = "Unable to remove attribute with name '" + attributeName + "' from profiles of tenant '" +
                         tenantName + "'";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    @Override
    public void updateAllWithDefaultValue(String tenantName, String attributeName,
                                          Object defaultValue) throws MongoDataException {
        try {
            String query = getQueryFor(KEY_FIND_BY_TENANT_AND_NON_EXISTING_ATTRIB_QUERY);
            Update update = getCollection().update(query, tenantName, attributeName).multi();
            update.with(MODIFIER_UPDATE_ATTRIBUTE, attributeName, defaultValue);
        } catch (MongoException ex) {
            String msg = "Unable to add attribute with name '" + attributeName + "' to profiles of tenant '" +
                         tenantName + "'";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }


    protected Find addSort(Find find, String sortBy, SortOrder sortOrder) {
        if (StringUtils.isNotEmpty(sortBy)) {
            find = find.sort("{" + sortBy + ": " + (sortOrder == SortOrder.ASC? "1": "-1") + "}");
        }

        return find;
    }

    protected Find addRange(Find find, Integer start, Integer count) {
        if (start != null) {
            find = find.skip(start);

            if (count != null) {
                find = find.limit(count);
            }
        }

        return find;
    }



    protected FindOne addProjection(FindOne findOne, String... attributesToReturn) {
        if (ArrayUtils.isNotEmpty(attributesToReturn)) {
            findOne = findOne.projection(buildProjectionWithAttributes(attributesToReturn));
        }

        return findOne;
    }

    protected Find addProjection(Find find, String... attributesToReturn) {
        if (ArrayUtils.isNotEmpty(attributesToReturn)) {
            find = find.projection(buildProjectionWithAttributes(attributesToReturn));
        }

        return find;
    }

    protected String buildProjectionWithAttributes(String... attributeNames) {
        StringBuilder projection = new StringBuilder(getQueryFor(KEY_DEFAULT_FIELDS));

        if (!ArrayUtils.contains(attributeNames, ProfileConstants.NO_ATTRIBUTE)) {
            projection.deleteCharAt(projection.length() - 1);

            for (String attributeName : attributeNames) {
                projection.append(", ");
                projection.append("\"");
                projection.append(ATTRIBUTE_FIELD_PREFIX);
                projection.append(attributeName);
                projection.append("\"");
                projection.append(": 1");
            }

            projection.append(" }");
        }

        return projection.toString();
    }

}
