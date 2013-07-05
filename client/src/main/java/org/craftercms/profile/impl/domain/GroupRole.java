package org.craftercms.profile.impl.domain;

import java.util.ArrayList;
import java.util.List;

public class GroupRole {
	
	private String id;
	private String name;
	private String tenantName;
	private List<String> roles = new ArrayList();
//	private String toString;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
//	public String toString() {
//		if (roles==null) {
//			return "";
//		}
//		toString = "";
//		for (String r: roles) {
//			toString = toString + " " + r;
//		}
//		return toString;
//	}

}
