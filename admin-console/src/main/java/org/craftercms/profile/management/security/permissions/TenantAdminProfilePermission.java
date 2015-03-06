package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.permissions.CompositePermission;
import org.craftercms.profile.api.Profile;

import static org.craftercms.profile.management.security.permissions.Action.CREATE_PROFILE;
import static org.craftercms.profile.management.security.permissions.Action.DELETE_PROFILE;
import static org.craftercms.profile.management.security.permissions.Action.GET_PROFILE;
import static org.craftercms.profile.management.security.permissions.Action.UPDATE_PROFILE;
import static org.craftercms.profile.management.security.AuthorizationUtils.TENANT_ADMIN_ROLE;

/**
 * Profile permission for the PROFILE_TENANT_ADMIN role.
 *
 * @author avasquez
 */
public class TenantAdminProfilePermission extends CompositePermission {

    public TenantAdminProfilePermission(Profile currentUser, Profile object) {
        super(new SubjectTenantIsSamePermission(currentUser.getTenant(), object.getTenant()),
              new SubjectRoleIsNotInferiorPermission(TENANT_ADMIN_ROLE, object.getRoles()),
              new DefaultPermission(GET_PROFILE, CREATE_PROFILE, UPDATE_PROFILE, DELETE_PROFILE));
    }

}
