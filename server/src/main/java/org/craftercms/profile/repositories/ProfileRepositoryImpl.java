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

import com.mongodb.MongoException;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.collections.MapUtils;
import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.ProfileException;
import org.jongo.Find;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Default implementation of Profile Repository Impl.
 *
 * @author Carlos Ortiz.
 * @author Alvaro Gonzales.
 */
public class ProfileRepositoryImpl extends JongoRepository<Profile> implements ProfileRepository {
    /**
     * Find by Role and tenant.
     */
    public static final String PROFILE_PROFILES_GET_BY_ROLES_AND_TENANT = "profile.profiles.getByRolesAndTenant";
    /**
     * Find by Tenant Name.
     */
    public static final String PROFILE_PROFILES_BY_TENANT_NAME = "profile.profiles.byTenantName";
    /**
     * Default Profiles return fields.
     */
    public static final String PROFILE_DEFAULT_RETURN_FIELDS = "profile.profiles.defaultFields";
    /**
     * Find By Id.
     */
    public static final String PROFILE_PROFILES_BY_ID = "profile.profiles.byId";
    public static final String PROFILE_PROFILE_BY_IDS = "profile.profile.byIds";
    public static final String PROFILE_PROFILE_BY_USERNAME = "profile.profile.ByUsername";
    public static final String PROFILE_PROFILE_BY_TENANT = "profile.profile.byTenant";
    public static final String PROFILE_PROFILE_REMOVE_ROLE = "profile.profile.removeRole";
    public static final String PROFILE_PROFILE_BY_ATTRIBUTE_VALUE = "profile.profile.byAttributeValue";
    /**
     * Das Logger.
     */
    private Logger log = LoggerFactory.getLogger(ProfileRepositoryImpl.class);

    /**
     * Creates A instance of a Jongo Repository.
     */
    public ProfileRepositoryImpl() throws MongoDataException {
    }


    @Override
    public Profile findById(final ObjectId id) throws ProfileException {
        log.debug("Finding profile with id {}", id);
        try {
            return super.findById(id.toString());
        } catch (MongoDataException ex) {
            log.error("Unable to search for profile with id" + id, ex);
            throw new ProfileException("Unable to find profile by Id", ex);
        }
    }

    @Override
    public Iterable<Profile> findByRolesAndTenantName(final String role, final String tenantName) throws
        ProfileException {
        log.debug("Finding profiles with role {} and tenant {}", role, tenantName);
        try {
            log.debug("Finding Tenants with Roles {} and Tenant {}", role, tenantName);
            String query = getQueryFor(PROFILE_PROFILES_GET_BY_ROLES_AND_TENANT);
            return find(prepareProfileQuery(query, null), role, tenantName);
        } catch (MongoDataException ex) {
            log.error("Unable to find Profile by Tenant \"" + tenantName + "\" and Roles \"" + role + "\"", ex);
            throw new ProfileException("Unable to search for profile by tenant and roles", ex);
        }
    }

    @Override
    public Iterable<Profile> getProfileRange(final String tenantName, final String sortBy, final String sortOrder,
                                             final List<String> attributesList, final int start,
                                             final int end) throws ProfileException {
        log.debug("Getting profiles for tenant {} ,sorted by {},ordering by {} start {} end {} with attributes {}",
            tenantName, sortBy, sortOrder, start, end, attributesList);
        try {
            String query = getQueryFor(PROFILE_PROFILES_BY_TENANT_NAME);
            String baseQuery = prepareProfileQuery(query, attributesList);
            Find mongoQuery = getCollection().find(baseQuery, tenantName).sort("{" + sortBy + ":1}").skip(start)
                .limit(end);
            return mongoQuery.as(Profile.class);
        } catch (MongoException ex) {
            log.debug("Getting profiles for tenant {} ,sorted by {},ordering by {} start {} end {} with attributes " +
                "{}", tenantName, sortBy, sortOrder, start, end, attributesList);
            log.error("Unable to find by range profiles", ex);
            throw new ProfileException("Unable to find profiles by range", ex);
        }


    }

