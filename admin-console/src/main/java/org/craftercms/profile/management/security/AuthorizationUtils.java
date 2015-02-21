package org.craftercms.profile.management.security;

import org.craftercms.profile.api.Profile;

/**
 * Authorization related utility methods.
 *
 * @author avasquez
 */
public class AuthorizationUtils {

    public static final String SUPERADMIN_ROLE = "PROFILE_SUPERADMIN";
    public static final String TENANT_ADMIN_ROLE = "PROFILE_TENANT_ADMIN";
    public static final String PROFILE_ADMIN_ROLE = "PROFILE_ADMIN";

    private AuthorizationUtils() {
    }

    public static boolean isSuperadmin(Profile profile) {
        return profile.hasRole(SUPERADMIN_ROLE);
    }

    public static boolean isTenantAdmin(Profile profile) {
        return profile.hasRole(TENANT_ADMIN_ROLE);
    }

    public static boolean isProfileAdmin(Profile profile) {
        return profile.hasRole(PROFILE_ADMIN_ROLE);
    }

}
