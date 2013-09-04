package org.craftercms.profile.management.services;

import java.util.List;

import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.impl.domain.GroupRole;

public interface GroupRoleMappingService {

    GroupRole createGroupRoleMapping(GroupRole groupRole) throws AppAuthenticationFailedException;

    GroupRole updateGroupRoleMapping(GroupRole groupRole) throws AppAuthenticationFailedException;

    void deleteGroupRoleMapping(List<String> ids) throws AppAuthenticationFailedException;

    List<GroupRole> getGroupRoleMapping(String tenant) throws AppAuthenticationFailedException;

    GroupRole getGroupRoleItem(String groupId) throws AppAuthenticationFailedException;

}
