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
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.exceptions.TenantException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Default Implementation of tenant repository.
 */
@Component
public class TenantRepositoryImpl extends JongoRepository<Tenant> implements TenantRepository {

    /**
     * Find by Tenant Roles query.
     */
    public static final String PROFILE_TENANT_BY_ROLES = "profile.tenant.byRoles";
    /**
     * Find by Tenant by It's name query.
     */
    public static final String PROFILE_TENANT_BY_NAME = "profile.tenant.byName";
    /**
     * Das Logger.
     */
    private Logger log = LoggerFactory.getLogger(TenantRepositoryImpl.class);

    /**
     * Default Ctr.
     *
     * @throws MongoDataException, If parent couldn't get information of the Tenant class.
     */
    public TenantRepositoryImpl() throws MongoDataException {
    }

    @Override
    public Tenant findTenantById(final ObjectId id) throws TenantException {
        try {
            return findById(id.toString());
        } catch (MongoDataException ex) {
            log.error("Unable to find Tenant by tenantId", ex);
            throw new TenantException("Unable to find tenant by Id " + id.toString(), ex);
        }
    }

    @Override
    public Iterable<Tenant> getTenantRange(final String sortBy, final String sortOrder, final int start,
                                           final int end) throws TenantException {
        log.debug("Getting tenants sorted by {} and order by {} starting at {} ending at {}", sortBy, sortOrder,
            start, end);
        try {
            return getCollection().find().skip(start).limit(end).sort("{" + sortBy + ":1}").as(Tenant.class);
        } catch (MongoException ex) {
            log.error("Getting tenants sorted by {} and order by {} starting at {} ending at {}", sortBy, sortOrder,
                start, end);
            throw new TenantException("Unable to find tenants and sorting them", ex);
        }
    }

    @Override
    public Tenant getTenantByName(final String tenantName) throws TenantException {
        log.debug("Getting tenant by name {}", tenantName);
        String query = getQueryFor(PROFILE_TENANT_BY_NAME);
        try {
            Tenant tenant = findOne(query, tenantName);
            log.debug("Tenant Found {}", tenant);
            return tenant;
        } catch (MongoDataException ex) {
            log.error("Unable to Search tenant by its name");
            throw new TenantException("Unable to search for tenant " + tenantName, ex);
        }
    }

    @Override
    public void setAttribute(final String tenantName, final Attribute attribute) throws TenantException {
        log.debug("Setting tenant {} attribute {}", tenantName, attribute);
        try {
            Tenant tenant = getTenantByName(tenantName);
            tenant.getSchema().getAttributes().add(attribute);
            save(tenant);
        } catch (MongoDataException ex) {
            log.error("Unable to save attribute " + attribute + " to tenant " + tenantName, ex);
            throw new TenantException("Unable to set tenant Attribute", ex);
        }
    }

    @Override
    public void deleteAttribute(final String tenantName, final String attributeName) {
        throw new UnsupportedOperationException();
    }

    @Override

    public Iterable<Tenant> getTenants(final String[] roles) throws TenantException {
        log.debug("Getting tenants by roles {}", roles);
        String query = getQueryFor(PROFILE_TENANT_BY_ROLES);
        try {
            return find(query, roles);
        } catch (MongoDataException ex) {
            log.error("Error Looking for tenants with roles " + StringUtils.join(roles, ","), ex);
            throw new TenantException("Unable to get Tenant by roles", ex);
        }

    }

    @Override
    public long countTenantsWithRoles(final String[] roles) throws TenantException {
        log.debug("Counting tenants with roles {}", roles);
        String query = getQueryFor(PROFILE_TENANT_BY_ROLES);
        try {
            long total = getCollection().count(query, roles);
            log.debug("Found {} tenants with roles {}", total, roles);
            return total;
        } catch (MongoException ex) {
            String rolesString = StringUtils.join(roles);
            log.error("Unable to count tenants with roles " + rolesString, ex);
            throw new TenantException("Unable to count tenants with roles" + rolesString, ex);
        }
    }

