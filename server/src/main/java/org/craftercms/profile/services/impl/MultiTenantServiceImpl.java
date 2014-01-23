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
package org.craftercms.profile.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.domain.Schema;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.exceptions.TenantException;
import org.craftercms.profile.exceptions.TicketException;
import org.craftercms.profile.repositories.TenantRepository;
import org.craftercms.profile.services.MultiTenantService;
import org.craftercms.profile.services.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

/**
 * Default MultiTenantService implementation.
 */
@Component
public class MultiTenantServiceImpl implements MultiTenantService {

    private final Logger log = LoggerFactory.getLogger(MultiTenantServiceImpl.class);
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private ProfileService profileService;

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#createTenant(java.lang.String, boolean,
     * java.util.List, java.util.List, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public Tenant createTenant(final String tenantName, final boolean createDefaults, final List<String> roles,
                               final List<String> domains, final boolean emailNewProfile) throws TenantException {

        Schema schema = new Schema();
        schema.setAttributes(new ArrayList<Attribute>());

        Tenant tenant = new Tenant();
        tenant.setTenantName(tenantName);
        tenant.setSchema(schema);
        tenant.setRoles(roles);
        tenant.setDomains(domains);
        tenant.setEmailNewProfile(emailNewProfile);

        Tenant current = null;
        try {
            tenantRepository.save(tenant);
        } catch (DuplicateKeyException | MongoDataException e) {
            log.error("Unable to create a Tenant " + tenant, e);
            throw new TenantException("Unable to create tenant", e);
        }
        return current;
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#createTenant(java.lang.String, boolean,
     * java.util.List, java.util.List, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public Tenant createTenant(final String tenantName, final boolean createDefaults, final List<String> roles,
                               final List<String> domains) throws TenantException {
        return createTenant(tenantName, createDefaults, roles, domains, true);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#updateTenant(java.lang.String, java.lang.String,
     * java.util.List, java.util.List)
     */
    @Override
    public Tenant updateTenant(final String id, final String tenantName, final List<String> roles,
                               final List<String> domains, final boolean emailNewProfile) throws TenantException {

        Tenant tenant;
        try {
            tenant = tenantRepository.findTenantById(new ObjectId(id));
        } catch (TenantException e) {
            log.error("Unable to find tenant with id " + id, e);
            throw new TenantException("Unable to find Tenant due a error ", e);
        }

        if (tenant != null) {
            tenant.setEmailNewProfile(emailNewProfile);

            if (id != null && !id.trim().isEmpty()) {
                tenant.setId(new ObjectId(id));
            }

            if (tenantName != null && !tenantName.trim().isEmpty()) {
                tenant.setTenantName(tenantName);
            }

            if (roles != null && !roles.isEmpty()) {
                tenant.setRoles(roles);
            } else {
                tenant.setRoles(Arrays.asList(ProfileConstants.DEFAULT_TENANT_ROLES));
            }

            if (domains != null && !domains.isEmpty()) {
                tenant.setDomains(domains);
            } else {
                tenant.setDomains(Arrays.asList(ProfileConstants.DEFAULT_TENANT_DOMAINS));
            }

            try {
                tenantRepository.save(tenant);
            } catch (MongoDataException e) {
                log.error("Unable to save tenant with id" + id + " due a error ", e);
                throw new TenantException("Unable to save tenant", e);
            }
        }

        return tenant;
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#updateTenant(java.lang.String, java.lang.String,
     * java.util.List, java.util.List)
     */
    @Override
    public Tenant updateTenant(final String id, final String tenantName, final List<String> roles,
                               final List<String> domains) throws TenantException {
        return updateTenant(id, tenantName, roles, domains, true);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#deleteTenant(java.lang.String)
     */
    @Override
    public void deleteTenant(final String tenantName) throws TenantException {
        try {
            Tenant t = tenantRepository.getTenantByName(tenantName);
            profileService.deleteProfiles(tenantName);
            if (t != null) {
                tenantRepository.deleteTenant(t.getId());
            }
        } catch (MongoDataException ex) {
            log.error("Unable to delete tenant " + tenantName, ex);
            throw new TenantException("Unable to delete tenant", ex);
        }

    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getTenantById(java.lang.String)
     */
    @Override
    public Tenant getTenantById(final String tenantId) throws TenantException {
        return tenantRepository.findTenantById(new ObjectId(tenantId));
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getTenantByName(java.lang.String)
     */
    @Override
    public Tenant getTenantByName(final String tenantName) throws TenantException {
        return tenantRepository.getTenantByName(tenantName);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getTenantByTicket(java.lang.String)
     */
    @Override
    public Tenant getTenantByTicket(final String ticket) throws TenantException, TicketException {
        Profile profile = this.profileService.getProfileByTicket(ticket);
        return getTenantByName(profile.getTenantName());

    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#exists(java.lang.String)
     */
    @Override
    public boolean exists(final String tenantName) throws TenantException {
        return getTenantByName(tenantName) != null;
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getTenantRange(java.lang.String, java.lang.String,
     * int, int)
     */
    @Override
    public List<Tenant> getTenantRange(final String sortBy, String sortOrder, int start, int end) {
        return tenantRepository.getTenantRange(sortBy, sortOrder, start, end);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getTenantsCount()
     */
    @Override
    public long getTenantsCount() throws MongoDataException {
        return tenantRepository.count();
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getAllTenants()
     */
    @Override
    public Iterable<Tenant> getAllTenants() throws TenantException {
        try {
            return tenantRepository.findAll();
        } catch (MongoDataException ex) {
            log.debug("Unable to find all tenants ", ex);
            throw new TenantException("Unable to find all teants", ex);
        }
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getTenantsByRoleName(java.lang.String)
     */
    @Override
    public Iterable<Tenant> getTenantsByRoleName(final String roleName) throws TenantException {
        return tenantRepository.getTenants(new String[] {roleName});
    }
}
