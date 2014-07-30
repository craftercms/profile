package org.craftercms.profile.api;

import java.util.Date;

import org.bson.types.ObjectId;

/**
 * An authentication ticket for a profile. The ticket will later be encrypted for secure transmission.
 *
 * @author avasquez
 */
public class Ticket {

    private ObjectId _id;
    private String tenant;
    private String profileId;
    private Date lastRequestTime;

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId id) {
        this._id = id;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public Date getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(Date lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + _id +
                ", tenant='" + tenant + '\'' +
                ", profileId='" + profileId + '\'' +
                ", lastRequestTime=" + lastRequestTime +
                '}';
    }

}
