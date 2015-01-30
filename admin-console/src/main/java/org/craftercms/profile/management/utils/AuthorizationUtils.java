package org.craftercms.profile.management.utils;

import org.craftercms.profile.api.Profile;
import org.craftercms.profile.management.exceptions.UnauthorizedException;
import org.craftercms.security.utils.SecurityUtils;

/**
 * Authorization related utility methods.
 *
 * @author avasquez
 */
public class AuthorizationUtils {

    public static final String SUPERADMIN_ROLE = "PROFILE_ADMIN";
    public static final String TENANT_ADMIN_ROLE = "PROFILE_TENANT_ADMIN";

    private AuthorizationUtils() {
    }

    public static boolean isCurrentUserSuperadmin() {
        return isSuperadmin(SecurityUtils.getCurrentProfile());
    }

    public static boolean isSuperadmin(Profile profile) {
        return profile.hasRole(SUPERADMIN_ROLE);
    }

    public static boolean isCurrentUserTenantAdmin() {
        return isTenantAdmin(SecurityUtils.getCurrentProfile());
    }

    public static boolean isTenantAdmin(Profile profile) {
        return profile.hasRole(TENANT_ADMIN_ROLE);
    }

    public static void checkCurrentUserIsSuperadmin() {
        if (!isSuperadmin(SecurityUtils.getCurrentProfile())) {
            throw new UnauthorizedException("Current user should be a superadmin");
        }
    }

    public static void checkCurrentUserIsAdminForTenant(String tenant) {
        Profile profile = SecurityUtils.getCurrentProfile();

        if (!isSuperadmin(profile) && !profile.getTenant().equals(tenant)) {
            throw new UnauthorizedException("Current user is not an admin for tenant '" + tenant + "'");
        }
    }

}
