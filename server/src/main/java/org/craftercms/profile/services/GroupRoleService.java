package org.craftercms.profile.services;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.craftercms.profile.domain.GroupRole;

public interface GroupRoleService {
    GroupRole createGroupMapping(String groupName, String tenant, List<String> roles, HttpServletResponse response);

    GroupRole updateGroupMapping(ObjectId groupId, String tenantName, List<String> roles);

    List<String> getGroupRoleMapping(String profileId, String tenantName, String[] groups);

    List<GroupRole> getGroupRoleMapping(String tenantName);

    List<String> getGroupRoleMapping(String profileId, String tenantName);

    void deleteGroupMapping(String id);

    GroupRole getGroupItem(String groupId);

}
