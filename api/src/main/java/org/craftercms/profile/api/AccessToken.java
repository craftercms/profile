package org.craftercms.profile.api;

import java.util.Date;
import java.util.List;

/**
 * Access token given to applications that need to access the REST API, acting for any tenant of a list.
 *
 * @author avasquez
 */
public class AccessToken {

    private String _id;
    private String application;
    private boolean master;
    private List<TenantPermission> tenantPermissions;
    private Date expiresOn;

    /**
     * Returns the ID of the access token.
     */
    public String getId() {
        return _id;
    }

    /**
     * Sets the ID of the access token.
     *
     * @param id    the token's ID
     */
    public void setId(String id) {
        this._id = id;
    }

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
     * Returns true if this is a master token. A master token can be used to create and delete other tokens.
     */
    public boolean isMaster() {
        return master;
    }

    /**
     * Sets if this is a master token. A master token can be used to create and delete other tokens.
     *
     * @param master trues if this should be a master token, false otherwise
     */
    public void setMaster(boolean master) {
        this.master = master;
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

    @Override
    public String toString() {
        return "AccessToken{" +
                "id='" + _id + '\'' +
                ", application='" + application + '\'' +
                ", tenantPermissions=" + tenantPermissions +
                ", expiresOn=" + expiresOn +
                '}';
    }

}
