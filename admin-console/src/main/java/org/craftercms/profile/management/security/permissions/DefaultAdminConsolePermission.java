package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.permissions.DefaultPermission;

/**
 * Default admin console permission.
 *
 * @author avasquez
 */
public class DefaultAdminConsolePermission extends DefaultPermission {

    public DefaultAdminConsolePermission(Action... allowedActions) {
        for (Action action : allowedActions) {
            allow(action.toString());
        }
    }

}
