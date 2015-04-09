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
package org.craftercms.profile.services.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.logging.Logged;
import org.craftercms.commons.mongo.DuplicateKeyException;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.TenantAction;
import org.craftercms.profile.api.exceptions.I10nProfileException;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.exceptions.AttributeAlreadyDefinedException;
import org.craftercms.profile.exceptions.AttributeNotDefinedException;
import org.craftercms.profile.exceptions.NoSuchTenantException;
import org.craftercms.profile.exceptions.TenantExistsException;
import org.craftercms.profile.permissions.Application;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link org.craftercms.profile.api.services.TenantService}.
 *
 * @author avasquez
 */
@Logged
public class TenantServiceImpl implements TenantService {

    private static final I10nLogger logger = new I10nLogger(TenantServiceImpl.class, "crafter.profile.messages" +
                                                                                     ".logging");

    public static final String LOG_KEY_TENANT_CREATED = "profile.tenant.tenantCreated";
    public static final String LOG_KEY_TENANT_DELETED = "profile.tenant.tenantDeleted";
    public static final String LOG_KEY_VERIFY_NEW_PROFILES_FLAG_SET = "profile.tenant.verifyNewProfilesFlagSet";
    public static final String LOG_KEY_ROLES_ADDED = "profile.tenant.rolesAdded";
    public static final String LOG_KEY_ROLES_REMOVED = "profile.tenant.rolesRemoved";
    public static final String LOG_KEY_ATTRIBUTE_DEFINITIONS_ADDED = "profile.tenant.attributeDefinitionsAdded";
    public static final String LOG_KEY_ATTRIBUTE_DEFINITIONS_UPDATED = "profile.tenant.attributeDefinitionsUpdated";
    public static final String LOG_KEY_ATTRIBUTE_DEFINITIONS_REMOVED = "profile.tenant.attributeDefinitionsRemoved";

    public static final String ERROR_KEY_CREATE_TENANT_ERROR = "profile.tenant.createTenantError";
    public static final String ERROR_KEY_GET_TENANT_ERROR = "profile.tenant.getTenantError";
    public static final String ERROR_KEY_UPDATE_TENANT_ERROR = "profile.tenant.updateTenantError";
    public static final String ERROR_KEY_DELETE_TENANT_ERROR = "profile.tenant.deleteTenantError";
    public static final String ERROR_KEY_GET_TENANT_COUNT_ERROR = "profile.tenant.getTenantCountError";
    public static final String ERROR_KEY_GET_ALL_TENANTS_ERROR = "profile.tenant.getAllTenantsError";

    public static final String ERROR_KEY_DELETE_ALL_PROFILES_ERROR = "profile.profile.deleteAll";
    public static final String ERROR_KEY_REMOVE_ROLE_FROM_ALL_PROFILES_ERROR = "profile.role.removeRoleFromAll";
    public static final String ERROR_KEY_REMOVE_ATTRIBUTE_FROM_ALL_PROFILES_ERROR = "profile.attribute" +
                                                                                    ".removeAttributeFromAllError";
    public static final String ERROR_KEY_ADD_DEFAULT_VALUE_ERROR = "profile.attribute.addDefaultValueError";

    protected PermissionEvaluator<Application, String> tenantPermissionEvaluator;
    protected PermissionEvaluator<Application, AttributeDefinition> attributePermissionEvaluator;
    protected TenantRepository tenantRepository;
    protected ProfileRepository profileRepository;
    protected ProfileService profileService;

    @Required
    public void setTenantPermissionEvaluator(PermissionEvaluator<Application, String> tenantPermissionEvaluator) {
        this.tenantPermissionEvaluator = tenantPermissionEvaluator;
    }

    @Required
    public void setAttributePermissionEvaluator(
        PermissionEvaluator<Application, AttributeDefinition> attributePermissionEvaluator) {
        this.attributePermissionEvaluator = attributePermissionEvaluator;
    }

