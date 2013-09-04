package org.craftercms.profile.management.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.craftercms.profile.exceptions.AppAuthenticationException;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.ConflictRequestException;
import org.craftercms.profile.impl.domain.Role;
import org.craftercms.profile.impl.domain.Tenant;
import org.craftercms.profile.management.services.RoleService;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class RoleServiceImpl implements RoleService {

    @Override
    public List<Role> getAllRoles() throws AppAuthenticationFailedException {
        if (!ProfileServiceManager.isAppTokenInit()) {
            ProfileServiceManager.setAppToken();
        }
        try {
            return ProfileServiceManager.getProfileClient().getAllRoles(ProfileServiceManager.getAppToken());
        } catch (AppAuthenticationException e) {
            try {
                ProfileServiceManager.setAppToken();
            } catch (AppAuthenticationFailedException e1) {
                ProfileServiceManager.getLogger().error("could not get an AppToken", e);
            }
            return ProfileServiceManager.getProfileClient().getAllRoles(ProfileServiceManager.getAppToken());
        }
    }

    @Override
    public List<String> getTenantsByRoleName(String roleName) throws AppAuthenticationFailedException {
        if (!ProfileServiceManager.isAppTokenInit()) {
            ProfileServiceManager.setAppToken();
        }
        try {
            return convertToTenantNameCollection(ProfileServiceManager.getProfileClient().getTenantsByRoleName
                (ProfileServiceManager.getAppToken(), roleName));
        } catch (AppAuthenticationException e) {
            try {
                ProfileServiceManager.setAppToken();
            } catch (AppAuthenticationFailedException e1) {
                ProfileServiceManager.getLogger().error("could not get an AppToken", e);
            }
            return convertToTenantNameCollection(ProfileServiceManager.getProfileClient().getTenantsByRoleName
                (ProfileServiceManager.getAppToken(), roleName));
        }
    }

    @Override
    public Role createRole(Role role) throws AppAuthenticationFailedException {
        if (!ProfileServiceManager.isAppTokenInit()) {
            ProfileServiceManager.setAppToken();
        }
        try {
            return ProfileServiceManager.getProfileClient().createRole(ProfileServiceManager.getAppToken(),
                role.getRoleName());
        } catch (AppAuthenticationException e) {
            try {
                ProfileServiceManager.setAppToken();
            } catch (AppAuthenticationFailedException e1) {
                ProfileServiceManager.getLogger().error("could not get an AppToken", e);
            }
            return ProfileServiceManager.getProfileClient().createRole(ProfileServiceManager.getAppToken(),
                role.getRoleName());
        }
    }

    @Override
    public void deleteRole(List<String> ids, BindingResult bindingResult) throws AppAuthenticationFailedException {
        //errors.rejectValue("name", "grouprole.mapping.name.validation.error.empty",null,null);
        if (ids == null) {
            return;
        }
        //List<String> errorDeleting = new ArrayList<String>();
        String errorDeletingMsg = "";
        for (String roleName : ids) {
            try {
                deleteRole(roleName);
            } catch (ConflictRequestException e) {
                errorDeletingMsg = errorDeletingMsg + " " + roleName;
            }
        }
        if (!errorDeletingMsg.isEmpty()) {
            System.out.println("***** errors " + errorDeletingMsg);
            //bindingResult.reject("group.name.validation.error.used.bytenant", new String[]{errorDeletingMsg},
            // "Some roles were not deleted because are used by Tenants");
        }
    }

    @Override
    public void deleteRole(String roleName) throws AppAuthenticationFailedException {
        if (!ProfileServiceManager.isAppTokenInit()) {
            ProfileServiceManager.setAppToken();
        }
        try {
            ProfileServiceManager.getProfileClient().deleteRole(ProfileServiceManager.getAppToken(), roleName);
        } catch (AppAuthenticationException e) {
            try {
                ProfileServiceManager.setAppToken();
            } catch (AppAuthenticationFailedException e1) {
                ProfileServiceManager.getLogger().error("could not get an AppToken", e);
            }
            ProfileServiceManager.getProfileClient().deleteRole(ProfileServiceManager.getAppToken(), roleName);
        }

    }

    private List<String> convertToTenantNameCollection(List<Tenant> list) {
        List<String> result = new ArrayList<String>();
        if (list == null) {
            return null;
        }
        for (Tenant t : list) {
            result.add(t.getTenantName());
        }
        return result;
    }

}
