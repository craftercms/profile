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

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.domain.Role;
import org.craftercms.profile.exceptions.RoleException;
import org.craftercms.profile.exceptions.TenantException;
import org.craftercms.profile.repositories.RoleRepository;
import org.craftercms.profile.repositories.TenantRepository;
import org.craftercms.profile.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleServiceImpl implements RoleService {

    private final transient Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private RoleRepository roleRepository;

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.RoleService#createRole(java.lang.String,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public Role createRole(String roleName) throws RoleException {
        Role role = new Role();
        role.setRoleName(roleName);
        try {
            roleRepository.save(role);
            return role;
        } catch (MongoDataException e) {
            log.error("Unable to Save new role " + roleName);
            throw new RoleException("Unable to save Role", e);
        }
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.RoleService#deleteRole(java.lang.String,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void deleteRole(final String roleName) throws TenantException, RoleException {
        long count;

        try {
            count = tenantRepository.countTenantsWithRoles(new String[] {roleName});
        } catch (TenantException e) {
            throw new TenantException("Unable to count tenant with roles " + roleName, e);
        }

        if (count > 0) {
            log.error("Role {} will not be deleted since they are {} tenants with that role", roleName, count);
            throw new RoleException("They are tenants associated with the role " + roleName);
        } else {
            Role role = getRole(roleName);
            if (role != null) {
                try {
                    roleRepository.removeById(role.getId().toString());
                } catch (MongoDataException e) {
                    log.error("Unable to delete Role with id" + role.getId().toString(), e);
                    throw new RoleException("Unable to delete Role", e);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.RoleService#deleteAllRoles()
     */
    @Override
    public void deleteAllRoles() throws RoleException {
        roleRepository.removeAll();
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.RoleService#getAllRoles()
     */
    @Override
    public Iterable<Role> getAllRoles() throws RoleException {
        try {
            return roleRepository.findAll();
        } catch (MongoDataException e) {
            log.error("Unable to search Roles", e);
            throw new RoleException("Unable to search for all roles", e);
        }
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.RoleService#getRole(java.lang.String)
     */
    @Override
    public Role getRole(String roleName) throws RoleException {
        return roleRepository.findByRoleName(roleName);
    }

}
