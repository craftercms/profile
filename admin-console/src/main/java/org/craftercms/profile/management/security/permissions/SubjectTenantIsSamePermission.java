package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.permissions.Permission;

/**
 * Created by alfonsovasquez on 20/2/15.
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
