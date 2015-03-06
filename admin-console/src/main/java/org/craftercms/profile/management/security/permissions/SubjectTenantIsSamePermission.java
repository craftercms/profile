package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.permissions.Permission;

/**
 * Permission that ensures that the current profile isn't trying to modify a tenant that it's not its own tenant or
 * a profile that doesn't correspond to its own tenant.
 *
 * @author avasquez
 */
public class SubjectTenantIsSamePermission implements Permission {

    private String subjectTenant;
    private String tenant;

    public SubjectTenantIsSamePermission(String subjectTenant, String tenant) {
        this.subjectTenant = subjectTenant;
        this.tenant = tenant;
    }

    @Override
    public boolean isAllowed(String action) {
        return subjectTenant.equals(tenant);
    }

}
