package org.craftercms.profile.management.security.permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.craftercms.commons.security.permissions.Permission;

import static org.craftercms.profile.management.security.AuthorizationUtils.PROFILE_ADMIN_ROLE;
import static org.craftercms.profile.management.security.AuthorizationUtils.SUPERADMIN_ROLE;
import static org.craftercms.profile.management.security.AuthorizationUtils.TENANT_ADMIN_ROLE;

/**
 * Permission that ensures that the current profile isn't trying to modify another profile if the former has an
 * inferior role than the later.
 *
 * @author avasquez
 */
public class SubjectRoleIsNotInferiorPermission implements Permission {

    private static final Map<String, String[]> superiorRolesMap = new HashMap<>();

    static {
        superiorRolesMap.put(SUPERADMIN_ROLE, new String[0]);
        superiorRolesMap.put(TENANT_ADMIN_ROLE, new String[] {SUPERADMIN_ROLE});
        superiorRolesMap.put(PROFILE_ADMIN_ROLE, new String[] {TENANT_ADMIN_ROLE, SUPERADMIN_ROLE});
    }

    protected String subjectRole;
    protected Set<String> objectRoles;

    public SubjectRoleIsNotInferiorPermission(String subjectRole, Set<String> objectRoles) {
        this.subjectRole = subjectRole;
        this.objectRoles = objectRoles;
    }

    @Override
    public boolean isAllowed(String action) {
        String[] superiorRoles = superiorRolesMap.get(subjectRole);
        for (String superiorRole : superiorRoles) {
            if (objectRoles.contains(superiorRole)) {
                return false;
            }
        }

        return true;
    }

}
