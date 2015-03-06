package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.permissions.PermissionBase;

/**
 * Default admin console permission.
 *
 * @author avasquez
 */
public class DefaultPermission extends PermissionBase {

    public DefaultPermission(Action... allowedActions) {
        for (Action action : allowedActions) {
            allow(action.toString());
        }
    }

}
