package org.craftercms.profile.social.utils;

/**
 * Utility interface that can be implemented to be able to resolve the current tenant.
 *
 * @author avasquez
 */
public interface TenantResolver {

    /**
     * Returns the current tenant.
     */
    String getCurrentTenant();

}
