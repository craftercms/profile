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

    public static final String SUPERADMIN_ROLE = "PROFILE_SUPERADMIN";
    public static final String TENANT_ADMIN_ROLE = "PROFILE_TENANT_ADMIN";
    public static final String PROFILE_ADMIN_ROLE = "PROFILE_ADMIN";

    private AuthorizationUtils() {
    }

    public static boolean isSuperadmin(Profile profile) {
        return profile.hasRole(SUPERADMIN_ROLE);
    }

    public static boolean isTenantAdmin(Profile profile) {
        return profile.hasAnyRole(SUPERADMIN_ROLE, TENANT_ADMIN_ROLE);
    }

    public static boolean isOnlyTenantAdmin(Profile profile) {
        return profile.hasRole(TENANT_ADMIN_ROLE);
    }

    public static boolean isTenantAdminForTenant(Profile profile, String tenant) {
        return profile.hasRole(SUPERADMIN_ROLE) ||
               (profile.hasRole(TENANT_ADMIN_ROLE) && profile.getTenant().equals(tenant));
    }

    public static boolean isProfileAdmin(Profile profile) {
        return profile.hasAnyRole(SUPERADMIN_ROLE, TENANT_ADMIN_ROLE, PROFILE_ADMIN_ROLE);
    }

    public static boolean isOnlyProfileAdmin(Profile profile) {
        return profile.hasRole(PROFILE_ADMIN_ROLE);
    }

    public static boolean isProfileAdminForTenant(Profile profile, String tenant) {
        return profile.hasRole(SUPERADMIN_ROLE) ||
               (profile.hasAnyRole(TENANT_ADMIN_ROLE, PROFILE_ADMIN_ROLE) && profile.getTenant().equals(tenant));
    }

    public static void checkCurrentUserIsSuperadmin() {
        if (!isSuperadmin(SecurityUtils.getCurrentProfile())) {
            throw new UnauthorizedException("Current user should be a superadmin");
        }
    }

    public static void checkCurrentUserIsTenantAdmin(String tenant) {
        if (!isTenantAdminForTenant(SecurityUtils.getCurrentProfile(), tenant)) {
            throw new UnauthorizedException("Current user is not an admin for tenant '" + tenant + "'");
        }
    }

    public static void checkCurrentUserIsProfileAdmin(String tenant) {
        if (!isProfileAdminForTenant(SecurityUtils.getCurrentProfile(), tenant)) {
            throw new UnauthorizedException("Current user is not a profile admin for tenant '" + tenant + "'");
        }
    }

}