    @Override
    public long getProfilesCount(final String tenantName) throws ProfileException {
        log.debug("Counting profiles for tenant {}", tenantName);
        try {
            String query = getQueryFor(PROFILE_PROFILES_BY_TENANT_NAME);
            return getCollection().count(query, tenantName);
        } catch (MongoException ex) {
            log.error("Unable to count profiles for tenant " + tenantName, ex);
            throw new ProfileException("Unable to count profiles for the given tenant.", ex);
        }
    }

    @Override
    public Profile getProfile(final String profileId) throws ProfileException {
        return getProfile(profileId, null);
    }

    @Override
    public Profile getProfile(final String profileId, final List<String> attributes) throws ProfileException {
        log.debug("Getting tenant with profile id {} and attributes {} (*null for all)", profileId, attributes);
        try {
            String query = getQueryFor(PROFILE_PROFILES_BY_ID);
            return findOne(prepareProfileQuery(query, attributes), profileId);
        } catch (MongoDataException ex) {
            log.error("Unable to search by Id " + profileId + "and attributes" + StringUtils.join(attributes, ","), ex);
            throw new ProfileException("Unable to search by profile id", ex);
        }
    }


    @Override
    public Iterable<Profile> getProfiles(final List<String> profileIdList) throws ProfileException {
        log.debug("Getting profiles using id list {}", profileIdList);
        try {
            return findAll();
        } catch (MongoDataException ex) {
            log.error("Unable to search for all profiles", ex);
            throw new ProfileException("Unable to search all profiles", ex);
        }
    }

    @Override
    public Iterable<Profile> getProfilesWithAttributes(final List<String> profileIdList) throws ProfileException {
        log.debug("Getting profiles from list {} and all attributes", profileIdList);
        try {
            String query = getQueryFor(PROFILE_PROFILE_BY_IDS);
            return find(query, profileIdList);
        } catch (MongoDataException ex) {
            log.error("Unable to search for all profiles", ex);
            throw new ProfileException("Unable to search all profiles", ex);
        }
    }

    @Override
    public void setAttributes(final String profileId, final Map<String, Object> attributes) throws ProfileException {
        log.debug("Setting attributes {} for profile Id {}", attributes, profileId);
        try {
            Profile profile = getProfile(profileId);
            profile.setAttributes(MapUtils.deepMerge(profile.getAttributes(), attributes));
            save(profile);
        } catch (MongoDataException ex) {
            log.error("Unable save attributes for profile " + profileId + " attribute " + attributes, ex);
            throw new ProfileException("Unable to save profile attributes", ex);
        }
    }

    @Override
    public Map<String, Object> getAllAttributes(final String profileId) throws ProfileException {
        log.debug("Getting attributes for profile ID {}", profileId);
        return org.apache.commons.collections4.MapUtils.unmodifiableMap(getProfile(profileId).getAttributes());
    }

    //TODO make filtering as a mongo query
    @Override
    public Map<String, Object> getAttributes(final String profileId, final List<String> attributes) throws
        ProfileException {
        log.debug("Getting attributes {} for profile {}", attributes, profileId);
        Map<String, Object> filteredAttr = new HashMap<>(attributes.size());
        Map<String, Object> originalAttr = getProfile(profileId).getAttributes();
        for (String attribute : attributes) {
            filteredAttr.put(attribute, originalAttr.get(attribute));
        }
        return org.apache.commons.collections4.MapUtils.unmodifiableMap(filteredAttr);
    }

    @Override
    public Map<String, Object> getAttribute(final String profileId, final String attributeKey) throws ProfileException {
        return getAttributes(profileId, Arrays.asList(attributeKey));
    }

    @Override
    public void deleteAllAttributes(final String profileId) throws ProfileException {
        log.debug("Removing all profile's {} attributes", profileId);
        try {
            Profile profile = getProfile(profileId);
            profile.getAttributes().clear();
            save(profile);
        } catch (MongoDataException ex) {
            log.debug("Unable to delete profile " + profileId + " attributes", ex);
            throw new ProfileException("Unable to delete profile Attributes", ex);
        }
    }

