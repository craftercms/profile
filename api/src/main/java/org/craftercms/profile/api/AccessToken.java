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

    /**
     * Returns the name of the application accessing Crafter Profile.
     */
    public String getApplication() {
        return application;
    }

    /**
     * Sets the name of the application accessing Crafter Profile.
     *
     * @param application   the application name
     */
    public void setApplication(String application) {
        this.application = application;
    }

    /**
     * Returns the tenant permissions the application has.
     */
    public List<TenantPermission> getTenantPermissions() {
        return tenantPermissions;
    }

    /**
     * Sets the he tenant permissions the application has.
     *
     * @param tenantPermissions the tenant permissions
     */
    public void setTenantPermissions(List<TenantPermission> tenantPermissions) {
        this.tenantPermissions = tenantPermissions;
    }

    /**
     * Returns the date of expiration of this token (when it becomes invalid)
     */
    public Date getExpiresOn() {
        return expiresOn;
    }

    /**
     * Sets the date of expiration of this token (when it becomes invalid)
     *
     * @param expiresOn the expiration date of the token
     */
    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }

}
