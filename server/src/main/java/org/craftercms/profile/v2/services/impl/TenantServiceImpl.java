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
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.TenantActions;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.v2.exceptions.AttributeAlreadyDefinedException;
import org.craftercms.profile.v2.exceptions.TenantExistsException;
import org.craftercms.profile.v2.exceptions.I10nProfileException;
import org.craftercms.profile.v2.exceptions.NoSuchTenantException;
import org.craftercms.profile.v2.permissions.Application;
import org.craftercms.profile.v2.repositories.ProfileRepository;
import org.craftercms.profile.v2.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Default implementation of {@link org.craftercms.profile.api.services.TenantService}.
 *
 * @author avasquez
 */
public class TenantServiceImpl implements TenantService {

    public static final String ERROR_KEY_CREATE_TENANT_ERROR =      "profile.tenant.createTenantError";
    public static final String ERROR_KEY_GET_TENANT_ERROR =         "profile.tenant.getTenantError";
    public static final String ERROR_KEY_UPDATE_TENANT_ERROR =      "profile.tenant.updateTenantError";
    public static final String ERROR_KEY_DELETE_TENANT_ERROR =      "profile.tenant.deleteTenantError";
    public static final String ERROR_KEY_GET_TENANT_COUNT_ERROR =   "profile.tenant.getTenantCountError";
    public static final String ERROR_KEY_GET_ALL_TENANTS_ERROR =    "profile.tenant.getAllTenantsError";

    protected PermissionEvaluator<Application, String> permissionEvaluator;
    protected TenantRepository tenantRepository;
    protected ProfileRepository profileRepository;

    @Required
    public void setPermissionEvaluator(PermissionEvaluator<Application, String> permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    @Required
    public void setTenantRepository(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Required
    public void setProfileRepository(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Tenant createTenant(String name, boolean verifyNewProfiles, Set<String> roles) throws ProfileException {
        checkTenantPermission(null, TenantActions.CREATE);

        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setVerifyNewProfiles(verifyNewProfiles);
        tenant.setRoles(roles);

        try {
            tenantRepository.save(tenant);
        } catch (DuplicateKeyException e) {
            throw new TenantExistsException(name);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_TENANT_ERROR, e, name);
        }

        return tenant;
    }

    @Override
    public Tenant getTenant(String name) throws ProfileException {
        checkTenantPermission(name, TenantActions.READ);

        try {
            return tenantRepository.findByName(name);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TENANT_ERROR, e, name);
        }
    }

    @Override
    public void deleteTenant(String name) throws ProfileException {
        checkTenantPermission(name, TenantActions.DELETE);

        try {
            profileRepository.removeAllForTenant(name);
            tenantRepository.removeByName(name);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_DELETE_TENANT_ERROR, e, name);
        }
    }

    @Override
    public long getTenantCount() throws ProfileException  {
        checkTenantPermission(null, TenantActions.COUNT);

        try {
            return tenantRepository.count();
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TENANT_COUNT_ERROR, e);
        }
    }

    @Override
    public Iterable<Tenant> getAllTenants() throws ProfileException {
        checkTenantPermission(null, TenantActions.READ_ALL);

        try {
            return tenantRepository.findAll();
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_ALL_TENANTS_ERROR, e);
        }
    }

    @Override
    public Tenant verifyNewProfiles(String tenantName, final boolean verify) throws ProfileException {
        return updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(Tenant tenant) throws ProfileException {
                tenant.setVerifyNewProfiles(verify);
            }

        });
    }

    @Override
    public Tenant addRoles(String tenantName, final Collection<String> roles) throws ProfileException {
        return updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(Tenant tenant) throws ProfileException {
                tenant.getRoles().addAll(roles);
            }

        });
    }

    @Override
    public Tenant removeRoles(String tenantName, final Collection<String> roles) throws ProfileException {
        return updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(Tenant tenant) throws ProfileException {
                tenant.getRoles().removeAll(roles);
            }

        });
    }

    @Override
    public Tenant addAttributeDefinitions(final String tenantName,
                                          final Collection<AttributeDefinition> attributeDefinitions)
            throws ProfileException {
        return updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(Tenant tenant) throws ProfileException {
                Set<AttributeDefinition> allDefinitions = tenant.getAttributeDefinitions();

                for (AttributeDefinition definition : attributeDefinitions) {
                    if (!allDefinitions.add(definition)) {
                        throw new AttributeAlreadyDefinedException(definition.getName(), tenantName);
                    }
                }
            }

        });
    }

    @Override
    public Tenant removeAttributeDefinitions(String tenantName,
                                             final Collection<String> attributeNames) throws ProfileException {
        return updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(Tenant tenant) throws ProfileException {
                Set<AttributeDefinition> allDefinitions = tenant.getAttributeDefinitions();
                String currentApp = Application.getCurrent().getName();

                for (String attributeName : attributeNames) {
                    for (Iterator<AttributeDefinition> iter = allDefinitions.iterator(); iter.hasNext();) {
                        AttributeDefinition attributeDefinition = iter.next();
                        if (attributeDefinition.getName().equals(attributeName)) {
                            if (!attributeDefinition.getOwner().equals(currentApp)) {
                                throw new ActionDeniedException("remove", attributeDefinition);
                            }

                            iter.remove();
                            break;
                        }
                    }
                }
            }

        });
    }

    protected void checkTenantPermission(String tenantName, String action) {
        if (!permissionEvaluator.isAllowed(tenantName, action)) {
            if (tenantName != null) {
                throw new ActionDeniedException(action, tenantName);
            } else {
                throw new ActionDeniedException(action);
            }
        }
    }

    protected Tenant updateTenant(String tenantName, UpdateCallback callback) throws ProfileException {
        Tenant tenant = getTenant(tenantName);
        if (tenant != null) {
            checkTenantPermission(tenantName, TenantActions.UPDATE);

            callback.doWithTenant(tenant);

            updateTenant(tenant);
        } else {
            throw new NoSuchTenantException(tenantName);
        }

        return tenant;
    }

    protected interface UpdateCallback {

        void doWithTenant(Tenant tenant) throws ProfileException;

    }

    protected Tenant updateTenant(Tenant tenant) throws ProfileException {
        checkTenantPermission(tenant.getName(), TenantActions.UPDATE);

        try {
            tenantRepository.save(tenant);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_UPDATE_TENANT_ERROR, e, tenant.getName());
        }

        return tenant;
    }

}
