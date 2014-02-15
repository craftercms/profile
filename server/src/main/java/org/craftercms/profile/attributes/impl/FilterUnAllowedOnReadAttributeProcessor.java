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
import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.PermissionService;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.attributes.AttributesProcessor;
import org.craftercms.profile.exceptions.AttributeProcessorException;
import org.craftercms.profile.util.ApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * {@link org.craftercms.profile.attributes.AttributesProcessor} that filters out attributes to which the current
 * application doesn't have permission to read.
 *
 * @author avasquez
 */
public class FilterUnAllowedOnReadAttributeProcessor implements AttributesProcessor {

    protected PermissionService permissionService;
    protected String readActionName;

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setReadActionName(String readActionName) {
        this.readActionName = readActionName;
    }

    @Override
    public Map<String, Object> process(Map<String, Object> attributes) throws AttributeProcessorException {
        String application = ApplicationContext.getCurrent().getApplication();
        Tenant tenant = ApplicationContext.getCurrent().getTenant();
        List<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();

        for (AttributeDefinition attributeDefinition : attributeDefinitions) {
            filterIfNotAllowed(application, attributeDefinition, attributes);
        }

        return attributes;
    }

    protected void filterIfNotAllowed(String application, AttributeDefinition attributeDefinition,
                                      Map<String, Object> attributes) throws AttributeProcessorException {
        try {
            if (!permissionService.allow(application, attributeDefinition, readActionName, null, false)) {
                Object attribute = attributes.get(attributeDefinition.getName());
                List<AttributeDefinition> subAttributeDefinitions = attributeDefinition.getSubAttributeDefinitions();

                if (attribute instanceof Map && CollectionUtils.isNotEmpty(subAttributeDefinitions)) {
                    Map<String, Object> subAttributes = (Map<String, Object>) attribute;

                    for (AttributeDefinition subAttributeDefinition : subAttributeDefinitions) {
                        filterIfNotAllowed(application, subAttributeDefinition, subAttributes);
                    }
                }
            } else {
                attributes.remove(attributeDefinition.getName());
            }
        } catch (PermissionException e) {
            throw new AttributeProcessorException("Error while checking permissions for attribute '" +
                    attributeDefinition.getName() + "'", e);
        }
    }

}
