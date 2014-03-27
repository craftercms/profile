package org.craftercms.profile.v2.services.impl;

import org.jongo.marshall.jackson.oid.ObjectId;

import java.util.Date;

/**
 * Verification token, used for verifying a new profile with the user or for verifying a reset password request.
 *
 * @author avasquez
 */
public class VerificationToken {

    private ObjectId _id;
    private String userId;
    private Date timestamp;

    public VerificationToken() {
    }

    public VerificationToken(String userId, Date timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId id) {
        this._id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "VerificationToken{" +
                ", id=" + _id +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

}
