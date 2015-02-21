package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.Permission;
import org.craftercms.commons.security.permissions.PermissionResolver;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.management.security.AuthorizationUtils;

/**
 * Created by alfonsovasquez on 20/2/15.
 */
public class TenantPermissionResolver implements PermissionResolver<Profile, String> {

    @Override
    public Permission getGlobalPermission(Profile subject) throws PermissionException {
        if (AuthorizationUtils.isSuperadmin(subject)) {
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