    @Required
    public void setTenantRepository(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Required
    public void setProfileRepository(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public Tenant createTenant(Tenant tenant) throws ProfileException {
        checkIfTenantActionIsAllowed(null, TenantAction.CREATE_TENANT);

        // Make sure ID is null, we want it auto-generated
        tenant.setId(null);

        try {
            tenantRepository.insert(tenant);
        } catch (DuplicateKeyException e) {
            throw new TenantExistsException(tenant.getName());
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_TENANT_ERROR, e, tenant.getName());
        }

        logger.debug(LOG_KEY_TENANT_CREATED, tenant);

        return tenant;
    }

    @Override
    public Tenant getTenant(String name) throws ProfileException {
        checkIfTenantActionIsAllowed(name, TenantAction.READ_TENANT);

        try {
            return tenantRepository.findByName(name);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TENANT_ERROR, e, name);
        }
    }

    @Override
    public Tenant updateTenant(final Tenant tenant) throws ProfileException {
        final String tenantName = tenant.getName();

        return updateTenant(tenant.getName(), new UpdateCallback() {

            @Override
            public void doWithTenant(Tenant originalTenant) throws ProfileException {
                Collection<String> removedRoles = CollectionUtils.subtract(originalTenant.getAvailableRoles(),
                                                                           tenant.getAvailableRoles());
                Collection<AttributeDefinition> removedDefinitions = CollectionUtils.subtract(
                    originalTenant.getAttributeDefinitions(), tenant.getAttributeDefinitions());

                for (String removedRole : removedRoles) {
                    removeRoleFromProfiles(tenantName, removedRole);
                }
                for (AttributeDefinition removedDefinition : removedDefinitions) {
                    removeAttributeFromProfiles(tenantName, removedDefinition.getName());
                }

                for (AttributeDefinition updatedDefinition : tenant.getAttributeDefinitions()) {
                    addDefaultValue(tenantName, updatedDefinition.getName(), updatedDefinition.getDefaultValue());
                }

                originalTenant.setVerifyNewProfiles(tenant.isVerifyNewProfiles());
                originalTenant.setAvailableRoles(tenant.getAvailableRoles());
                originalTenant.setAttributeDefinitions(tenant.getAttributeDefinitions());
            }

        });
    }

    @Override
    public void deleteTenant(String name) throws ProfileException {
        checkIfTenantActionIsAllowed(name, TenantAction.DELETE_TENANT);

        try {
            profileRepository.removeAll(name);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_DELETE_ALL_PROFILES_ERROR, e, name);
        }

