package org.craftercms.profile.api;

import org.jongo.marshall.jackson.oid.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * A tenant is normally an application that shares common configuration.
 *
 * @author avasquez
 */
public class Tenant {

    private String name;
    private boolean verifyNewAccounts;
    private List<String> roles;
    private List<AttributeDefinition> attributeDefinitions;

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
     * Returns true if new accounts should be verified through email by the user, for the accounts or users of
     * this tenant.
     */
    public boolean isVerifyNewAccounts() {
        return verifyNewAccounts;
    }

    /**
     * Sets if new accounts should be verified through email by the user, for the accounts or users of
     * this tenant.
     *
     * @param verifyNewAccounts true to verify new accounts, false otherwise
     */
    public void setVerifyNewAccounts(boolean verifyNewAccounts) {
        this.verifyNewAccounts = verifyNewAccounts;
    }

    /**
     * Returns the roles that can be assigned to users of this tenant.
     */
    public List<String> getRoles() {
        if (roles == null) {
            roles = new ArrayList<String>();
        }

        return roles;
    }

    /**
     * Sets the roles that can be assigned to users of this tenant.
     *
     * @param roles the available roles for users of the tenant.
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * Returns the definitions of attributes that user of this tenant can contain.
     */
    public List<AttributeDefinition> getAttributeDefinitions() {
        if (attributeDefinitions == null) {
            attributeDefinitions = new ArrayList<AttributeDefinition>();
        }

        return attributeDefinitions;
    }

    /**
     * Sets the definitions of attributes that users of this tenant can contain.
     *
     * @param attributeDefinitions  the available attribute definitions for users of the tenant
     */
    public void setAttributeDefinitions(List<AttributeDefinition> attributeDefinitions) {
        this.attributeDefinitions = attributeDefinitions;
    }

}
