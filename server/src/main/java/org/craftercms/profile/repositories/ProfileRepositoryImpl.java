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
package org.craftercms.profile.repositories;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


public class ProfileRepositoryImpl implements ProfileRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Profile> getProfileRange(String tenantName, String sortBy, String sortOrder,
                                         List<String> attributesList, int start, int end) {
        Query query = new Query();
        query.fields().include(ProfileConstants.USER_NAME);
        query.fields().include(ProfileConstants.EMAIL);
        query.fields().include(ProfileConstants.PASSWORD);
        query.fields().include(ProfileConstants.ACTIVE);
        query.fields().include(ProfileConstants.CREATED);
        query.fields().include(ProfileConstants.MODIFIED);
        query.fields().include(ProfileConstants.TENANT_NAME);
        query.fields().include(ProfileConstants.ROLES);

        if (attributesList != null) {
            for (String attribute : attributesList) {
                query.fields().include(ProfileConstants.ATTRIBUTES_DOT + attribute);
            }
        }

        if (sortBy != null) {
            String sorting = sortBy;
            if (!Arrays.asList(ProfileConstants.DOMAIN_PROFILE_FIELDS).contains(sortBy)) {
                sorting = ProfileConstants.ATTRIBUTES_DOT + sortBy;
            }

            if (sortOrder != null) {
                query.sort().on(sorting, sortOrder.equalsIgnoreCase(ProfileConstants.SORT_ORDER_DESC)? Order
                    .DESCENDING: Order.ASCENDING);
            } else {
                query.sort().on(sorting, Order.ASCENDING);
            }
        }

        query.skip(start);
        query.limit(end > start? (end - start + 1): 0);
        query = query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));

        return mongoTemplate.find(query, Profile.class);
    }

    @Override
    public long getProfilesCount(String tenantName) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));
        return mongoTemplate.count(query, Profile.class);
    }

    public Profile getProfile(String profileId) {
        Query query = new Query();
        query.fields().include(ProfileConstants.USER_NAME);
        query.fields().include(ProfileConstants.EMAIL);
        query.fields().include(ProfileConstants.PASSWORD);
        query.fields().include(ProfileConstants.ACTIVE);
        query.fields().include(ProfileConstants.CREATED);
        query.fields().include(ProfileConstants.MODIFIED);
        query.fields().include(ProfileConstants.TENANT_NAME);
        query.fields().include(ProfileConstants.ROLES);
        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));

        return mongoTemplate.findOne(query, Profile.class);
    }

    public Profile getProfile(String profileId, List<String> attributes) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));

        query.fields().include(ProfileConstants.USER_NAME);
        query.fields().include(ProfileConstants.PASSWORD);
        query.fields().include(ProfileConstants.EMAIL);
        query.fields().include(ProfileConstants.ACTIVE);
        query.fields().include(ProfileConstants.CREATED);
        query.fields().include(ProfileConstants.MODIFIED);
        query.fields().include(ProfileConstants.TENANT_NAME);
        query.fields().include(ProfileConstants.ROLES);
        if (attributes != null) {
            for (String attribute : attributes) {
                query.fields().include(ProfileConstants.ATTRIBUTES_DOT + attribute);
            }
        }

        return mongoTemplate.findOne(query, Profile.class);
    }

    public List<Profile> getProfiles(List<String> profileIdList) {
        Query query = new Query();
        query.fields().include(ProfileConstants.USER_NAME);
        query.fields().include(ProfileConstants.PASSWORD);
        query.fields().include(ProfileConstants.EMAIL);
        query.fields().include(ProfileConstants.ACTIVE);
        query.fields().include(ProfileConstants.CREATED);
        query.fields().include(ProfileConstants.MODIFIED);
        query.fields().include(ProfileConstants.TENANT_NAME);
        query.fields().include(ProfileConstants.ROLES);
        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).in(profileIdList));

        return mongoTemplate.find(query, Profile.class);
    }

    @Override
    public Profile getProfileByUserName(String userName, String tenantName) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ProfileConstants.USER_NAME).is(userName));
        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));

        query.fields().include(ProfileConstants.USER_NAME);
        query.fields().include(ProfileConstants.EMAIL);
        query.fields().include(ProfileConstants.PASSWORD);
        query.fields().include(ProfileConstants.ACTIVE);
        query.fields().include(ProfileConstants.CREATED);
        query.fields().include(ProfileConstants.MODIFIED);
        query.fields().include(ProfileConstants.TENANT_NAME);
        query.fields().include(ProfileConstants.ROLES);
        return mongoTemplate.findOne(query, Profile.class);
    }

    @Override
    public Profile getProfileByUserName(String userName, String tenantName, List<String> attributes) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ProfileConstants.USER_NAME).is(userName));
        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));

        query.fields().include(ProfileConstants.USER_NAME);
        query.fields().include(ProfileConstants.EMAIL);
        query.fields().include(ProfileConstants.PASSWORD);
        query.fields().include(ProfileConstants.ACTIVE);
        query.fields().include(ProfileConstants.CREATED);
        query.fields().include(ProfileConstants.MODIFIED);
        query.fields().include(ProfileConstants.TENANT_NAME);
        query.fields().include(ProfileConstants.ROLES);
        if (attributes != null) {
            for (String attribute : attributes) {
                query.fields().include(ProfileConstants.ATTRIBUTES_DOT + attribute);
            }
        }

        return mongoTemplate.findOne(query, Profile.class);
    }

    @Override
    public Profile getProfileByUserNameWithAllAttributes(String userName, String tenantName) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ProfileConstants.USER_NAME).is(userName));
        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));

        query.fields().include(ProfileConstants.USER_NAME);
        query.fields().include(ProfileConstants.EMAIL);
        query.fields().include(ProfileConstants.PASSWORD);
        query.fields().include(ProfileConstants.ACTIVE);
        query.fields().include(ProfileConstants.CREATED);
        query.fields().include(ProfileConstants.MODIFIED);
        query.fields().include(ProfileConstants.ATTRIBUTES);
        query.fields().include(ProfileConstants.TENANT_NAME);
        query.fields().include(ProfileConstants.ROLES);
        return mongoTemplate.findOne(query, Profile.class);
    }

    @Override
    public List<Profile> getProfilesWithAttributes(List<String> profileIdList) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).in(profileIdList));

        return mongoTemplate.find(query, Profile.class);
    }

    @Override
    public List<Profile> getProfilesByTenantName(String tenantName) {

        Query query = new Query();
        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));


        return mongoTemplate.find(query, Profile.class);
    }

    @Override
    public void setAttributes(String profileId, Map<String, Serializable> attributes) {
        Query query = new Query();
        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));
        Update update = new Update();

        if (!attributes.isEmpty() && attributes.keySet() != null) {
            Iterator it = attributes.keySet().iterator();

            while (it.hasNext()) {
                String key = (String)it.next();
                String value = (String)attributes.get(key);
                update.set(ProfileConstants.ATTRIBUTES_DOT + key, value);
            }
        }

        mongoTemplate.updateFirst(query, update, Profile.class);
    }

    @Override
    public Map<String, Serializable> getAllAttributes(String profileId) {
        Map<String, Serializable> result = new HashMap<String, Serializable>();
        Query query = new Query();
        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));
        query.fields().include(ProfileConstants.ATTRIBUTES);

        Profile profile = mongoTemplate.findOne(query, Profile.class);
        if (profile != null) {
            return profile.getAttributes();
        }

        return result;
    }

    public Map<String, Serializable> getAttributes(String profileId, List<String> attributes) {
        Map<String, Serializable> result = new HashMap<String, Serializable>();
        Query query = new Query();
        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));

        if (attributes != null) {
            for (String attribute : attributes) {
                query.fields().include(ProfileConstants.ATTRIBUTES_DOT + attribute);
            }
        }

        Profile profile = mongoTemplate.findOne(query, Profile.class);
        if (profile != null) {
            return profile.getAttributes();
        }

        return result;
    }

    @Override
    public Map<String, Serializable> getAttribute(String profileId, String attributeKey) {
        Map<String, Serializable> result = new HashMap<String, Serializable>();
        Query query = new Query();
        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));
        query.fields().include(ProfileConstants.ATTRIBUTES_DOT + attributeKey);

        Profile profile = mongoTemplate.findOne(query, Profile.class);
        if (profile != null) {
            return profile.getAttributes();
        }

        return result;
    }

    @Override
    public void deleteAllAttributes(String profileId) {
        Query query = new Query();
        Update update = new Update();
        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));
        update.unset(ProfileConstants.ATTRIBUTES);

        mongoTemplate.updateFirst(query, update, Profile.class);
    }

    @Override
    public void deleteAttributes(String profileId, List<String> attributes) {
        Query query = new Query();
        Update update = new Update();
        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));

        if (attributes != null) {
            for (String attribute : attributes) {
                update.unset(ProfileConstants.ATTRIBUTES_DOT + attribute);
            }
        }

        mongoTemplate.updateFirst(query, update, Profile.class);
    }

    @Override
    public void deleteRole(String profileId, String roleName) {
        Query query = new Query();

        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(new ObjectId(profileId)));
        mongoTemplate.upsert(query, new Update().pull("roles", roleName), Profile.class);

    }

}