        try {
            tenantRepository.removeByName(name);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_DELETE_TENANT_ERROR, e, name);
        }

        logger.debug(LOG_KEY_TENANT_DELETED, name);
    }

    @Override
    public long getTenantCount() throws ProfileException {
        checkIfTenantActionIsAllowed(null, TenantAction.READ_TENANT);

        try {
            return tenantRepository.count();
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TENANT_COUNT_ERROR, e);
        }
    }

    @Override
    public List<Tenant> getAllTenants() throws ProfileException {
        checkIfTenantActionIsAllowed(null, TenantAction.READ_TENANT);

        try {
            return IterableUtils.toList(tenantRepository.findAll());
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_ALL_TENANTS_ERROR, e);
        }
    }

    @Override
    public Tenant verifyNewProfiles(String tenantName, final boolean verify) throws ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(Tenant tenant) throws ProfileException {
                tenant.setVerifyNewProfiles(verify);
            }

        });

        logger.debug(LOG_KEY_VERIFY_NEW_PROFILES_FLAG_SET, tenantName, verify);

        return tenant;
    }

    @Override
    public Tenant addRoles(String tenantName, final Collection<String> roles) throws ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(Tenant tenant) throws ProfileException {
                tenant.getAvailableRoles().addAll(roles);
            }

        });

        logger.debug(LOG_KEY_ROLES_ADDED, roles, tenantName);

        return tenant;
    }

    @Override
    public Tenant removeRoles(final String tenantName, final Collection<String> roles) throws ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(Tenant tenant) throws ProfileException {
                for (String role : roles) {
                    removeRoleFromProfiles(tenantName, role);
                }

                tenant.getAvailableRoles().removeAll(roles);
            }

        });

        logger.debug(LOG_KEY_ROLES_REMOVED, roles, tenantName);

        return tenant;
    }

    @Override
    public Tenant addAttributeDefinitions(final String tenantName,
                                          final Collection<AttributeDefinition> attributeDefinitions) throws
        ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

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

        logger.debug(LOG_KEY_ATTRIBUTE_DEFINITIONS_ADDED, attributeDefinitions, tenantName);

        return tenant;
    }

    @Override
    public Tenant updateAttributeDefinitions(final String tenantName,
                                             final Collection<AttributeDefinition> attributeDefinitions) throws
        ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(Tenant tenant) throws ProfileException {
                Set<AttributeDefinition> allDefinitions = tenant.getAttributeDefinitions();

                for (AttributeDefinition updatedDefinition : attributeDefinitions) {
                    String attributeName = updatedDefinition.getName();
                    AttributeDefinition originalDefinition = findAttributeDefinition(allDefinitions, attributeName);
                    if (originalDefinition != null) {
                        originalDefinition.setPermissions(updatedDefinition.getPermissions());
                        originalDefinition.setMetadata(updatedDefinition.getMetadata());
                    } else {
                        throw new AttributeNotDefinedException(attributeName, tenantName);
                    }
                }
            }

        });

        logger.debug(LOG_KEY_ATTRIBUTE_DEFINITIONS_UPDATED, attributeDefinitions, tenantName);

        return tenant;
    }

    @Override
    public Tenant removeAttributeDefinitions(final String tenantName,
                                             final Collection<String> attributeNames) throws ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(Tenant tenant) throws ProfileException {
                Set<AttributeDefinition> allDefinitions = tenant.getAttributeDefinitions();

                for (String attributeName : attributeNames) {
                    for (Iterator<AttributeDefinition> iter = allDefinitions.iterator(); iter.hasNext(); ) {
                        AttributeDefinition definition = iter.next();
                        if (definition.getName().equals(attributeName)) {
                            removeAttributeFromProfiles(tenantName, definition.getName());

                            iter.remove();
                            break;
                        }
                    }
                }
            }

        });

        logger.debug(LOG_KEY_ATTRIBUTE_DEFINITIONS_REMOVED, attributeNames, tenantName);

        return tenant;
    }

    protected void checkIfTenantActionIsAllowed(String tenantName, TenantAction action) {
        if (!tenantPermissionEvaluator.isAllowed(tenantName, action.toString())) {
            if (tenantName != null) {
                throw new ActionDeniedException(action.toString(), "tenant \"" + tenantName + "\"");
            } else {
                throw new ActionDeniedException(action.toString());
            }
        }
    }

    protected Tenant updateTenant(String tenantName, UpdateCallback callback) throws ProfileException {
        Tenant tenant = getTenant(tenantName);
        if (tenant != null) {
            checkIfTenantActionIsAllowed(tenantName, TenantAction.UPDATE_TENANT);

            callback.doWithTenant(tenant);

            try {
                tenantRepository.save(tenant);
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_UPDATE_TENANT_ERROR, e, tenant.getName());
            }
        } else {
            throw new NoSuchTenantException(tenantName);
        }

        return tenant;
    }

    protected interface UpdateCallback {

        void doWithTenant(Tenant tenant) throws ProfileException;

    }

    protected AttributeDefinition findAttributeDefinition(Iterable<AttributeDefinition> definitions,
                                                          final String attributeName) {
        return CollectionUtils.find(definitions, new Predicate<AttributeDefinition>() {

            @Override
            public boolean evaluate(AttributeDefinition definition) {
                return definition.getName().equals(attributeName);
            }

        });
    }

    protected void removeRoleFromProfiles(String tenantName, String role) throws ProfileException {
        try {
            profileRepository.removeRoleFromAll(tenantName, role);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_REMOVE_ROLE_FROM_ALL_PROFILES_ERROR, e, role, tenantName);
        }
    }

    protected void removeAttributeFromProfiles(String tenantName, String attributeName) throws ProfileException {
        try {
            profileRepository.removeAttributeFromAll(tenantName, attributeName);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_REMOVE_ATTRIBUTE_FROM_ALL_PROFILES_ERROR, e, attributeName,
                                           tenantName);
        }
    }

    protected void addDefaultValue(String tenantName, String attributeName,
                                   Object defaultValue) throws ProfileException {
        if (defaultValue != null) {
            try {
                profileRepository.updateAllWithDefaultValue(tenantName, attributeName, defaultValue);
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_ADD_DEFAULT_VALUE_ERROR, e, attributeName, tenantName);
            }
        }
    }

}