    @Override
    public void deleteAttributes(final String profileId, final List<String> attributesName) throws ProfileException {
        log.debug("Deleting profile's {} attribute {}", profileId, attributesName);
        try {
            String query = deleteAttributesQuery(attributesName);
            update(profileId, query, false, false);
        } catch (MongoDataException ex) {
            log.error("Unable to delete profile " + profileId + " attributes " + StringUtils.join(attributesName), ex);
            throw new ProfileException("Unable to delete profile Attributes", ex);
        }
    }

    @Override
    public Profile getProfileByUserName(final String userName, final String tenantName) throws ProfileException {
        return getProfileByUserName(userName, tenantName, null);
    }

    @Override
    public Profile getProfileByUserName(final String userName, final String tenantName,
                                        final List<String> attributes) throws ProfileException {
        log.debug("Getting Profile by username {} and tenant {} with attributes {} (null for all)", userName,
            tenantName, attributes);
        try {
            String query = getQueryFor(PROFILE_PROFILE_BY_USERNAME);
            return findOne(prepareProfileQuery(query, attributes), userName, tenantName);
        } catch (MongoDataException ex) {
            log.error("Unable to find profile by username " + userName + " and tenant " + tenantName, ex);
            throw new ProfileException("Unable to find profile by username and tenant", ex);
        }
    }

    @Override
    public Iterable<Profile> getProfilesByTenantName(final String tenantName) throws ProfileException {
        try {
            String query = getQueryFor(PROFILE_PROFILE_BY_TENANT);
            return find(query, tenantName);
        } catch (MongoDataException ex) {
            log.error("Unable to find tenant " + tenantName + " profiles", ex);
            throw new ProfileException("Unable to find profiles by tenant", ex);
        }
    }

    @Override
    public void deleteRole(final String profileId, final String roleName) throws ProfileException {
        log.debug("Removing role {} for profile {}", roleName, profileId);
        try {
            String query = getQueryFor(PROFILE_PROFILE_REMOVE_ROLE);
            update(profileId, query, false, false, roleName);
        } catch (MongoDataException ex) {
            log.error("Unable to delete role " + roleName + " from profile " + profileId, ex);
            throw new ProfileException("Unable to remove role from profile", ex);
        }
    }

    @Override

    public Iterable<Profile> findByAttributeAndValue(final String attribute, final String attributeValue) throws
        ProfileException {
        log.debug("Getting list of Profiles that have attribute {} = {}", attribute, attributeValue);
        try {
            String query = getQueryFor(PROFILE_PROFILE_BY_ATTRIBUTE_VALUE);

            return find(query, attribute, attributeValue);
        } catch (MongoDataException ex) {
            log.error("Unable to find profiles with attribute " + attribute + " and value " + attributeValue, ex);
            throw new ProfileException("Unable to find profiles with attribute and value", ex);
        }
    }

    @Override
    public void delete(final Iterable<Profile> profiles) throws ProfileException {
        log.debug("Deleting profiles {}", profiles);
        try {
            log.debug("Getting profile id's to delete");
            ArrayList<ObjectId> toDelete = new ArrayList<>();
            for (Profile profile : profiles) {
                toDelete.add(profile.getId());
            }
            String query = getQueryFor(PROFILE_PROFILE_BY_IDS);
            remove(query, toDelete);
        } catch (MongoDataException ex) {
            log.debug("Unable to delete profiles " + StringUtils.join(profiles), ex);
            throw new ProfileException("Unable to delete profiles in list", ex);
        }
    }

