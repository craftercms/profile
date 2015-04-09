package org.craftercms.profile.api;


import java.util.Map;

import org.craftercms.commons.security.permissions.SecuredObjectBase;

/**
 * Represents the definition of an attribute in a tenant.
 *
 * @author avasquez
 */
public class AttributeDefinition extends SecuredObjectBase<AttributePermission> {

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
