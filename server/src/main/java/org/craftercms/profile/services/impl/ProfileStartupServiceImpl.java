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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.InvalidEmailException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.exceptions.RoleException;
import org.craftercms.profile.exceptions.TenantException;
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

    private final transient Logger log = LoggerFactory.getLogger(ProfileStartupServiceImpl.class);
    private Map<String, String> schemaAttributes = new HashMap<String, String>() {
        {
            put("last-name", "Last Name");
            put("first-name", "First Name");
        }
    };
    private String profilePassword = "admin";
    private String profileUsername = "admin";
    private String profileEmail = "adminprofile@craftercms.com";
    private String authorPassword = "author";
    private String authorUsername = "author";
    private String authorEmail = "authorprofile@craftercms.com";
    private String regularPassword = "regular";
    private String regularUsername = "regular";
    private String regularEmail = "regularprofile@craftercms.com";
    private String superAdminPassword = "superadmin";
    private String superAdminUsername = "superadmin";
    private String superAdminEmail = "superadminprofile@craftercms.com";
    private String tenantName = "craftercms";
    private List<String> adminRoles;
    private List<String> superadminRoles;
    private List<String> authorRoles;
    private List<String> regularRoles;
    @Autowired
    private MultiTenantService multiTenantService;
    @Autowired
    private SchemaService schemaService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private RoleService roleService;
    private boolean isDefaultRolesOn = true;
    private String propertiesFile;

    private List<String> appRoles;

    private List<String> tenantDefaultDomains;

    private List<String> tenantDefaultRoles;

    private boolean createBasicUser = false;

    @Override
    public void onApplicationEvent(final ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            try {
                startup();
            } catch (InvalidEmailException | CipherException | MailException | NoSuchProfileException |
                TenantException | RoleException | MongoDataException ex) {
                log.error("Unable to create validate or create basic system information, " +
                    "startup will be stop" + "until this issues are fix.", ex);
                throw new UnsupportedOperationException("Unable to create or validate basic system information", ex);

            }
        }

    }

    public void startup() throws InvalidEmailException, CipherException, MailException, NoSuchProfileException,
        TenantException, RoleException, MongoDataException {
        if (!isTenantExist()) {
            createBasicCollections(tenantName);
            createBaseProfiles(tenantName);
        }
    }

    private Tenant createBasicCollections(final String tenantName) throws TenantException, RoleException {
        Tenant tenant = this.multiTenantService.getTenantByName(tenantName);
        if (tenant == null) {
            tenant = this.multiTenantService.createTenant(tenantName, false, tenantDefaultRoles, tenantDefaultDomains);
            addAttributes(tenantName, schemaAttributes);

            if (this.isDefaultRolesOn) {
                createSystemAppRoles(tenantName);
            }
        }
        return tenant;
    }

    private void addAttributes(final String tenantName, final Map<String, String> attributes) throws TenantException {
        List<String> key = new ArrayList<String>(attributes.keySet());
        List<String> value = new ArrayList<String>(attributes.values());
        Attribute attribute;
        for (int i = 0; i < key.size(); i++) {
            attribute = new Attribute();
            attribute.setLabel(value.get(i));
            attribute.setName(key.get(i));
            attribute.setOrder((i + 1) * 100);
            attribute.setRequired(false);
            attribute.setConstraint("");
            schemaService.setAttribute(tenantName, attribute);
        }
    }


    private void createSystemAppRoles(final String tenantName) throws RoleException {
        for (String role : this.appRoles) {
            this.roleService.createRole(role);
        }
    }

    private void createBaseProfiles(final String tenantName) throws InvalidEmailException, CipherException,
        MailException, NoSuchProfileException, TenantException, MongoDataException {

        if (this.adminRoles == null || this.adminRoles.size() == 0) {
            if (this.adminRoles == null) {
                this.adminRoles = convertLineToList("");
            }
            adminRoles.add("ADMIN");
        }

        this.profileService.createProfile(profileUsername, profilePassword, true, tenantName, profileEmail, new HashMap<String, Object>(), this.adminRoles, null, null, null);
        log.info("ADMIN profile created");

        if (superAdminUsername == null) {
            return;
        }

        if (superadminRoles == null || this.superadminRoles.size() == 0) {
            if (this.superadminRoles == null) {
                this.superadminRoles = convertLineToList("");
            }
            adminRoles.add("SUPERADMIN");
        }

        this.profileService.createProfile(superAdminUsername, superAdminPassword, true, tenantName, superAdminEmail, new HashMap<String, Object>(), this.superadminRoles, null, null, null);
        log.info("SUPERADMIN profile created");

        if (createBasicUser) {
            this.authorRoles = convertLineToList("SOCIAL_AUTHOR");
            this.profileService.createProfile(authorUsername, authorPassword, true, tenantName, authorEmail, new HashMap<String, Object>(), this.authorRoles, null, null, null);
            log.info("AUTHOR profile created");

            this.regularRoles = convertLineToList("SOCIAL_USER");
            this.profileService.createProfile(regularUsername, regularPassword, true, tenantName, regularEmail, new HashMap<String, Object>(), this.regularRoles, null, null, null);
            log.info("REGULAR profile created");
        }
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

    @Value("#{ssrSettings['profile.create.basic.user']}")
    public void setCreateBasicUser(String createBasicUser) {
        if (createBasicUser != null && !createBasicUser.isEmpty()) {
            this.createBasicUser = Boolean.parseBoolean(createBasicUser);
        }

    }

    @Value("#{ssrSettings['profile.create.system.roles']}")
    public void setCreateSystemRoles(String createDefaultSystemRoles) {
        if (createDefaultSystemRoles != null && !createDefaultSystemRoles.isEmpty()) {
            isDefaultRolesOn = Boolean.parseBoolean(createDefaultSystemRoles);
        }

    }

    @Value("#{ssrSettings['profile.regular.username']}")
    public void setRegularUsername(String username) {
        if (username != null && !username.isEmpty()) {
            this.regularUsername = username;
        }
    }

    @Value("#{ssrSettings['profile.regular.password']}")
    public void setRegularPassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.regularPassword = password;
        }

    }

    @Value("#{ssrSettings['profile.author.username']}")
    public void setAuthorUsername(String username) {
        if (username != null && !username.isEmpty()) {
            this.authorUsername = username;
        }

    }

    @Value("#{ssrSettings['profile.author.password']}")
    public void setAuthorPassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.authorPassword = password;
        }

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
        if (list == null || list.length() == 0) {
            return values;
        }
        String[] arrayRoles = list.split(",");
        for (String role : arrayRoles) {
            values.add(role.trim());
        }
        return values;
    }


}
