package org.craftercms.profile.v2.services.impl;

import java.util.Date;

/**
 * Verification token, used for verifying a new profile with the user.
 *
 * @author avasquez
 */
public class VerificationToken {

    private String userId;
    private Date expirationDate;

    public VerificationToken() {
    }

    public VerificationToken(String userId, Date expirationDate) {
        this.userId = userId;
        this.expirationDate = expirationDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String toString() {
        return "VerificationToken{" +
                "userId='" + userId + '\'' +
                ", expirationDate=" + expirationDate +
                '}';
    }

}
