package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.permissions.DefaultPermission;

/**
 * Permission for the PROFILE_SUPERADMIN role (can do anything).
 *
 * @author avasquez
 */
public class SuperadminPermission extends DefaultPermission {

    public SuperadminPermission() {
        allowAny();
    }

}
