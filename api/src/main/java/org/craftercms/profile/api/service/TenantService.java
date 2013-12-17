package org.craftercms.profile.api.service;

import org.craftercms.profile.api.Tenant;

import java.util.List;

/**
 * Service for handling tenants.
 *
 * @author avasquez
 */
public interface TenantService {

    /**
     * Creates a new tenant.
     *
     * @param name              the tenant's name
     * @param verifyNewAccounts if when a new user account is created, an email to verify it should be sent to
     *                          the user
     * @param roles             the roles available to users of the tenant
     * @param groups            the groups available to users of the tenant
     */
    void createTenant(String name, boolean verifyNewAccounts, List<String> roles, List<String> groups);

    /**
     * Updates a tenant.
     *
     * @param name              the tenant's name
     * @param verifyNewAccounts if when a new user account is created, an email to verify it should be sent to
     *                          the user
     * @param roles             the roles available to users of the tenant
     * @param groups            the groups available to users of the tenant
     */
    void updateTenant(String name, boolean verifyNewAccounts, List<String> roles, List<String> groups);

    /**
     * Deletes a tenant.
     *
     * @param name  the tenant's name
     */
    void deleteTenant(String name);

    /**
     * Returns a tenant.
     *
     * @param name  the tenant's name
     *
     * @return the tenant
     */
    Tenant getTenant(String name);

    /**
     * Returns the total number of tenants.
     *
     * @return the number of tenants
     */
    int getTenantCount();

    /**
     * Returns a list with all the tenants.
     *
     * @return a list with al the tenants.
     */
    List<Tenant> getAllTenants();

}
