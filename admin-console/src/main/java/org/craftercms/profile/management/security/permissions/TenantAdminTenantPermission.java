package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.permissions.CompositePermission;
import org.craftercms.profile.api.Profile;

import static org.craftercms.profile.management.security.permissions.Action.GET_PROFILE_COUNT;
import static org.craftercms.profile.management.security.permissions.Action.GET_PROFILE_LIST;
import static org.craftercms.profile.management.security.permissions.Action.GET_TENANT;
import static org.craftercms.profile.management.security.permissions.Action.UPDATE_TENANT;

/**
 * Tenant permission for the PROFILE_TENANT_ADMIN role.
 *
 * @author avasquez
 */
public class TenantAdminTenantPermission extends CompositePermission {

    public TenantAdminTenantPermission(Profile currentUser, String tenant) {
        super(new SubjectTenantIsSamePermission(currentUser.getTenant(), tenant),
              new DefaultPermission(GET_TENANT, UPDATE_TENANT, GET_PROFILE_COUNT, GET_PROFILE_LIST));
    }

}
