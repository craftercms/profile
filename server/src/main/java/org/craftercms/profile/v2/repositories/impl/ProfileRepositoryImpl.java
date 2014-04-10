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

import com.mongodb.MongoException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.v2.repositories.ProfileRepository;
import org.jongo.Find;
import org.jongo.FindOne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link org.craftercms.profile.v2.repositories.ProfileRepository}.
 *
 * @author avasquez
 */
public class ProfileRepositoryImpl extends JongoRepository<Profile> implements ProfileRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProfileRepositoryImpl.class);

    public static final String FIELD_TENANT_NAME = "tenantName";

    public static final String KEY_DEFAULT_FIELDS =                     "profile.profile.defaultFields";
    public static final String KEY_FIND_BY_TENANT_AND_USERNAME_QUERY =  "profile.profile.byTenantAndUsername";
    public static final String KEY_COUNT_BY_TENANT_QUERY =              "profile.profile.countByTenant";
    public static final String KEY_REMOVE_BY_TENANT_QUERY =             "profile.profile.removeByTenant";
    public static final String KEY_FIND_BY_IDS_QUERY =                  "profile.profile.byIds";
    public static final String KEY_FIND_BY_TENANT_QUERY =               "profile.profile.byTenant";
    public static final String KEY_FIND_BY_TENANT_AND_ROLE_QUERY =      "profile.profile.byTenantAndRole";
    public static final String KEY_FIND_BY_TENANT_AND_ATTRIB_QUERY =    "profile.profile.byTenantAndAttribute";

    public static final String ATTRIBUTE_FIELD_PREFIX = "attributes.";

    /**
     * Creates a instance of a Jongo Repository.
     */
    public ProfileRepositoryImpl() throws MongoDataException {
        super();
    }

    @Override
    public Profile findById(String id, String... attributesToReturn) throws MongoDataException {
        try {
            if (ArrayUtils.isNotEmpty(attributesToReturn)) {
                String projection = buildProjectionWithAttributes(attributesToReturn);

                return getCollection().findOne(new ObjectId(id)).projection(projection).as(Profile.class);
            } else {
                return getCollection().findOne(new ObjectId(id)).as(Profile.class);
            }
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
    public Iterable<Profile> findByIds(List<String> ids, String sortBy, SortOrder sortOrder,
                                       String... attributesToReturn) throws MongoDataException {
        try {
            String query = getQueryFor(KEY_FIND_BY_IDS_QUERY);
            Find find = getCollection().find(query, ids);

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
    public Iterable<Profile> findByTenantAndAttribute(String tenantName, String attributeName, String attributeValue,
                                                      String sortBy, SortOrder sortOrder, String... attributesToReturn)
            throws MongoDataException {
        try {
            String query = getQueryFor(KEY_FIND_BY_TENANT_AND_ATTRIB_QUERY);
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
    public void removeAllForTenant(String tenantName) throws MongoDataException {
        remove(getQueryFor(KEY_REMOVE_BY_TENANT_QUERY), tenantName);
    }

    @Override
    public String findTenantNameForProfile(String profileId) throws MongoDataException {
        try {
            FindOne findOne = getCollection().findOne(new ObjectId(profileId));
            findOne = findOne.projection("{\"" + FIELD_TENANT_NAME + "\": 1}");
            Map<String, String> tenantNameMap = findOne.as(Map.class);

            return tenantNameMap.get(FIELD_TENANT_NAME);
        } catch (MongoException ex) {
            String msg = "Unable to find tenant name for profile " + profileId;
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        } catch (IllegalArgumentException ex) {
            String msg = "Given id '" + profileId + "' can't be converted to an ObjectId";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    protected Find addSort(Find find, String sortBy, SortOrder sortOrder) {
        if (StringUtils.isNotEmpty(sortBy)) {
            find = find.sort("{" + sortBy + ": " + (sortOrder == SortOrder.ASC ? "1" : "-1") + "}");
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

    protected Find addProjection(Find find, String... attributesToReturn) {
        if (ArrayUtils.isNotEmpty(attributesToReturn)) {
            find = find.projection(buildProjectionWithAttributes(attributesToReturn));
        }

        return find;
    }

    protected String buildProjectionWithAttributes(String... attributeNames) {
        StringBuilder projection = new StringBuilder(getQueryFor(KEY_DEFAULT_FIELDS));

        projection.deleteCharAt(projection.length() - 1);

        for (String attributeName : attributeNames) {
            projection.append(", ");
            projection.append("\"");
            projection.append(ATTRIBUTE_FIELD_PREFIX);
            projection.append(attributeName);
            projection.append("\"");
            projection.append(": 1");
        }

        projection.append("}");

        return projection.toString();
    }

}