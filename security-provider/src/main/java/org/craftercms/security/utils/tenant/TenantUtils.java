package org.craftercms.security.utils.tenant;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.security.utils.SecurityUtils;

/**
 * Tenant related utility methods.
 *
 * @author avasquez
 */
public class TenantUtils {

    private TenantUtils() {
    }

    /**
     * Returns a list with the names of all tenants.
     *
     * @param tenantService the service that retrieves the {@link org.craftercms.profile.api.Tenant}s.
     *
     * @return the list of tenant names
     */
    public static List<String> getTenantNames(TenantService tenantService) throws ProfileException {
        List<Tenant> tenants = tenantService.getAllTenants();
        List<String> tenantNames = new ArrayList<>(tenants.size());

        if (CollectionUtils.isNotEmpty(tenants)) {
            for (Tenant tenant : tenants) {
                tenantNames.add(tenant.getName());
            }
        }

        return tenantNames;
    }

    /**
     * Returns the current tenant name, which is the tenant of the currently authenticated profile.
     *
     * @return the current tenant name.
     */
    public static String getCurrentTenantName() {
        Profile profile = SecurityUtils.getCurrentProfile();
        if (profile != null) {
            return profile.getTenant();
        } else {
            return null;
        }
    }

}
