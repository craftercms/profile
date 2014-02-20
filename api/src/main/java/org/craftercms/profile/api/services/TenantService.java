package org.craftercms.profile.api.services;

import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;

import java.util.List;

/**
 * Service for managing tenants.
 *
 * @author avasquez
 */
public interface TenantService {

    /**
     * Creates the given tenant, failing it already has been created.
     *
     * @param tenant    the tenant to create
     *
     * @return the tenant
     */
    Tenant createTenant(Tenant tenant);

    /**
     * Returns a tenant.
     *
     * @param name  the tenant's name
     *
     * @return the tenant
     */
    Tenant getTenant(String name);

    /**
     * Updates a tenant.
     *
     * @param tenant the updated tenant
     *
     * @return the tenant
     */
    Tenant updateTenant(Tenant tenant);

    /**
     * Deletes a tenant.
     *
     * @param name  the tenant's name
     *
     * @return the tenant
     */
    Tenant deleteTenant(String name);

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

    /**
     * Sets verify new accounts for specified tenant.
     *
     * @param tenantName    the tenant's name
     * @param verify        true to verify new accounts through email, false otherwise
     *
     * @return the tenant
     */
    Tenant verifyNewAccounts(String tenantName, boolean verify);

    /**
     * Adds the given roles to the specified tenant.
     *
     * @param tenantName    the tenant's name
     * @param roles         the roles to add
     *
     * @return the tenant
     */
    Tenant addRoles(String tenantName, String... roles);

    /**
     * Removes the given roles from the specified tenant.
     *
     * @param tenantName    the tenant's name
     * @param roles         the roles to remove
     *
     * @return the tenant
     */
    Tenant removeRoles(String tenantName, String... roles);

    /**
     * Adds the given attribute definitions to the current tenant.
     *
     * @param tenantName            the tenant's name
     * @param attributeDefinitions  the definitions to add
     *
     * @return the tenant
     */
    Tenant addAttributeDefinitions(String tenantName, AttributeDefinition... attributeDefinitions);

    /**
     * Removes the given attribute definitions from the current tenant.
     *
     * @param tenantName            the tenant's name
     * @param attributeDefinitions  the definitions to remove
     *
     * @return the tenant
     */
    Tenant removeAttributeDefinitions(String tenantName, AttributeDefinition... attributeDefinitions);

}
