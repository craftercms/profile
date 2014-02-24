package org.craftercms.profile.controllers.rest;

import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.profile.constants.GroupRoleConstants;
import org.craftercms.profile.domain.GroupRole;
import org.craftercms.profile.exceptions.GroupRoleException;
import org.craftercms.profile.exceptions.ProfileException;
import org.craftercms.profile.services.GroupRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/2/group/")
public class GroupMappingRestController {

    @Autowired
    private GroupRoleService groupService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ModelAttribute
    public GroupRole createGroupRoleMapping(@RequestParam(required = true) final String tenantName,
                                            @RequestParam(required = true) final String groupName,
                                            @RequestParam(value = GroupRoleConstants.ROLES) final String[] roles)
        throws GroupRoleException {
        return groupService.createGroupMapping(groupName, tenantName, Arrays.asList(roles));

    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ModelAttribute
    public GroupRole updateGroupRoleMapping(@RequestParam(required = true) final String groupId,
                                            @RequestParam(required = true) final String tenantName,
                                            @RequestParam(value = GroupRoleConstants.ROLES) final String[] roles)
        throws GroupRoleException {
        return groupService.updateGroupMapping(new ObjectId(groupId), tenantName, Arrays.asList(roles));
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteGroupRoleMapping(@RequestParam(required = true) final String groupId) throws GroupRoleException {
        groupService.deleteGroupMapping(groupId);
    }

    @RequestMapping(value = "get_item", method = RequestMethod.GET)
    @ModelAttribute
    public GroupRole getItem(@RequestParam(required = true) final String groupId) throws GroupRoleException {
        return groupService.getGroupItem(groupId);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ModelAttribute
    public List<String> getGroupRoleMapping(@RequestParam(required = true) final String profileId,
                                            @RequestParam(required = true) final String tenantName,
                                            @RequestParam(required = true, value = GroupRoleConstants.GROUPS)
    final String[] groups) throws GroupRoleException, ProfileException {
        return groupService.getGroupRoleMapping(profileId, tenantName, groups);
    }

    @RequestMapping(value = "get_all", method = RequestMethod.GET)
    @ModelAttribute
    public List<String> getGroupRoles(@RequestParam(required = true) final String profileId,
                                      @RequestParam(required = true) final String tenantName) throws GroupRoleException, ProfileException {
        return groupService.getGroupRoleMapping(profileId, tenantName);
    }

    @RequestMapping(value = "get_all_tenant", method = RequestMethod.GET)
    @ModelAttribute
    public Iterable<GroupRole> getGroupRole(@RequestParam(required = true) final String tenantName) throws GroupRoleException {
        return groupService.getGroupRoleMapping(tenantName);
    }

}
