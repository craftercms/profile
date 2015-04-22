package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.Permission;
import org.craftercms.commons.security.permissions.PermissionResolver;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.management.security.AuthorizationUtils;

/**
 * {@link org.craftercms.commons.security.permissions.PermissionResolver} for tenant permissions.
 *
 * @author avasquez
 */
public class TenantPermissionResolver implements PermissionResolver<Profile, String> {

    @Override
    public Permission getGlobalPermission(Profile currentUser) throws PermissionException {
        if (AuthorizationUtils.isSuperadmin(currentUser)) {
            return new SuperadminPermission();
        } else {
            return null;
        }
    }

    @Override
    public Permission getPermission(Profile currentUser, String tenant) throws PermissionException {
        if (AuthorizationUtils.isSuperadmin(currentUser)) {
            return new SuperadminPermission();
        } else if (AuthorizationUtils.isTenantAdmin(currentUser)) {
            return new TenantAdminTenantPermission(currentUser, tenant);
        } else {
            return new ProfileAdminTenantPermission(currentUser, tenant);
        }
    }

}
