package org.craftercms.profile.api;

import java.util.Date;

/**
 * An authentication ticket for a profile. The ticket will later be encrypted for secure transmission.
 *
 * @author avasquez
 */
public class Ticket {

    private String userId;
    private Date expiresOn;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }

}
