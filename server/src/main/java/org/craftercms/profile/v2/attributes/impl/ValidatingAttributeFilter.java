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
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.v2.attributes.AttributeFilter;
import org.craftercms.profile.v2.exceptions.AttributeConstraintException;
import org.craftercms.profile.v2.exceptions.AttributeFilterException;
import org.craftercms.profile.v2.exceptions.InvalidAttributeTypeException;
import org.craftercms.profile.v2.exceptions.RequiredAttributeException;

import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link org.craftercms.profile.v2.attributes.AttributeFilter} that validates attributes
 * on write.
 *
 * @author avasquez
 */
public class ValidatingAttributeFilter implements AttributeFilter {

    @Override
    public Map<String, Object> filter(Tenant tenant, Map<String, Object> attributes) throws AttributeFilterException {
        List<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();

        for (AttributeDefinition attributeDefinition : attributeDefinitions) {
            validateAttribute(attributeDefinition, attributes.get(attributeDefinition.getName()));
        }

        return attributes;
    }

    protected void validateAttribute(AttributeDefinition definition, Object attribute)
            throws AttributeFilterException {
        String name = definition.getName();
        String type = definition.getType();
        String constraint = definition.getConstraint();
        List<AttributeDefinition> subAttributeDefinitions = definition.getSubAttributeDefinitions();

        if (definition.isRequired() && (attribute == null || isAttributeEmpty(attribute))) {
            throw new RequiredAttributeException(name);
        }

        Class<?> clazz;
        try {
            clazz = Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new AttributeFilterException("Can't find type " + type, e);
        }

        if (!clazz.isAssignableFrom(attribute.getClass())) {
            throw new InvalidAttributeTypeException(name, type, attribute.getClass().getName());
        }
        if (attributeNotMatchesConstraint(attribute, constraint)) {
            throw new AttributeConstraintException(name, constraint);
        }

        if (attribute instanceof Map && CollectionUtils.isNotEmpty(subAttributeDefinitions)) {
            Map<String, Object> subAttributes = (Map<String, Object>) attribute;

            for (AttributeDefinition subAttributeDefinition : subAttributeDefinitions) {
                validateAttribute(subAttributeDefinition, subAttributes.get(subAttributeDefinition.getName()));
            }
        }
    }

    private boolean isAttributeEmpty(Object attribute) {
        return attribute instanceof String && ((String) attribute).isEmpty();
    }

    private boolean attributeNotMatchesConstraint(Object attribute, String constraint) {
        return attribute instanceof String && !((String) attribute).matches(constraint);
    }

}
