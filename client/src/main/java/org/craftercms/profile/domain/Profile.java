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

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("profile")
@XmlRootElement
public class Profile implements Serializable {
	private static final long serialVersionUID = 3370284215738389717L;

	private String id;

	private String userName;
	
	private String password;

	private Boolean active;

	private Date created;

	private Date modified;
	
	private String tenantName;

    private Map<String, Object> attributes;
	
	private List<String> roles;
	
	public Profile() {
	}

	public Profile(String id, String userName, String password, Boolean active, Date created, Date modified, Map<String, Object> attributes) {

		this(id, userName, password, active, created, modified, attributes, null, "");
	}
	
	public Profile(String id, String userName, String password, Boolean active, Date created, Date modified, Map<String, Object> attributes, List<String> roles, String tenantName) {
		super();

		this.id = id;
		this.userName = userName;
		this.password = password;
		this.active = active;
		this.created = created;
		this.modified = modified;
		this.attributes = attributes;
		this.roles = roles;
		this.tenantName = tenantName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

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

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public String toString() {
		return String.format("Profile [id='%s' userName='%s' password='%s' active='%b' created='%tc' modified='%tc']", id, userName, password, active, created, modified);
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}