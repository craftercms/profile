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

import org.craftercms.commons.mongo.DuplicateKeyException;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.permissions.annotations.HasPermission;
import org.craftercms.commons.security.permissions.annotations.SecuredObject;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.TenantActions;
import org.craftercms.profile.api.TenantPermission;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.v2.exceptions.AttributeAlreadyDefinedException;
import org.craftercms.profile.v2.exceptions.I10nProfileException;
import org.craftercms.profile.v2.exceptions.NoSuchTenantException;
import org.craftercms.profile.v2.exceptions.TenantAlreadyExistsException;
import org.craftercms.profile.v2.permissions.Application;
import org.craftercms.profile.v2.repositories.ProfileRepository;
import org.craftercms.profile.v2.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Required;

import java.util.Iterator;
import java.util.Set;

/**
 * Default implementation of {@link org.craftercms.profile.api.services.TenantService}.
 *
 * @author avasquez
 */
public class TenantServiceImpl implements TenantService {

    public static final String ERROR_KEY_CREATE_TENANT_ERROR =     "profile.tenant.createTenantError";
    public static final String ERROR_KEY_GET_TENANT_ERROR =        "profile.tenant.getTenantError";
    public static final String ERROR_KEY_UPDATE_TENANT_ERROR =     "profile.tenant.updateTenantError";
    public static final String ERROR_KEY_DELETE_TENANT_ERROR =     "profile.tenant.deleteTenantError";
    public static final String ERROR_KEY_GET_TENANT_COUNT_ERROR =  "profile.tenant.getTenantCountError";
    public static final String ERROR_KEY_GET_ALL_TENANTS_ERROR =   "profile.tenant.getAllTenantsError";

    protected TenantRepository tenantRepository;
    protected ProfileRepository profileRepository;

    @Required
    public void setTenantRepository(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.CREATE)
    public Tenant createTenant(String name, boolean verifyNewAccounts, Set<String> roles,
                               Set<AttributeDefinition> attributeDefinitions) throws ProfileException {
        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setVerifyNewProfiles(verifyNewAccounts);
        tenant.setRoles(roles);
        tenant.setAttributeDefinitions(attributeDefinitions);

        try {
            tenantRepository.save(tenant);
        } catch (DuplicateKeyException e) {
            throw new TenantAlreadyExistsException(name);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_TENANT_ERROR, e, name);
        }

        return tenant;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.READ)
    public Tenant getTenant(@SecuredObject String name) throws ProfileException {
        try {
            return tenantRepository.findByName(name);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TENANT_ERROR, e, name);
        }
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.UPDATE)
    public Tenant updateTenant(@SecuredObject Tenant tenant) throws ProfileException {
        try {
            tenantRepository.save(tenant);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_UPDATE_TENANT_ERROR, e, tenant.getName());
        }

        return tenant;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.DELETE)
    public Tenant deleteTenant(@SecuredObject String name) throws ProfileException {
        Tenant tenant = getTenant(name);

        try {
            profileRepository.removeAllForTenant(name);
            tenantRepository.removeByName(name);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_DELETE_TENANT_ERROR, e, name);
        }

        return tenant;
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.COUNT)
    public long getTenantCount() throws ProfileException  {
        try {
            return tenantRepository.count();
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TENANT_COUNT_ERROR, e);
        }
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.READ_ALL)
    public Iterable<Tenant> getAllTenants() throws ProfileException {
        try {
            return tenantRepository.findAll();
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_ALL_TENANTS_ERROR, e);
        }
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.UPDATE)
    public Tenant verifyNewAccounts(@SecuredObject String tenantName, boolean verify) throws ProfileException {
        Tenant tenant = getTenant(tenantName);
        if (tenant != null) {
            tenant.setVerifyNewProfiles(verify);
            updateTenant(tenant);

            return tenant;
        } else {
            throw new NoSuchTenantException(tenantName);
        }
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.UPDATE)
    public Tenant addRoles(@SecuredObject String tenantName, String... roles) throws ProfileException {
        Tenant tenant = getTenant(tenantName);
        if (tenant != null) {
            Set<String> allRoles = tenant.getRoles();

            for (String role : roles) {
                allRoles.add(role);
            }

            updateTenant(tenant);

            return tenant;
        } else {
            throw new NoSuchTenantException(tenantName);
        }
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.UPDATE)
    public Tenant removeRoles(@SecuredObject String tenantName, String... roles) throws ProfileException {
        Tenant tenant = getTenant(tenantName);
        if (tenant != null) {
            Set<String> allRoles = tenant.getRoles();

            for (String role : roles) {
                allRoles.remove(role);
            }

            updateTenant(tenant);

            return tenant;
        } else {
            throw new NoSuchTenantException(tenantName);
        }
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.UPDATE)
    public Tenant addAttributeDefinitions(@SecuredObject String tenantName,
                                          AttributeDefinition... attributeDefinitions) throws ProfileException {
        Tenant tenant = getTenant(tenantName);
        if (tenant != null) {
            Set<AttributeDefinition> allDefinitions = tenant.getAttributeDefinitions();

            for (AttributeDefinition definition : attributeDefinitions) {
                if (!allDefinitions.add(definition)) {
                    throw new AttributeAlreadyDefinedException(definition.getName(), tenantName);
                }
            }

            updateTenant(tenant);

            return tenant;
        } else {
            throw new NoSuchTenantException(tenantName);
        }
    }

    @Override
    @HasPermission(type = TenantPermission.class, action = TenantActions.UPDATE)
    public Tenant removeAttributeDefinitions(@SecuredObject String tenantName, String... attributeNames)
            throws ProfileException {
        Tenant tenant = getTenant(tenantName);
        if (tenant != null) {
            Set<AttributeDefinition> allDefinitions = tenant.getAttributeDefinitions();
            String currentApp = Application.getCurrent().getName();

            for (String attributeName : attributeNames) {
                for (Iterator<AttributeDefinition> iter = allDefinitions.iterator(); iter.hasNext();) {
                    AttributeDefinition definition = iter.next();
                    if (definition.getName().equals(attributeName)) {
                        if (!definition.getOwner().equals(currentApp)) {
                            throw new ActionDeniedException("remove", definition);
                        }

                        iter.remove();
                        break;
                    }
                }
            }

            updateTenant(tenant);

            return tenant;
        } else {
            throw new NoSuchTenantException(tenantName);
        }
    }

}
