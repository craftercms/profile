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
package org.craftercms.profile.services.impl;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.services.MultiTenantService;
import org.craftercms.profile.services.ProfileService;
import org.craftercms.profile.services.RoleService;
import org.craftercms.profile.services.SchemaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ProfileStartupServiceImpl implements ApplicationListener {
	
   private Map<String,String> schemaAttributes = new HashMap<String,String>(){
	   		{
	   			put("last-name","Last Name");	
	   			put("first-name","First Name"); 
	   		}
	   };
	
	private String profilePassword = "admin";
	private String profileUsername = "admin";
	private String superAdminPassword = "superadmin";
	private String superAdminUsername = "superadmin";
	private String tenantName = "craftercms"; 
	private List<String> adminRoles;
	private List<String> superadminRoles;
	
	@Autowired
	private MultiTenantService multiTenantService;
	
	@Autowired
	private SchemaService schemaService;
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private RoleService roleService;
	
	private boolean isDefaultRolesOn = true;
	private final transient Logger log = LoggerFactory
			.getLogger(ProfileStartupServiceImpl.class);
	
	private String propertiesFile;

	private List<String> appRoles;

	private List<String> tenantDefaultDomains;

	private List<String> tenantDefaultRoles;
	
	@Override
    public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
        	try {
        		startup();
        	} catch(Exception e) {
        		log.error("Profile startup error: ", e);
        	}
        }
    }

	public void startup() {
		if (!isTenantExist()) {

			createBasicCollections(tenantName);
			createBaseProfiles(tenantName);
			
		} 
		
		
	}
	
	private Tenant createBasicCollections(String tenantName) {
		Tenant tenant = null;
        
		tenant = this.multiTenantService.createTenant(tenantName, false, tenantDefaultRoles, tenantDefaultDomains);
		addAttributes(tenantName,schemaAttributes);
		
		if (this.isDefaultRolesOn) {
			createAppRoles(tenantName);
		}
		return tenant;
	}
	
	private void addAttributes(String tenantName, Map<String,String> attributes) {
		List<String> key = new ArrayList<String>(attributes.keySet());
		List<String> value = new ArrayList<String>(attributes.values());
		Attribute attribute;
		for (int i =0;i<key.size(); i++) {
			attribute = new Attribute();
			attribute.setLabel(value.get(i));
			attribute.setName(key.get(i));
			attribute.setOrder((i+1) * 100);
			attribute.setRequired(false);
			attribute.setConstraint("");
			schemaService.setAttribute(tenantName, attribute);
		}
	}
	

	private void createAppRoles(String tenantName) {
		for (String role: this.appRoles) {
			this.roleService.createRole(role, tenantName);
		}
	}
	
	private void createBaseProfiles(String tenantName) {
		
		if (this.adminRoles.size()==0) {
			adminRoles.add("ADMIN");
		}

		this.profileService.createProfile(profileUsername, profilePassword,true,tenantName,new HashMap<String,Serializable>(),this.adminRoles);
		log.info("ADMIN profile created");
		
		if (superAdminUsername==null) {
			return;
		}
		
		if (this.superadminRoles.size()==0) {
			adminRoles.add("SUPERADMIN");
		}

		this.profileService.createProfile(superAdminUsername, superAdminPassword,true,tenantName,new HashMap<String,Serializable>(),this.superadminRoles);
		log.info("SUPERADMIN profile created");
	}
	
	@Value("#{ssrSettings['default-domains']}")
	public void setTenantDefaultDomain(String defaultDomains) {
		this.tenantDefaultDomains = convertLineToList(defaultDomains);
		
	}
	@Value("#{ssrSettings['default-tenant-roles']}")
	public void setTenantDefaultRoles(String defaultRoles) {
		this.tenantDefaultRoles = convertLineToList(defaultRoles);
		
	}
	@Value("#{ssrSettings['profile-username']}")
	public void setProfileProfileUsername(String username) {
		this.profileUsername = username;
	}
	@Value("#{ssrSettings['profile-password']}")
	public void setProfileProfilePassword(String password) {
		this.profilePassword = password;
	}
	@Value("#{ssrSettings['profile-superadmin-password']}")
	public void setProfileSuperadminPassword(String superAdminPass) {
		this.superAdminPassword = superAdminPass;
	}
	@Value("#{ssrSettings['profile-superadmin-user']}")
	public void setProfileSuperadminUser(String superAdminUser) {
		this.superAdminUsername = superAdminUser;
	}
	@Value("#{ssrSettings['tenant-name']}")
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	@Value("#{ssrSettings['admin-roles']}")
	public void setAdminRoles(String roles) {
		this.adminRoles = convertLineToList(roles); 
		
	}
	@Value("#{ssrSettings['superadmin-roles']}")
	public void setSuperAdminRoles(String roles) {
		this.superadminRoles = convertLineToList(roles); 
		
	}

	@Value("#{ssrSettings['app-roles']}")
	public void setAppRoles(String roles) {
		this.appRoles = convertLineToList(roles);
		
	}
	
	private boolean isTenantExist() {
		boolean exist = false;
		
		Tenant tenant = null;
		try {
			tenant = multiTenantService.getTenantByName(this.tenantName);
			if (tenant != null) { 
				exist = true;
			} else if (tenant == null) {
				exist = false;
			} else {
				log.info(this.profileUsername + " exists");
			}
		} catch (Exception e) {
			exist = false;
		}
		return exist;
		
	}

	public String getPropertiesFile() {
		return propertiesFile;
	}

	public void setPropertiesFile(String propertiesFile) {
		this.propertiesFile = propertiesFile;
	}
	
	private List<String> convertLineToList(String list) {
		List<String> values = new ArrayList<String>();
		if (list==null || list.length() ==0) { 
			return values;
		}
		String[] arrayRoles = list.split(",");
		for (String role: arrayRoles) {
			values.add(role.trim());
		}
		return values;
	}
	

}