    /**
     * Generates a unset using . annotation.
     *
     * @param attributesToDelete name of the attributes to be deleted, if a value is a nested document the sould
     *                           include the parent and child.
     * @return A string
     */
    private String deleteAttributesQuery(final List<String> attributesToDelete) {
        log.debug("Creating query for unset attributes {}", attributesToDelete);
        StringBuilder builder = new StringBuilder("{$unset:{");
        Iterator<String> iter = attributesToDelete.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            builder.append(ProfileConstants.ATTRIBUTES_DOT);
            builder.append(key);
            builder.append(":1");
            if (iter.hasNext()) {
                builder.append(",");
            }
        }
        builder.append("}}");
        log.debug("Generated query {}", builder.toString());
        return builder.toString();
    }

    /**
     * Sort of simple way to append query + Return values.
     *
     * @param query      Base Query (the query that does the filtering
     * @param attributes Attributes (to be return) if null or empty ignore.
     * @return The complete Query to be send to mongo using jongo.
     */
    private String prepareProfileQuery(final String query, final List<String> attributes) {
        StringBuilder builder = new StringBuilder(query);
        String defaultFields = getQueryFor(PROFILE_DEFAULT_RETURN_FIELDS);
        if (attributes == null || attributes.isEmpty()) {
            builder.append(",");
            builder.append(defaultFields);
            return builder.toString();
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(",");
        ListIterator<String> iter = attributes.listIterator();
        while (iter.hasNext()) {
            String attribute = iter.next();
            builder.append("\"");
            builder.append(ProfileConstants.ATTRIBUTES_DOT);
            builder.append(attribute);
            builder.append("\"");
            builder.append(":1");

            if (iter.hasNext()) {
                builder.append(",");
            }
        }
        builder.append("}");
        return builder.toString();
    }


    //    @Autowired
    //    private MongoTemplate mongoTemplate;
    //
    //    public List<Profile> getProfileRange(String tenantName, String sortBy, String sortOrder,
    //                                         List<String> attributesList, int start, int end) {
    //        Query query = new Query();
    //        query.fields().include(ProfileConstants.USER_NAME);
    //        query.fields().include(ProfileConstants.EMAIL);
    //        query.fields().include(ProfileConstants.PASSWORD);
    //        query.fields().include(ProfileConstants.ACTIVE);
    //        query.fields().include(ProfileConstants.CREATED);
    //        query.fields().include(ProfileConstants.MODIFIED);
    //        query.fields().include(ProfileConstants.TENANT_NAME);
    //        query.fields().include(ProfileConstants.ROLES);
    //
    //        if (attributesList != null) {
    //            for (String attribute : attributesList) {
    //                query.fields().include(ProfileConstants.ATTRIBUTES_DOT + attribute);
    //            }
    //        }
    //
    //        if (sortBy != null) {
    //            String sorting = sortBy;
    //            if (!Arrays.asList(ProfileConstants.DOMAIN_PROFILE_FIELDS).contains(sortBy)) {
    //                sorting = ProfileConstants.ATTRIBUTES_DOT + sortBy;
    //            }
    //
    //            if (sortOrder != null) {
    //                query.sort().on(sorting, sortOrder.equalsIgnoreCase(ProfileConstants.SORT_ORDER_DESC)? Order
    //                    .DESCENDING: Order.ASCENDING);
    //            } else {
    //                query.sort().on(sorting, Order.ASCENDING);
    //            }
    //        }
    //
    //        query.skip(start);
    //        query.limit(end > start? (end - start + 1): 0);
    //        query = query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));
    //
    //        return mongoTemplate.find(query, Profile.class);
    //    }
    //
    //    @Override
    //    public long getProfilesCount(String tenantName) {
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));
    //        return mongoTemplate.count(query, Profile.class);
    //    }
    //
    //    public Profile getProfile(String profileId) {
    //        Query query = new Query();
    //        query.fields().include(ProfileConstants.USER_NAME);
    //        query.fields().include(ProfileConstants.EMAIL);
    //        query.fields().include(ProfileConstants.PASSWORD);
    //        query.fields().include(ProfileConstants.ACTIVE);
    //        query.fields().include(ProfileConstants.CREATED);
    //        query.fields().include(ProfileConstants.MODIFIED);
    //        query.fields().include(ProfileConstants.TENANT_NAME);
    //        query.fields().include(ProfileConstants.ROLES);
    //        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));
    //
    //        return mongoTemplate.findOne(query, Profile.class);
    //    }
    //
    //    public Profile getProfile(String profileId, List<String> attributes) {
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));
    //
    //        query.fields().include(ProfileConstants.USER_NAME);
    //        query.fields().include(ProfileConstants.PASSWORD);
    //        query.fields().include(ProfileConstants.EMAIL);
    //        query.fields().include(ProfileConstants.ACTIVE);
    //        query.fields().include(ProfileConstants.CREATED);
    //        query.fields().include(ProfileConstants.MODIFIED);
    //        query.fields().include(ProfileConstants.TENANT_NAME);
    //        query.fields().include(ProfileConstants.ROLES);
    //        if (attributes != null) {
    //            for (String attribute : attributes) {
    //                query.fields().include(ProfileConstants.ATTRIBUTES_DOT + attribute);
    //            }
    //        }
    //
    //        return mongoTemplate.findOne(query, Profile.class);
    //    }
    //
    //    public List<Profile> getProfiles(List<String> profileIdList) {
    //        Query query = new Query();
    //        query.fields().include(ProfileConstants.USER_NAME);
    //        query.fields().include(ProfileConstants.PASSWORD);
    //        query.fields().include(ProfileConstants.EMAIL);
    //        query.fields().include(ProfileConstants.ACTIVE);
    //        query.fields().include(ProfileConstants.CREATED);
    //        query.fields().include(ProfileConstants.MODIFIED);
    //        query.fields().include(ProfileConstants.TENANT_NAME);
    //        query.fields().include(ProfileConstants.ROLES);
    //        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).in(profileIdList));
    //
    //        return mongoTemplate.find(query, Profile.class);
    //    }
    //
    //    @Override
    //    public Profile getProfileByUserName(String userName, String tenantName) {
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.USER_NAME).is(userName));
    //        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));
    //
    //        query.fields().include(ProfileConstants.USER_NAME);
    //        query.fields().include(ProfileConstants.EMAIL);
    //        query.fields().include(ProfileConstants.PASSWORD);
    //        query.fields().include(ProfileConstants.ACTIVE);
    //        query.fields().include(ProfileConstants.CREATED);
    //        query.fields().include(ProfileConstants.MODIFIED);
    //        query.fields().include(ProfileConstants.TENANT_NAME);
    //        query.fields().include(ProfileConstants.ROLES);
    //        return mongoTemplate.findOne(query, Profile.class);
    //    }
    //
    //    @Override
    //    public Profile getProfileByUserName(String userName, String tenantName, List<String> attributes) {
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.USER_NAME).is(userName));
    //        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));
    //
    //        query.fields().include(ProfileConstants.USER_NAME);
    //        query.fields().include(ProfileConstants.EMAIL);
    //        query.fields().include(ProfileConstants.PASSWORD);
    //        query.fields().include(ProfileConstants.ACTIVE);
    //        query.fields().include(ProfileConstants.CREATED);
    //        query.fields().include(ProfileConstants.MODIFIED);
    //        query.fields().include(ProfileConstants.TENANT_NAME);
    //        query.fields().include(ProfileConstants.ROLES);
    //        if (attributes != null) {
    //            for (String attribute : attributes) {
    //                query.fields().include(ProfileConstants.ATTRIBUTES_DOT + attribute);
    //            }
    //        }
    //
    //        return mongoTemplate.findOne(query, Profile.class);
    //    }
    //
    //    @Override
    //    public Profile getProfileByUserNameWithAllAttributes(String userName, String tenantName) {
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.USER_NAME).is(userName));
    //        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));
    //
    //        query.fields().include(ProfileConstants.USER_NAME);
    //        query.fields().include(ProfileConstants.EMAIL);
    //        query.fields().include(ProfileConstants.PASSWORD);
    //        query.fields().include(ProfileConstants.ACTIVE);
    //        query.fields().include(ProfileConstants.CREATED);
    //        query.fields().include(ProfileConstants.MODIFIED);
    //        query.fields().include(ProfileConstants.ATTRIBUTES);
    //        query.fields().include(ProfileConstants.TENANT_NAME);
    //        query.fields().include(ProfileConstants.ROLES);
    //        return mongoTemplate.findOne(query, Profile.class);
    //    }
    //
    //    @Override
    //    public List<Profile> getProfilesWithAttributes(List<String> profileIdList) {
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).in(profileIdList));
    //
    //        return mongoTemplate.find(query, Profile.class);
    //    }
    //
    //    @Override
    //    public List<Profile> getProfilesByTenantName(String tenantName) {
    //
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));
    //
    //
    //        return mongoTemplate.find(query, Profile.class);
    //    }
    //
    //    @Override
    //    public void setAttributes(String profileId, Map<String, Serializable> attributes) {
    //        if (attributes != null && !attributes.isEmpty()) {
    //            Query query = new Query();
    //            query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));
    //            Update update = new Update();
    //
    //            Iterator it = attributes.keySet().iterator();
    //            while (it.hasNext()) {
    //                String key = (String)it.next();
    //                update.set(ProfileConstants.ATTRIBUTES_DOT + key, attributes.get(key));
    //            }
    //
    //            mongoTemplate.updateFirst(query, update, Profile.class);
    //        }
    //    }
    //
    //    @Override
    //    public Map<String, Serializable> getAllAttributes(String profileId) {
    //        Map<String, Serializable> result = new HashMap<String, Serializable>();
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));
    //        query.fields().include(ProfileConstants.ATTRIBUTES);
    //
    //        Profile profile = mongoTemplate.findOne(query, Profile.class);
    //        if (profile != null) {
    //            return profile.getAttributes();
    //        }
    //
    //        return result;
    //    }
    //
    //    public Map<String, Serializable> getAttributes(String profileId, List<String> attributes) {
    //        Map<String, Serializable> result = new HashMap<String, Serializable>();
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));
    //
    //        if (attributes != null) {
    //            for (String attribute : attributes) {
    //                query.fields().include(ProfileConstants.ATTRIBUTES_DOT + attribute);
    //            }
    //        }
    //
    //        Profile profile = mongoTemplate.findOne(query, Profile.class);
    //        if (profile != null) {
    //            return profile.getAttributes();
    //        }
    //
    //        return result;
    //    }
    //
    //    @Override
    //    public Map<String, Serializable> getAttribute(String profileId, String attributeKey) {
    //        Map<String, Serializable> result = new HashMap<String, Serializable>();
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));
    //        query.fields().include(ProfileConstants.ATTRIBUTES_DOT + attributeKey);
    //
    //        Profile profile = mongoTemplate.findOne(query, Profile.class);
    //        if (profile != null) {
    //            return profile.getAttributes();
    //        }
    //
    //        return result;
    //    }
    //
    //    @Override
    //    public void deleteAllAttributes(String profileId) {
    //        Query query = new Query();
    //        Update update = new Update();
    //        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));
    //        update.unset(ProfileConstants.ATTRIBUTES);
    //
    //        mongoTemplate.updateFirst(query, update, Profile.class);
    //    }
    //
    //    @Override
    //    public void deleteAttributes(String profileId, List<String> attributes) {
    //        Query query = new Query();
    //        Update update = new Update();
    //        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(profileId));
    //
    //        if (attributes != null) {
    //            for (String attribute : attributes) {
    //                update.unset(ProfileConstants.ATTRIBUTES_DOT + attribute);
    //            }
    //        }
    //
    //        mongoTemplate.updateFirst(query, update, Profile.class);
    //    }
    //
    //    @Override
    //    public void deleteRole(String profileId, String roleName) {
    //        Query query = new Query();
    //
    //        query.addCriteria(Criteria.where(ProfileConstants.FIELD_ID).is(new ObjectId(profileId)));
    //        mongoTemplate.upsert(query, new Update().pull("roles", roleName), Profile.class);
    //
    //    }
    //
    //    @Override
    //    public List<Profile> findByAttributeAndValue(final String attribute, final String attributeValue) {
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.ATTRIBUTES_DOT + attribute).is(attributeValue));
    //        return mongoTemplate.find(query,Profile.class);
    //    }

}