package org.craftercms.profile.api;

import java.util.ArrayList;
import java.util.List;

/**
 * A security group a user can be member of. Groups have several roles.
 *
 * @author avasquez
 */
public class Group {

    private String name;
    private List<String> roles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        if (roles == null) {
            roles = new ArrayList<String>();
        }

        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
