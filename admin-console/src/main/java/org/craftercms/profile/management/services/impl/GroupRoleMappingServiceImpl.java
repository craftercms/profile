package org.craftercms.profile.management.services.impl;

import java.util.List;

import org.craftercms.profile.client.exceptions.AppAuthenticationException;
import org.craftercms.profile.client.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.client.impl.domain.GroupRole;
import org.craftercms.profile.management.services.GroupRoleMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GroupRoleMappingServiceImpl implements GroupRoleMappingService {

    private static final Logger log = LoggerFactory.getLogger(GroupRoleMappingServiceImpl.class);

    @Override
    public GroupRole createGroupRoleMapping(GroupRole groupRole) throws AppAuthenticationFailedException {
        if (!ProfileServiceManager.isAppTokenInit()) {
            ProfileServiceManager.setAppToken();
        }
        try {
            return ProfileServiceManager.getProfileClient().createGroupRoleMapping(ProfileServiceManager.getAppToken
                (), groupRole.getTenantName(), groupRole.getName(), groupRole.getRoles());
        } catch (AppAuthenticationException e) {
            try {
                ProfileServiceManager.setAppToken();
            } catch (AppAuthenticationFailedException e1) {
                log.error("could not get an AppToken", e);
            }
            return ProfileServiceManager.getProfileClient().createGroupRoleMapping(ProfileServiceManager.getAppToken
                (), groupRole.getTenantName(), groupRole.getName(), groupRole.getRoles());
        }
    }

    @Override
    public GroupRole updateGroupRoleMapping(GroupRole groupRole) throws AppAuthenticationFailedException {
        if (!ProfileServiceManager.isAppTokenInit()) {
            ProfileServiceManager.setAppToken();
        }
        try {
            return ProfileServiceManager.getProfileClient().updateGroupRoleMapping(ProfileServiceManager.getAppToken
                (), groupRole.getTenantName(), groupRole.getId(), groupRole.getRoles());
        } catch (AppAuthenticationException e) {
            try {
                ProfileServiceManager.setAppToken();
            } catch (AppAuthenticationFailedException e1) {
                log.error("could not get an AppToken", e);
            }
            return ProfileServiceManager.getProfileClient().updateGroupRoleMapping(ProfileServiceManager.getAppToken
                (), groupRole.getTenantName(), groupRole.getId(), groupRole.getRoles());
        }
    }

    @Override
    public List<GroupRole> getGroupRoleMapping(String tenant) throws AppAuthenticationFailedException {
        if (!ProfileServiceManager.isAppTokenInit()) {
            ProfileServiceManager.setAppToken();
        }
        try {
            return ProfileServiceManager.getProfileClient().getGroupRoleMappingByTenant(ProfileServiceManager
                .getAppToken(), tenant);
        } catch (AppAuthenticationException e) {
            try {
                ProfileServiceManager.setAppToken();
            } catch (AppAuthenticationFailedException e1) {
                log.error("could not get an AppToken", e);
            }
            return ProfileServiceManager.getProfileClient().getGroupRoleMappingByTenant(ProfileServiceManager
                .getAppToken(), tenant);
        }

    }

    @Override
    public GroupRole getGroupRoleItem(String groupId) throws AppAuthenticationFailedException {
        if (!ProfileServiceManager.isAppTokenInit()) {
            ProfileServiceManager.setAppToken();
        }
        try {
            return ProfileServiceManager.getProfileClient().getGroupRoleMapping(ProfileServiceManager.getAppToken(),
                groupId);
        } catch (AppAuthenticationException e) {
            try {
                ProfileServiceManager.setAppToken();
            } catch (AppAuthenticationFailedException e1) {
                log.error("could not get an AppToken", e);
            }
            return ProfileServiceManager.getProfileClient().getGroupRoleMapping(ProfileServiceManager.getAppToken(),
                groupId);
        }

    }

    @Override
    public void deleteGroupRoleMapping(List<String> ids) throws AppAuthenticationFailedException {
        if (ids == null) {
            return;
        }
        for (String id : ids) {
            deleteGroupRoleMapping(id);
        }

    }

    public void deleteGroupRoleMapping(String id) throws AppAuthenticationFailedException {

        if (!ProfileServiceManager.isAppTokenInit()) {
            ProfileServiceManager.setAppToken();
        }
        try {
            ProfileServiceManager.getProfileClient().deleteGroupRoleMapping(ProfileServiceManager.getAppToken(), id);
        } catch (AppAuthenticationException e) {
            try {
                ProfileServiceManager.setAppToken();
            } catch (AppAuthenticationFailedException e1) {
                log.error("could not get an AppToken", e);
            }
            ProfileServiceManager.getProfileClient().deleteGroupRoleMapping(ProfileServiceManager.getAppToken(), id);
        }
    }

}
