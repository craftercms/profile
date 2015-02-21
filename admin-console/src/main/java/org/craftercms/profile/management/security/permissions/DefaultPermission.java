package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.permissions.PermissionBase;

/**
 * Created by alfonsovasquez on 20/2/15.
 */
public class DefaultPermission extends PermissionBase {

    public DefaultPermission(Action... allowedActions) {
        for (Action action : allowedActions) {
            allow(action.toString());
        }
    }

}
