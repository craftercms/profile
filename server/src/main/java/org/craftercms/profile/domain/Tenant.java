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

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.Document;
import org.jongo.marshall.jackson.oid.Id;

@XStreamAlias("tenant")
@XmlRootElement
@Document(collectionName = "tenant")
public class Tenant {

    @Id
    private ObjectId id;
    private String tenantName;
    private Boolean emailNewProfile;
    private List<String> domains;
    private List<String> roles;
    private Schema schema;

    public ObjectId getId() {
        return id;
    }

    public void setId(final ObjectId id) {
        this.id = id;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(final String tenantName) {
        this.tenantName = tenantName;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(final Schema schema) {
        this.schema = schema;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(final List<String> domains) {
        this.domains = domains;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(final List<String> roles) {
        this.roles = roles;
    }

    public Boolean getEmailNewProfile() {
        return emailNewProfile;
    }

    public void setEmailNewProfile(final boolean emailNewProfile) {
        this.emailNewProfile = emailNewProfile;
    }


}
