package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.permissions.CompositePermission;
import org.craftercms.profile.api.Profile;

import static org.craftercms.profile.management.security.permissions.Action.GET_PROFILE_COUNT;
import static org.craftercms.profile.management.security.permissions.Action.GET_PROFILE_LIST;
import static org.craftercms.profile.management.security.permissions.Action.GET_TENANT;

/**
 * Created by alfonsovasquez on 20/2/15.
 */
public class ProfileAdminTenantPermission extends CompositePermission {

    public ProfileAdminTenantPermission(Profile currentUser, String tenant) {
        super(new SubjectTenantIsSamePermission(currentUser.getTenant(), tenant),
              new DefaultPermission(GET_TENANT, GET_PROFILE_COUNT, GET_PROFILE_LIST));
    }

}
