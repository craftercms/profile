/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
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
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.commons.entitlements.model.EntitlementType;
import org.craftercms.commons.entitlements.validator.EntitlementValidator;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.logging.Logged;
import org.craftercms.commons.mongo.DuplicateKeyException;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.mongo.UpdateHelper;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.TenantAction;
import org.craftercms.profile.api.exceptions.I10nProfileException;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.exceptions.NoSuchTenantException;
import org.craftercms.profile.exceptions.TenantExistsException;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.repositories.TenantRepository;
import org.craftercms.profile.utils.db.TenantUpdater;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link org.craftercms.profile.api.services.TenantService}.
 *
 * @author avasquez
 */
@Logged
public class TenantServiceImpl implements TenantService {

    private static final I10nLogger logger = new I10nLogger(TenantServiceImpl.class,
                                                            "crafter.profile.messages.logging");

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

    public static final String ERROR_KEY_ENTITLEMENT_ERROR = "profile.license.entitlementError";

    protected PermissionEvaluator<AccessToken, String> tenantPermissionEvaluator;
    protected PermissionEvaluator<AccessToken, AttributeDefinition> attributePermissionEvaluator;
    protected TenantRepository tenantRepository;
    protected ProfileRepository profileRepository;
    protected ProfileService profileService;

    protected EntitlementValidator entitlementValidator;

    @Required
    public void setTenantPermissionEvaluator(PermissionEvaluator<AccessToken, String> tenantPermissionEvaluator) {
        this.tenantPermissionEvaluator = tenantPermissionEvaluator;
    }

    @Required
    public void setAttributePermissionEvaluator(
        PermissionEvaluator<AccessToken, AttributeDefinition> attributePermissionEvaluator) {
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

    @Required
    public void setEntitlementValidator(final EntitlementValidator entitlementValidator) {
        this.entitlementValidator = entitlementValidator;
    }

    @Override
    public Tenant createTenant(Tenant tenant) throws ProfileException {
        checkIfTenantActionIsAllowed(null, TenantAction.CREATE_TENANT);

        try {
            entitlementValidator.validateEntitlement(EntitlementType.SITE, 1);
        } catch (Exception e) {
            throw new I10nProfileException(ERROR_KEY_ENTITLEMENT_ERROR, e);
        }

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

        Tenant updatedTenant = updateTenant(tenant.getName(), new UpdateCallback() {

            @Override
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                Tenant originalTenant = tenantUpdater.getTenant();
                Collection<String> originalRoles = originalTenant.getAvailableRoles();
                Collection<String> newRoles = tenant.getAvailableRoles();
                Collection<AttributeDefinition> originalDefinitions = originalTenant.getAttributeDefinitions();
                Collection<AttributeDefinition> newDefinitions = tenant.getAttributeDefinitions();
                Collection<String> removedRoles = CollectionUtils.subtract(originalRoles, newRoles);
                Collection<AttributeDefinition> removedDefinitions = CollectionUtils.subtract(originalDefinitions,
                                                                                              newDefinitions);

                for (String removedRole : removedRoles) {
                    removeRoleFromProfiles(tenantName, removedRole);
                }
                for (AttributeDefinition removedDefinition : removedDefinitions) {
                    removeAttributeFromProfiles(tenantName, removedDefinition.getName());
                }

                tenantUpdater.setVerifyNewProfiles(tenant.isVerifyNewProfiles());
                tenantUpdater.setSsoEnabled(tenant.isSsoEnabled());
                tenantUpdater.setCleanseAttributes(tenant.isCleanseAttributes());
                tenantUpdater.setAvailableRoles(tenant.getAvailableRoles());
                tenantUpdater.setAttributeDefinitions(tenant.getAttributeDefinitions());
            }

        });

        for (AttributeDefinition definition : updatedTenant.getAttributeDefinitions()) {
            addDefaultValue(tenantName, definition.getName(), definition.getDefaultValue());
        }

        return updatedTenant;
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
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                tenantUpdater.setVerifyNewProfiles(verify);
            }

        });

        logger.debug(LOG_KEY_VERIFY_NEW_PROFILES_FLAG_SET, tenantName, verify);

        return tenant;
    }

    @Override
    public Tenant addRoles(String tenantName, final Collection<String> roles) throws ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                tenantUpdater.addAvailableRoles(roles);
            }

        });

        logger.debug(LOG_KEY_ROLES_ADDED, roles, tenantName);

        return tenant;
    }

    @Override
    public Tenant removeRoles(final String tenantName, final Collection<String> roles) throws ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                for (String role : roles) {
                    removeRoleFromProfiles(tenantName, role);
                }

                tenantUpdater.removeAvailableRoles(roles);
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
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                tenantUpdater.addAttributeDefinitions(attributeDefinitions);
            }

        });

        for (AttributeDefinition definition : tenant.getAttributeDefinitions()) {
            addDefaultValue(tenantName, definition.getName(), definition.getDefaultValue());
        }

        logger.debug(LOG_KEY_ATTRIBUTE_DEFINITIONS_ADDED, attributeDefinitions, tenantName);

        return tenant;
    }

    @Override
    public Tenant updateAttributeDefinitions(final String tenantName,
                                             final Collection<AttributeDefinition> attributeDefinitions) throws
        ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                tenantUpdater.updateAttributeDefinitions(attributeDefinitions);
            }

        });

        logger.debug(LOG_KEY_ATTRIBUTE_DEFINITIONS_UPDATED, attributeDefinitions, tenantName);

        return tenant;
    }

    @Override
    public Tenant removeAttributeDefinitions(final String tenantName,
                                             final Collection<String> attributeNames) throws ProfileException {
        for (String attributeName : attributeNames) {
            removeAttributeFromProfiles(tenantName, attributeName);
        }

        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                tenantUpdater.removeAttributeDefinitions(attributeNames);
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

            UpdateHelper updateHelper = new UpdateHelper();
            TenantUpdater tenantUpdater = new TenantUpdater(tenant, updateHelper, tenantRepository);

            callback.doWithTenant(tenantUpdater);

            try {
                tenantUpdater.update();
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_UPDATE_TENANT_ERROR, e, tenant.getName());
            }
        } else {
            throw new NoSuchTenantException(tenantName);
        }

        return tenant;
    }

    protected interface UpdateCallback {

        void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException;

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
