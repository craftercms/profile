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
package org.craftercms.profile.management.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.craftercms.profile.impl.domain.*;

import org.craftercms.profile.management.util.TenantUtil;

public class ProfileUserAccountForm implements Serializable{

    private String id;

    private String username;

	private String password;

    private String confirmPassword;

    private boolean active;

    private String tenantName;

    private Map<String, Object> attributes;

	private ArrayList<String> roles;
	
	private Map roleOption;
	
	public ProfileUserAccountForm() {}
	
	public ProfileUserAccountForm(Tenant tenant, List<String> roleList) {
        active = true;
        this.tenantName = tenant.getTenantName();
		initRoleOption(roleList);
		if (tenant.getSchema().getAttributes() != null) {
			attributes = TenantUtil.getSchemaAttributesAsMap(tenant);
		}else{
            attributes = new HashMap<String, Object>();
        }
	}
	
	public ProfileUserAccountForm(Profile profile, Tenant tenant, boolean clean) {
		if (!clean) {
            this.id = profile.getId();
			this.username = profile.getUserName();
			this.password = profile.getPassword();
			this.confirmPassword = password;
            this.active = profile.getActive();
			this.roles = (ArrayList<String>) profile.getRoles();
            this.tenantName = profile.getTenantName();
            this.attributes = TenantUtil.getSchemaAttributesAsMap(tenant, profile);
		}
		initRoleOption(tenant.getRoles());
		if (tenant.getSchema() != null && clean) {
		    this.attributes = TenantUtil.getSchemaAttributesAsMap(tenant);
        }
	}
	
	public ProfileUserAccountForm(Profile profile) {
		if (profile==null) {
			return;
		}
		this.password = profile.getPassword();
		this.confirmPassword = password;
		this.username = profile.getUserName();
		this.id = profile.getId();
		this.roles = (ArrayList<String>) profile.getRoles();
		this.active = profile.getActive();
        this.tenantName = profile.getTenantName();
	}

    public void initTenantValues(Tenant tenant){
        initRoleOption(tenant.getRoles());
        attributes = TenantUtil.getSchemaAttributesAsMap(tenant);
    }
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    public Map getRoleOption() {
        return roleOption;
    }

    private void initRoleOption(List<String> roles) {
        Map<String, String> data = new HashMap<String,String>();
        for(String r: roles) {
            data.put(r,r);
        }

        this.roleOption = data;

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
