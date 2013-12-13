package org.craftercms.profile.api;

import java.util.*;

/**
 * Representation of a user.
 *
 * @author avasquez
 */
public class User {

    private String username;
    private String password;
    private String email;
    private Boolean enabled;
    private Date created;
    private Date modified;
    private String tenant;
    private List<String> roles;
    private List<Group> groups;
    private Map<String, Object> attributes;

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public List<String> getRoles() {
        if (roles == null) {
            roles = new ArrayList<String>();
        }

        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<Group> getGroups() {
        if (groups == null) {
            groups = new ArrayList<Group>();
        }

        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }

        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

}
