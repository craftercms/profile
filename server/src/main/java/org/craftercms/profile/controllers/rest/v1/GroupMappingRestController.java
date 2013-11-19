package org.craftercms.profile.controllers.rest.v1;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.craftercms.profile.constants.GroupRoleConstants;
import org.craftercms.profile.domain.GroupRole;
import org.craftercms.profile.services.GroupRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/1/group/")
public class GroupMappingRestController {

    @Autowired
    private GroupRoleService groupService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ModelAttribute
    public GroupRole createGroupRoleMapping(@RequestParam(required = true) String tenantName,
                                            @RequestParam(required = true) String groupName,
                                            @RequestParam(value = GroupRoleConstants.ROLES) String[] roles,
                                            HttpServletResponse response) {
        return groupService.createGroupMapping(groupName, tenantName, Arrays.asList(roles), response);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ModelAttribute
    public GroupRole updateGroupRoleMapping(@RequestParam(required = true) String groupId,
                                            @RequestParam(required = true) String tenantName,
                                            @RequestParam(value = GroupRoleConstants.ROLES) String[] roles) {
        return groupService.updateGroupMapping(new ObjectId(groupId), tenantName, Arrays.asList(roles));
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteGroupRoleMapping(@RequestParam(required = true) String groupId) {
        groupService.deleteGroupMapping(groupId);
    }

    @RequestMapping(value = "get_item", method = RequestMethod.GET)
    @ModelAttribute
    public GroupRole getItem(@RequestParam(required = true) String groupId) {
        return groupService.getGroupItem(groupId);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ModelAttribute
    public List<String> getGroupRoleMapping(@RequestParam(required = true) String profileId,
                                            @RequestParam(required = true) String tenantName,
                                            @RequestParam(required = true, value = GroupRoleConstants.GROUPS)
                                            String[] groups) {
        return groupService.getGroupRoleMapping(profileId, tenantName, groups);
    }

    @RequestMapping(value = "get_all", method = RequestMethod.GET)
    @ModelAttribute
    public List<String> getGroupRoles(@RequestParam(required = true) String profileId, @RequestParam(required = true) String tenantName) {
        return groupService.getGroupRoleMapping(profileId, tenantName);
    }

    @RequestMapping(value = "get_all_tenant", method = RequestMethod.GET)
    @ModelAttribute
    public List<GroupRole> getGroupRole(@RequestParam(required = true) String tenantName) {
        return groupService.getGroupRoleMapping(tenantName);
    }

}
