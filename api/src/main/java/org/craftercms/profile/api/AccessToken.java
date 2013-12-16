package org.craftercms.profile.api;

import java.util.Date;

/**
 * Access token given to applications that need to access the REST API, acting for a specific tenant. An app can only
 * have one access token per tenant at any given time. The token will later be encrypted for secure transmission.
 *
 * @author avasquez
 */
public class AccessToken {

    private String application;
    private String tenant;
    private Date expiresOn;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public Date getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }

}
