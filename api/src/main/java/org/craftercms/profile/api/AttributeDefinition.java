package org.craftercms.profile.api;


import org.craftercms.commons.security.permissions.SecuredObjectBase;

import java.util.Map;

/**
 * Represents the definition of an attribute in a tenant.
 *
 * @author avasquez
 */
public class AttributeDefinition extends SecuredObjectBase<AttributePermission> {

    private String name;
    private String owner;
    private Map<String, Object> metadata;

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
     * Returns the owner (application) of the attribute definition.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner (application) of the attribute definition.
     */
    public void setOwner(final String owner) {
        this.owner = owner;
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

}
