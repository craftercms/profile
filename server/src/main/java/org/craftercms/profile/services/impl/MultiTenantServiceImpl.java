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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.domain.Schema;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.repositories.TenantRepository;
import org.craftercms.profile.services.MultiTenantService;
import org.craftercms.profile.services.ProfileService;
import org.craftercms.profile.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
public class MultiTenantServiceImpl implements MultiTenantService {

    private final transient Logger log = LoggerFactory.getLogger(MultiTenantServiceImpl.class);

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private RoleService roleService;

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#createTenant(java.lang.String, boolean, java.util.List, java.util.List, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public Tenant createTenant(String tenantName, boolean createDefaults, List<String> roles, List<String> domains, boolean emailNewProfile,
                               HttpServletResponse response) {

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
            current = tenantRepository.save(tenant);
        } catch (DuplicateKeyException e) {
            try {
                if (response != null) {
                    response.sendError(HttpServletResponse.SC_CONFLICT);
                }
            } catch (IOException e1) {
                log.error("Can't set error status after a DuplicateKey exception was received.");
            }
        }
        return current;
    }
    
    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#createTenant(java.lang.String, boolean, java.util.List, java.util.List, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public Tenant createTenant(String tenantName, boolean createDefaults, List<String> roles, List<String> domains, 
                               HttpServletResponse response) {
    	return createTenant(tenantName, createDefaults, roles, domains, true, response);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#updateTenant(java.lang.String, java.lang.String, java.util.List, java.util.List)
     */
    @Override
    public Tenant updateTenant(String id, String tenantName, List<String> roles, List<String> domains, boolean emailNewProfile) {
        Tenant tenant = tenantRepository.findTenantById(new ObjectId(id));

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

            tenant = tenantRepository.save(tenant);
        }

        return tenant;
    }
    
    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#updateTenant(java.lang.String, java.lang.String, java.util.List, java.util.List)
     */
    @Override
    public Tenant updateTenant(String id, String tenantName, List<String> roles, List<String> domains) {
    	return updateTenant(id, tenantName, roles, domains, true);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#deleteTenant(java.lang.String)
     */
    @Override
    public void deleteTenant(String tenantName) {

        profileService.deleteProfiles(tenantName);
        Tenant t = tenantRepository.getTenantByName(tenantName);
        if (t != null) {
            tenantRepository.delete(t.getId());
        }

    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getTenantById(java.lang.String)
     */
    @Override
    public Tenant getTenantById(String tenantId) {
        return tenantRepository.findTenantById(new ObjectId(tenantId));
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getTenantByName(java.lang.String)
     */
    @Override
    public Tenant getTenantByName(String tenantName) {
        return tenantRepository.getTenantByName(tenantName);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getTenantByTicket(java.lang.String)
     */
    @Override
    public Tenant getTenantByTicket(String ticket) {
        Profile profile = this.profileService.getProfileByTicket(ticket);
        return getTenantByName(profile.getTenantName());

    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#exists(java.lang.String)
     */
    @Override
    public boolean exists(String tenantName) {
        Tenant t = getTenantByName(tenantName);
        if (t == null) {
            return false;
        }
        return true;

    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getTenantRange(java.lang.String, java.lang.String, int, int)
     */
    @Override
    public List<Tenant> getTenantRange(String sortBy, String sortOrder, int start, int end) {
        return tenantRepository.getTenantRange(sortBy, sortOrder, start, end);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getTenantsCount()
     */
    @Override
    public long getTenantsCount() {
        return tenantRepository.count();
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getAllTenants()
     */
    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.MultiTenantService#getTenantsByRoleName(java.lang.String)
     */
    @Override
    public List<Tenant> getTenantsByRoleName(String roleName) {
        return tenantRepository.getTenants(new String[] {roleName});
    }
}
