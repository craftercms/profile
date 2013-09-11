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
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.bson.types.ObjectId;
import org.craftercms.profile.constants.ProfileConstants;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

@XStreamAlias("role")
@XmlRootElement
public class Role implements Serializable {
    @Field(ProfileConstants.FIELD_ID)
    private ObjectId id;

    //	@Field(ProfileConstants.TENANT_NAME)
    //	private String tenantName;

    @Indexed(unique = true)
    @Field(ProfileConstants.ROLE_NAME)
    private String roleName;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    //	public String getTenantName() {
    //		return tenantName;
    //	}
    //
    //	public void setTenantName(String tenantName) {
    //		this.tenantName = tenantName;
    //	}
}
