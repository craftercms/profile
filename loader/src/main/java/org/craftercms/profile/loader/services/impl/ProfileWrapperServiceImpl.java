package org.craftercms.profile.loader.services.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.domain.Role;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.InvalidEmailException;
import org.craftercms.profile.loader.services.ProfileWrapperService;
import org.craftercms.profile.services.MultiTenantService;
import org.craftercms.profile.services.ProfileService;
import org.craftercms.profile.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * @author Sandra O'Keeffe
 * Date: 9/4/13
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ProfileWrapperServiceImpl implements ProfileWrapperService {


    // roles appenders
    private List<String> tenantRoles;
    private List<String> tenantDomains;

    // this will generate approx. 20% inactive profiles
    private boolean[] activeArray = {true, true, true, true, true, true, true, true, false, false};

    private ProfileService profileService;
    private MultiTenantService multiTenantService;
    private RoleService roleService;

    // assign all the same emails
    private String testEmail;

    @Override
    public Profile createProfile(String tenant) throws AppAuthenticationFailedException {
        // randomly generate profile details & then use profile client to create


        String username = RandomStringUtils.randomAlphabetic(randomFromInterval(8, 12));
        String password = RandomStringUtils.randomAscii(randomFromInterval(8, 20));

        Profile createdProfile = null;
        try {
            createdProfile =  profileService.createProfile(username, password, activeArray[randomFromInterval(1, 10)-1], tenant, testEmail, null, null, null);
        } catch (InvalidEmailException e) {
            // Don't really care about this, continue to try create profiles
            System.out.println(e);
        }
        return createdProfile;
    }


    @Override
    public Tenant createTenant(String tenant) throws AppAuthenticationFailedException {

         List<String> roles = new ArrayList<String>();
        for (String tenantRole : tenantRoles) {
            roles.add(tenant + tenantRole);
            // create role
            createRole(tenant + tenantRole);

        }
        return  multiTenantService.createTenant(tenant, true, roles, tenantDomains, null);

    }

    @Override
    public Role createRole(String roleName) throws AppAuthenticationFailedException {
        // randomly generate profile details & then use profile client to create
        return roleService.createRole(roleName, null);

    }

    public int randomFromInterval(int from, int to) {
        return (int) Math.floor(Math.random()*(to-from+1)+from);
    }


    @Value("${crafter.profile.loader.email}")
    public void setTestEmail(String testEmail) {
        this.testEmail = testEmail;
    }

    @Value("${crafter.profile.loader.tenant.domains}")
    public void setTenantDomains(String tenantDomains) {
        this.tenantDomains = Arrays.asList(tenantDomains.split(","));
    }

    @Value("${crafter.profile.loader.tenant.roles.appenders}")
    public void setTenantRoles(String tenantRoles) {
        this.tenantRoles = Arrays.asList(tenantRoles.split(","));
    }

    @Autowired
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setMultiTenantService(MultiTenantService multiTenantService) {
        this.multiTenantService = multiTenantService;
    }
}

