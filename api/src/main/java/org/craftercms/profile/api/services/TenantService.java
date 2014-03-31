package org.craftercms.profile.api.services;

import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.exceptions.ProfileException;

import java.util.Collection;
import java.util.Set;

/**
 * Service for managing tenants.
 *
 * @author avasquez
 */
public interface TenantService {

    /**
     * Creates the given tenant, failing it already has been created.
     *
     * @param name                  the tenant's name
     * @param verifyNewProfiles     if new profiles should be verified through email by the user
     * @param roles                 the usable roles by the tenant's profiles
     *
     * @return the created tenant
     */
    Tenant createTenant(String name, boolean verifyNewProfiles, Set<String> roles) throws ProfileException;

    /**
     * Returns a tenant.
     *
     * @param name  the tenant's name
     *
     * @return the tenant
     */
    Tenant getTenant(String name) throws ProfileException;

    /**
     * Updates a tenant.
     *
     * @param tenant the updated tenant
     */
    Tenant updateTenant(Tenant tenant) throws ProfileException;

    /**
     * Deletes a tenant.
     *
     * @param name  the tenant's name
     */
    void deleteTenant(String name) throws ProfileException;

    /**
     * Returns the total number of tenants.
     *
     * @return the number of tenants
     */
    long getTenantCount() throws ProfileException;

    /**
     * Returns a list with all the tenants.
     *
     * @return a list with al the tenants.
     */
    Iterable<Tenant> getAllTenants() throws ProfileException;

    /**
     * Sets verify new profiles for specified tenant.
     *
     * @param tenantName    the tenant's name
     * @param verify        true to verify new profiles through email, false otherwise
     *
     * @return the tenant
     */
    Tenant verifyNewProfiles(String tenantName, boolean verify) throws ProfileException;

    /**
     * Adds the given roles to the specified tenant.
     *
     * @param tenantName    the tenant's name
     * @param roles         the roles to add
     *
     * @return the tenant
     */
    Tenant addRoles(String tenantName, Collection<String> roles) throws ProfileException;

    /**
     * Removes the given roles from the specified tenant.
     *
     * @param tenantName    the tenant's name
     * @param roles         the roles to remove
     *
     * @return the tenant
     */
    Tenant removeRoles(String tenantName, Collection<String> roles) throws ProfileException;

    /**
     * Adds the given attribute definitions to the current tenant.
     *
     * @param tenantName            the tenant's name
     * @param attributeDefinitions  the definitions to add
     *
     * @return the tenant
     */
    Tenant addAttributeDefinitions(String tenantName, Collection<AttributeDefinition> attributeDefinitions)
            throws ProfileException;

    /**
     * Removes the given attribute definitions from the current tenant.
     *
     * @param tenantName        the tenant's name
     * @param attributeNames    the name of the attributes whose definitions should be removed
     *
     * @return the tenant
     */
    Tenant removeAttributeDefinitions(String tenantName, Collection<String> attributeNames) throws ProfileException;

}
