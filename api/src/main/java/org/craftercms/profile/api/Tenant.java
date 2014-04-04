package org.craftercms.profile.api;

import org.bson.types.ObjectId;

import java.util.HashSet;
import java.util.Set;

/**
 * A tenant is normally an application that shares common configuration.
 *
 * @author avasquez
 */
public class Tenant {

    private ObjectId _id;
    private String name;
    private boolean verifyNewProfiles;
    private Set<String> roles;
    private Set<AttributeDefinition> attributeDefinitions;

    /**
     * Returns the tenant's DB ID.
     */
    public ObjectId getId() {
        return _id;
    }

    /**
     * Sets the tenant's DB ID.
     *
     * @param id the ID
     */
    public void setId(ObjectId id) {
        this._id = id;
    }

    /**
     * Returns the name of the tenant.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the tenant.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns true if new profiles should be verified through email by the user, for the accounts or users of
     * this tenant.
     */
    public boolean isVerifyNewProfiles() {
        return verifyNewProfiles;
    }

    /**
     * Sets if new profiles should be verified through email by the user, for the accounts or users of
     * this tenant.
     *
     * @param verifyNewProfiles true to verify new profiles, false otherwise
     */
    public void setVerifyNewProfiles(boolean verifyNewProfiles) {
        this.verifyNewProfiles = verifyNewProfiles;
    }

    /**
     * Returns the roles that can be assigned to users of this tenant.
     */
    public Set<String> getRoles() {
        if (roles == null) {
            roles = new HashSet<>();
        }

        return roles;
    }

    /**
     * Sets the roles that can be assigned to users of this tenant.
     *
     * @param roles the available roles for users of the tenant.
     */
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    /**
     * Returns the definitions of attributes that user of this tenant can contain.
     */
    public Set<AttributeDefinition> getAttributeDefinitions() {
        if (attributeDefinitions == null) {
            attributeDefinitions = new HashSet<>();
        }

        return attributeDefinitions;
    }

    /**
     * Sets the definitions of attributes that users of this tenant can contain.
     *
     * @param attributeDefinitions  the available attribute definitions for users of the tenant
     */
    public void setAttributeDefinitions(Set<AttributeDefinition> attributeDefinitions) {
        this.attributeDefinitions = attributeDefinitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tenant tenant = (Tenant) o;

        if (!_id.equals(tenant._id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id=" + _id +
                ", name='" + name + '\'' +
                ", verifyNewProfiles=" + verifyNewProfiles +
                ", roles=" + roles +
                ", attributeDefinitions=" + attributeDefinitions +
                '}';
    }

}
