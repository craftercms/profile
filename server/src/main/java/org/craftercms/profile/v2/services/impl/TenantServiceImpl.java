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
package org.craftercms.profile.v2.services.impl;

import org.craftercms.commons.security.permissions.annotations.HasPermission;
import org.craftercms.commons.security.permissions.annotations.SecuredObject;
import org.craftercms.profile.api.*;
import org.craftercms.profile.api.services.TenantService;

import java.util.List;

/**
 * Default implementation of {@link org.craftercms.profile.api.services.TenantService}.
 *
 * @author avasquez
 */
public class TenantServiceImpl implements TenantService {

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.CREATE)
    public Tenant createTenant(Tenant tenant) {
        return null;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.READ)
    public Tenant getTenant(@SecuredObject String name) {
        return null;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.UPDATE)
    public Tenant updateTenant(@SecuredObject Tenant tenant) {
        return null;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.DELETE)
    public Tenant deleteTenant(@SecuredObject String name) {
        return null;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.COUNT)
    public int getTenantCount() {
        return 0;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.READ_ALL)
    public List<Tenant> getAllTenants() {
        return null;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.UPDATE)
    public Tenant verifyNewAccounts(@SecuredObject String tenantName, boolean verify) {
        return null;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.UPDATE)
    public Tenant addRoles(@SecuredObject String tenantName, String... roles) {
        return null;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.UPDATE)
    public Tenant removeRoles(@SecuredObject String tenantName, String... roles) {
        return null;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.UPDATE)
    public Tenant addAttributeDefinitions(@SecuredObject String tenantName,
                                          AttributeDefinition... attributeDefinitions) {
        return null;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.UPDATE)
    public Tenant removeAttributeDefinitions(@SecuredObject String tenantName,
                                             AttributeDefinition... attributeDefinitions) {
        return null;
    }

}
