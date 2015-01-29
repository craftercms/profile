package org.craftercms.profile.management.utils;

import org.craftercms.security.utils.SecurityUtils;

/**
 * Authorization related utility methods.
 *
 * @author avasquez
 */
public class AuthorizationUtils {

    public static final String SUPERADMIN_ROLE = "PROFILE_ADMIN";

    private AuthorizationUtils() {
    }

    public static boolean isCurrentUserSuperadmin() {
        return SecurityUtils.getCurrentProfile().hasRole(SUPERADMIN_ROLE);
    }

}
