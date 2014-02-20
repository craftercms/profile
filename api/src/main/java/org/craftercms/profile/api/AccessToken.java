package org.craftercms.profile.api;

import java.util.Date;
import java.util.List;

/**
 * Access token given to applications that need to access the REST API, acting for any tenant of a list. The
 * token will later be encrypted for secure transmission.
 *
 * @author avasquez
 */
public class AccessToken {

    private String application;
    private List<TenantPermission> tenantPermissions;
    private Date expiresOn;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public List<TenantPermission> getTenantPermissions() {
        return tenantPermissions;
    }

    public void setTenantPermissions(List<TenantPermission> tenantPermissions) {
        this.tenantPermissions = tenantPermissions;
    }

    public Date getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }

}
