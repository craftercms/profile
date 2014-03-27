package org.craftercms.profile.api;

import org.bson.types.ObjectId;

import java.util.Date;

/**
 * An authentication ticket for a profile. The ticket will later be encrypted for secure transmission.
 *
 * @author avasquez
 */
public class Ticket {

    private ObjectId _id;
    private String userId;
    private Date timestamp;

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
        return "Ticket{" +
                "id=" + _id +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

}
