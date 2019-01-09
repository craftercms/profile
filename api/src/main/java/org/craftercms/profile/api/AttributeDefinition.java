/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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

package org.craftercms.profile.api;


import org.craftercms.commons.security.permissions.ProtectedResourceBase;

import java.util.Map;

/**
 * Represents the definition of an attribute in a tenant.
 *
 * @author avasquez
 */
public class AttributeDefinition extends ProtectedResourceBase<AttributePermission> {

    private String name;
    private Map<String, Object> metadata;
    private Object defaultValue;

    public AttributeDefinition() {
    }

    public AttributeDefinition(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the attribute name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the additional metadata associated to an attribute (like label, type, etc.).
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Sets the additional metadata associated to an attribute (like label, type, etc.).
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * Returns the default value for all attributes.
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value of all attributes.
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AttributeDefinition that = (AttributeDefinition) o;

        if (!name.equals(that.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "AttributeDefinition{" +
                "name='" + name + '\'' +
                ", metadata=" + metadata +
                ", permissions=" + permissions +
                '}';
    }

}
