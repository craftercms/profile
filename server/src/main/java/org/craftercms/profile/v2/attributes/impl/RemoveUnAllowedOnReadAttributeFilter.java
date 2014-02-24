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
import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.AttributeActions;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.v2.attributes.AttributeFilter;
import org.craftercms.profile.v2.exceptions.AttributeFilterException;
import org.craftercms.profile.v2.permissions.Application;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;

/**
 * {@link org.craftercms.profile.v2.attributes.AttributeFilter} that filters out attributes to which the current
 * application doesn't have permission to read.
 *
 * @author avasquez
 */
public class RemoveUnAllowedOnReadAttributeFilter implements AttributeFilter {

    protected PermissionEvaluator<Application, AttributeDefinition> permissionEvaluator;

    @Required
    public void setPermissionEvaluator(PermissionEvaluator<Application, AttributeDefinition> permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    @Override
    public Map<String, Object> filter(Tenant tenant, Map<String, Object> attributes) throws AttributeFilterException {
        List<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();

        for (AttributeDefinition attributeDefinition : attributeDefinitions) {
            filterIfNotAllowed(attributeDefinition, attributes);
        }

        return attributes;
    }

    protected void filterIfNotAllowed(AttributeDefinition attributeDefinition, Map<String, Object> attributes)
            throws AttributeFilterException {
        try {
            if (!permissionEvaluator.isAllowed(attributeDefinition, AttributeActions.READ)) {
                Object attribute = attributes.get(attributeDefinition.getName());
                List<AttributeDefinition> subAttributeDefinitions = attributeDefinition.getSubAttributeDefinitions();

                if (attribute instanceof Map && CollectionUtils.isNotEmpty(subAttributeDefinitions)) {
                    Map<String, Object> subAttributes = (Map<String, Object>) attribute;

                    for (AttributeDefinition subAttributeDefinition : subAttributeDefinitions) {
                        filterIfNotAllowed(subAttributeDefinition, subAttributes);
                    }
                }
            } else {
                attributes.remove(attributeDefinition.getName());
            }
        } catch (PermissionException e) {
            throw new AttributeFilterException("Error while checking permissions for attribute '" +
                    attributeDefinition.getName() + "'", e);
        }
    }

}
