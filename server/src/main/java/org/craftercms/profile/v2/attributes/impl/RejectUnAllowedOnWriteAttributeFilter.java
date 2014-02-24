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
package org.craftercms.profile.v2.attributes.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.AttributeActions;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.v2.attributes.AttributeFilter;
import org.craftercms.profile.v2.exceptions.AttributeFilterException;
import org.craftercms.profile.v2.exceptions.AttributeWriteNotAllowedException;
import org.craftercms.profile.v2.exceptions.UndefinedAttributeException;
import org.craftercms.profile.v2.permissions.Application;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;

/**
 * {@link org.craftercms.profile.v2.attributes.AttributeFilter} that rejects attributes to which the current
 * application doesn't have permission to write (or that don't have a corresponding definition).
 *
 * @author avasquez
 */
public class RejectUnAllowedOnWriteAttributeFilter implements AttributeFilter {

    protected PermissionEvaluator<Application, AttributeDefinition> permissionEvaluator;

    @Required
    public void setPermissionEvaluator(PermissionEvaluator<Application, AttributeDefinition> permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    @Override
    public Map<String, Object> filter(Tenant tenant, Map<String, Object> attributes) throws AttributeFilterException {
        List<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();

        for (Map.Entry<String, Object> attribEntry : attributes.entrySet()) {
            String attributeName = attribEntry.getKey();
            Object attribute = attribEntry.getValue();

            rejectIfNotAllowed(attributeDefinitions, attributeName, attribute);
        }

        return attributes;
    }

    protected void rejectIfNotAllowed(List<AttributeDefinition> attributeDefinitions, String attributeName,
                                      Object attribute) throws AttributeFilterException {
        AttributeDefinition definition = getAttributeDefinitionByName(attributeDefinitions, attributeName);
        if (definition != null) {
            try {
                if (permissionEvaluator.isAllowed(definition, AttributeActions.WRITE)) {
                    List<AttributeDefinition> subAttributeDefinitions = definition.getSubAttributeDefinitions();

                    if (attribute instanceof Map && CollectionUtils.isNotEmpty(subAttributeDefinitions)) {
                        Map<String, Object> subAttributes = (Map<String, Object>) attribute;

                        for (Map.Entry<String, Object> subAttribEntry : subAttributes.entrySet()) {
                            String subAttribName = subAttribEntry.getKey();
                            Object subAttrib = subAttribEntry.getValue();

                            rejectIfNotAllowed(subAttributeDefinitions, subAttribName, subAttrib);
                        }
                    }
                } else {
                    throw new AttributeWriteNotAllowedException(attributeName);
                }
            } catch (PermissionException e) {
                throw new AttributeFilterException("Error while checking permissions for attribute '" +
                        attributeName + "'", e);
            }
        } else {
            throw new UndefinedAttributeException(attributeName);
        }
    }

    protected AttributeDefinition getAttributeDefinitionByName(final List<AttributeDefinition> attributeDefinitions,
                                                               final String name) {
        return CollectionUtils.find(attributeDefinitions, new Predicate<AttributeDefinition>() {

            @Override
            public boolean evaluate(AttributeDefinition definition) {
                return definition.getName().equals(name);
            }

        });
    }

}
