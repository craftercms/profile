package org.craftercms.profile.api;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of permissions with a certain propagation, used to restrict users to a
 * {@link org.craftercms.profile.api.SecuredObject}.
 *
 * @author avasquez
 */
public class AccessControlList {

    public enum Propagation {
        NONE,
        PROPAGATE,
        INHERIT;
    }
    
    private Propagation propagation;
    private List<Permission> permissions;

    public Propagation getPropagation() {
        return propagation;
    }

    public void setPropagation(Propagation propagation) {
        this.propagation = propagation;
    }

    public List<Permission> getPermissions() {
        if (permissions == null) {
            permissions = new ArrayList<Permission>();
        }
        
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
    
}
