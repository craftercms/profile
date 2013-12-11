package org.craftercms.profile.api;

import java.util.List;

/**
 * A set of actions that a certain authority (either a user, a role or a group) can apply on a secure object.
 *
 * @author avasquez
 */
public class Permission {

    private String authority;
    private List<String> allowedActions;
    private List<String> deniedActions;

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public List<String> getAllowedActions() {
        return allowedActions;
    }

    public void setAllowedActions(List<String> allowedActions) {
        this.allowedActions = allowedActions;
    }

    public List<String> getDeniedActions() {
        return deniedActions;
    }

    public void setDeniedActions(List<String> deniedActions) {
        this.deniedActions = deniedActions;
    }

}
