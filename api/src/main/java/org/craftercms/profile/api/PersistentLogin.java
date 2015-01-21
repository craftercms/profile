package org.craftercms.profile.api;

import java.util.Date;

/**
 * Represents a persistent authentication or login, triggered by remember me functionality. The login information
 * stored is basically the one proposed in <a href="http://jaspan.com/improved_persistent_login_cookie_best_practice">
 * Improved Persistent Login Cookie Best Practice</a>. The ID is basically the login series identifier.
 *
 * @author avasquez
 */
public class PersistentLogin {

    private String _id;
    private String tenant;
    private String profileId;
    private String token;
    private Date timestamp;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(final String tenant) {
        this.tenant = tenant;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersistentLogin login = (PersistentLogin) o;

        if (!_id.equals(login._id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    public String toString() {
        return "PersistentLogin{" +
            "id='" + _id + '\'' +
            ", tenant='" + tenant + '\'' +
            ", profileId='" + profileId + '\'' +
            ", token='" + token + '\'' +
            ", timestamp='" + timestamp + '\'' +
            '}';
    }

}
