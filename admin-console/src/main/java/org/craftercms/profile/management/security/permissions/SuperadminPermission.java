package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.permissions.PermissionBase;

/**
 * Created by alfonsovasquez on 20/2/15.
 */
public class SuperadminPermission extends PermissionBase {

    public SuperadminPermission() {
        allowAny();
    }

}
