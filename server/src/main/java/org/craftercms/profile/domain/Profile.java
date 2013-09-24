/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.profile.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.bson.types.ObjectId;
import org.craftercms.profile.constants.ProfileConstants;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Field;

@XStreamAlias("profile")
@XmlRootElement
@CompoundIndexes({
        @CompoundIndex(name = "tenantName_userName_idx", def = "{'tenantName': 1, 'userName': 1}", unique = true),
        @CompoundIndex(name = "tenantName_idx", def = "{'tenantName': 1}" )
})
@JsonIgnoreProperties({ "password" })
public class Profile implements Serializable {
    private static final long serialVersionUID = 3370284215738389717L;

    @Field(ProfileConstants.FIELD_ID)
    private ObjectId id;

    @Field(ProfileConstants.USER_NAME)
    private String userName;
    
    @Field(ProfileConstants.PASSWORD)
    @XmlTransient
    private String password;

    @Field(ProfileConstants.ACTIVE)
    private Boolean active;

    @Field(ProfileConstants.CREATED)
    private Date created;

    @Field(ProfileConstants.MODIFIED)
    private Date modified;

    @Field(ProfileConstants.TENANT_NAME)
    private String tenantName;

    @Field(ProfileConstants.EMAIL)
    private String email;
    
    @Field(ProfileConstants.VERIFY)
    private Boolean verify;

    @Field(ProfileConstants.ROLES)
    private List<String> roles;

    @Field(ProfileConstants.ATTRIBUTES)
    private Map<String, Serializable> attributes;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @XmlTransient
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public Boolean getVerify() {
        return verify;
    }

    public void setVerify(Boolean verify) {
        this.verify = verify;
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

    public Map<String, Serializable> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Serializable> attributes) {
        this.attributes = attributes;
    }

    public String toString() {
        return String.format("Profile [id='%s' userName='%s' active='%b' email='%s'created='%tc' " +
            "modified='%tc']", id, userName, active, email, created, modified);
    }

    public String getTenantName() {
        return tenantName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}