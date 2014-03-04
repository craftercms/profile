package org.craftercms.profile.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

/**
 * Representation of a user.
 *
 * @author avasquez
 */
public class Profile {

    private ObjectId _id;
    private String username;
    private String password;
    private String email;
    private Boolean enabled;
    private Date created;
    private Date modified;
    private String tenant;
    private List<String> roles;
    private Map<String, Object> attributes;

    public ObjectId getId() {
        return _id;
    }

    public void setId(final ObjectId id) {
        this._id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(final Date modified) {
        this.modified = modified;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(final String tenant) {
        this.tenant = tenant;
    }

    public List<String> getRoles() {
        if (roles == null) {
            roles = new ArrayList<>();
        }

        return roles;
    }

    public void setRoles(final List<String> roles) {
        this.roles = roles;
    }

    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
        }

        return attributes;
    }

    public void setAttributes(final Map<String, Object> attributes) {
        this.attributes = attributes;
    }

}
