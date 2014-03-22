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
package org.craftercms.profile.v2.services.impl.aspects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.AttributeActions;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.v2.exceptions.I10nProfileException;
import org.craftercms.profile.v2.exceptions.NoSuchTenantException;
import org.craftercms.profile.v2.repositories.TenantRepository;
import org.craftercms.profile.v2.services.impl.TenantServiceImpl;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;
import java.util.Set;

/**
 * Aspect that protects profile attributes on read/write/delete.
 *
 * @author avasquez
 */
@Aspect
public class AttributeSecuringAspect {

    private static final I10nLogger logger = new I10nLogger(AttributeSecuringAspect.class,
            "crafter.profile.messages.logging");

    private static final String LOG_KEY_EVALUATING_ATTRIB_ACTION =      "profile.attribute.evaluatingAttributeAction";
    private static final String LOG_KEY_REMOVING_UNREADABLE_ATTRIB =    "profile.attribute.removingUnreadableAttribute";
    private static final String LOG_KEY_NO_ATTRIB_DEF_FOUND =           "profile.attribute.noAttributeDefFound";

    protected PermissionEvaluator permissionEvaluator;
    protected TenantRepository tenantRepository;

    @Required
    public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    @Required
    public void setTenantRepository(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @AfterReturning(value = "execution(* org.craftercms.profile.api.services.ProfileService.getAttributes()) && " +
                            "args(tenantName, profileId, attributeNames)", returning = "attributes",
                    argNames = "tenantName, profileId, attributeNames, attributes")
    public void filterNonReadableAttributes(String tenantName, String profileId, String[] attributeNames,
                                            Map<String, Object> attributes) throws I10nProfileException {
        Tenant tenant = getTenant(tenantName);
        Set<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();

        for (AttributeDefinition attributeDefinition : attributeDefinitions) {
            filterAttributeIfReadNotAllowed(tenant, attributeDefinition, attributes);
        }
    }

    @Before(value = "execution(* org.craftercms.profile.api.services.ProfileService.updateAttributes()) && " +
                    "args(tenantName, profileId, attributes)", argNames = "tenantName, profileId, attributes")
    public void removeNonWritableAttributes(String tenantName, String profileId, Map<String, Object> attributes)
            throws I10nProfileException {
        Tenant tenant = getTenant(tenantName);
        Set<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();

        for (String attributeName : attributes.keySet()) {
            rejectAttributeIfActionNotAllowed(tenant, attributeName, AttributeActions.WRITE, attributeDefinitions);
        }
    }

    @Before(value = "execution(* org.craftercms.profile.api.services.ProfileService.deleteAttributes()) && " +
            "args(tenantName, profileId, attributeNames)", argNames = "tenantName, profileId, attributeNames")
    public void removeNonDeletableAttributes(String tenantName, String profileId, String[] attributeNames)
            throws I10nProfileException {
        Tenant tenant = getTenant(tenantName);
        Set<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();

        for (String attributeName : attributeNames) {
            rejectAttributeIfActionNotAllowed(tenant, attributeName, AttributeActions.DELETE, attributeDefinitions);
        }
    }

    protected void filterAttributeIfReadNotAllowed(Tenant tenant, AttributeDefinition attributeDefinition,
                                                   Map<String, Object> attributes) throws PermissionException {
        String attributeName = attributeDefinition.getName();

        logger.debug(LOG_KEY_EVALUATING_ATTRIB_ACTION, AttributeActions.READ, attributeName, tenant.getName());

        if (!permissionEvaluator.isAllowed(attributeDefinition, AttributeActions.READ)) {
            logger.debug(LOG_KEY_REMOVING_UNREADABLE_ATTRIB, attributeName);

            attributes.remove(attributeName);
        }
    }

    protected void rejectAttributeIfActionNotAllowed(Tenant tenant, String attributeName, String action,
                                                     Set<AttributeDefinition> attributeDefinitions)
            throws PermissionException {
        AttributeDefinition definition = getAttributeDefinitionByName(attributeDefinitions, attributeName);
        if (definition != null) {
            logger.debug(LOG_KEY_EVALUATING_ATTRIB_ACTION, action, attributeName, tenant.getName());

            if (!permissionEvaluator.isAllowed(definition, action)) {
                throw new ActionDeniedException(action, attributeName);
            }
        } else {
            logger.debug(LOG_KEY_NO_ATTRIB_DEF_FOUND, attributeName, tenant.getName());
        }
    }

    protected AttributeDefinition getAttributeDefinitionByName(final Set<AttributeDefinition> attributeDefinitions,
                                                               final String name) {
        return CollectionUtils.find(attributeDefinitions, new Predicate<AttributeDefinition>() {

            @Override
            public boolean evaluate(AttributeDefinition definition) {
                return definition.getName().equals(name);
            }

        });
    }

    protected Tenant getTenant(String name) throws I10nProfileException {
        try {
            Tenant tenant = tenantRepository.findByName(name);
            if (tenant != null) {
                return tenant;
            } else {
                throw new NoSuchTenantException(name);
            }
        } catch (MongoDataException e) {
            throw new I10nProfileException(TenantServiceImpl.ERROR_KEY_GET_TENANT_ERROR, name);
        }
    }

}