    @Override
    public void deleteTenant(final ObjectId tenantId) throws TenantException {
        try {
            log.debug("Deleting tenant with tenantId {}", tenantId);
            WriteResult writeResult = getCollection().remove(tenantId);
            checkCommandResult(writeResult);
        } catch (MongoDataException ex) {
            log.debug("Unable to delete Tenant with tenantId " + tenantId.toString(), ex);
            throw new TenantException("Unable to delete tenant", ex);
        }
    }

    @Override
    public long count() throws TenantException {
        try {
            log.debug("Counting tenants");
            long total = getCollection().count();
            log.debug("Found {} tenants", total);
            return total;
        } catch (MongoException ex) {
            log.debug("Unable to count tenants");
            throw new TenantException("Unable count tenants", ex);

        }
    }


    //    @Override
    //    public List<Tenant> getTenantRange(String sortBy, String sortOrder, int start, int end) {
    //        Query query = new Query();
    //
    //        if (sortBy != null) {
    //            String sortingBy = sortBy;
    //            if (!Arrays.asList(ProfileConstants.TENANT_ORDER_BY_FIELDS).contains(sortBy)) {
    //                sortingBy = ProfileConstants.TENANT_NAME;
    //            }
    //
    //            if (sortOrder != null) {
    //                query.sort().on(sortingBy, sortOrder.equalsIgnoreCase(ProfileConstants.SORT_ORDER_DESC)? Order
    //                    .DESCENDING: Order.ASCENDING);
    //            } else {
    //                query.sort().on(sortingBy, Order.ASCENDING);
    //            }
    //        }
    //
    //        query.skip(start);
    //        query.limit(end > start? (end - start + 1): 0);
    //
    //        return mongoTemplate.find(query, Tenant.class);
    //    }
    //
    //    @Override
    //    public List<Tenant> getTenants(String[] roles) {
    //        Query query = new Query();
    //
    //
    //        if (roles != null && roles.length > 0) {
    //            query.addCriteria(Criteria.where("roles").in(roles));
    //        }
    //
    //        return mongoTemplate.find(query, Tenant.class);
    //    }
    //
    //    @Override
    //    public Tenant getTenantByName(String tenantName) {
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));
    //        return mongoTemplate.findOne(query, Tenant.class);
    //    }
    //
    //    @Override
    //    public void setAttribute(String tenantName, Attribute attribute) {
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));
    //        Update update = new Update();
    //        Tenant t = getTenantByName(tenantName);
    //        if (t != null) {
    //            Iterator<Attribute> i = t.getSchema().getAttributes().iterator();
    //            boolean added = false;
    //            int pos = 0;
    //            while (i.hasNext()) {
    //                if (i.next().getName().equals(attribute.getName())) {
    //                    i.remove();
    //                    t.getSchema().getAttributes().add(pos, attribute);
    //                    added = true;
    //                    break;
    //                }
    //                pos++;
    //            }
    //            if (!added) {
    //                t.getSchema().getAttributes().add(attribute);
    //            }
    //        }
    //
    //        update = update.set(ProfileConstants.SCHEMA, t.getSchema());
    //        mongoTemplate.updateFirst(query, update, Tenant.class);
    //    }
    //
    //    @Override
    //    public void deleteAttribute(String tenantName, String attributeName) {
    //        Query query = new Query();
    //        query.addCriteria(Criteria.where(ProfileConstants.TENANT_NAME).is(tenantName));
    //        Update update = new Update();
    //        Tenant t = getTenantByName(tenantName);
    //        if (t != null) {
    //            Iterator<Attribute> i = t.getSchema().getAttributes().iterator();
    //            while (i.hasNext()) {
    //                if (i.next().getName().equals(attributeName)) {
    //                    i.remove();
    //                    break;
    //                }
    //            }
    //        }
    //        update = update.set(ProfileConstants.SCHEMA, t.getSchema());
    //        mongoTemplate.updateFirst(query, update, Tenant.class);
    //    }
    //
    //


}
