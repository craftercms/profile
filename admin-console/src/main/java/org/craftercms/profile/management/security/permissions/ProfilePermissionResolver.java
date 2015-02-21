package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.Permission;
import org.craftercms.commons.security.permissions.PermissionResolver;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.management.security.AuthorizationUtils;

/**
 * Created by alfonsovasquez on 20/2/15.
 */
public class ProfilePermissionResolver implements PermissionResolver<Profile, Profile> {

    @Override
    public Permission getGlobalPermission(Profile currentUser) throws PermissionException {
        throw  new UnsupportedOperationException();
    }

    @Override
    public Permission getPermission(Profile currentUser, Profile profile) throws PermissionException {
        if (AuthorizationUtils.isSuperadmin(currentUser)) {
            return new SuperadminPermission();
        } else if (AuthorizationUtils.isTenantAdmin(currentUser)) {
            return new TenantAdminProfilePermission(currentUser, profile);
        } else {
            return new ProfileAdminProfilePermission(currentUser, profile);
        }
    }

}
