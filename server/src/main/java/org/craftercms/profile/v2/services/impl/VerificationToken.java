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
    private String profileId;
    private Date timestamp;

    public VerificationToken() {
    }

    public VerificationToken(String profileId, Date timestamp) {
        this.profileId = profileId;
        this.timestamp = timestamp;
    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId id) {
        this._id = id;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
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
                ", profileId='" + profileId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

}
