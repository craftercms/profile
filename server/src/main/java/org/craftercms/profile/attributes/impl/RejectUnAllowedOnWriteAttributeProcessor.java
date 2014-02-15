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
package org.craftercms.profile.attributes.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.PermissionService;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.attributes.AttributesProcessor;
import org.craftercms.profile.exceptions.AttributeProcessorException;
import org.craftercms.profile.exceptions.AttributeWriteNotAllowedException;
import org.craftercms.profile.exceptions.UndefinedAttributeException;
import org.craftercms.profile.util.ApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * {@link org.craftercms.profile.attributes.AttributesProcessor} that rejects attributes to which the current
 * application doesn't have permission to write (or that don't have a corresponding definition).
 *
 * @author avasquez
 */
public class RejectUnAllowedOnWriteAttributeProcessor implements AttributesProcessor {

    protected PermissionService permissionService;
    protected String writeActionName;

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setWriteActionName(String writeActionName) {
        this.writeActionName = writeActionName;
    }

    @Override
    public Map<String, Object> process(Map<String, Object> attributes) throws AttributeProcessorException {
        String application = ApplicationContext.getCurrent().getApplication();
        Tenant tenant = ApplicationContext.getCurrent().getTenant();
        List<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();

        for (Map.Entry<String, Object> attribEntry : attributes.entrySet()) {
            String attributeName = attribEntry.getKey();
            Object attribute = attribEntry.getValue();

            rejectIfNotAllowed(application, attributeDefinitions, attributeName, attribute);
        }

        return attributes;
    }

    protected void rejectIfNotAllowed(String application, List<AttributeDefinition> attributeDefinitions,
                                      String attributeName, Object attribute) throws AttributeProcessorException {
        AttributeDefinition definition = getAttributeDefinitionByName(attributeDefinitions, attributeName);
        if (definition != null) {
            try {
                if (permissionService.allow(application, definition, writeActionName, null, false)) {
                    List<AttributeDefinition> subAttributeDefinitions = definition.getSubAttributeDefinitions();

                    if (attribute instanceof Map && CollectionUtils.isNotEmpty(subAttributeDefinitions)) {
                        Map<String, Object> subAttributes = (Map<String, Object>) attribute;

                        for (Map.Entry<String, Object> subAttribEntry : subAttributes.entrySet()) {
                            String subAttribName = subAttribEntry.getKey();
                            Object subAttrib = subAttribEntry.getValue();

                            rejectIfNotAllowed(application, subAttributeDefinitions, subAttribName, subAttrib);
                        }
                    }
                } else {
                    throw new AttributeWriteNotAllowedException(attributeName);
                }
            } catch (PermissionException e) {
                throw new AttributeProcessorException("Error while checking permissions for attribute '" +
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
