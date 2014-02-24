package org.craftercms.profile.services;

import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.profile.domain.GroupRole;
import org.craftercms.profile.exceptions.GroupRoleException;
import org.craftercms.profile.exceptions.ProfileException;

/**
 * Encapsulates the services related to group - roles feature
 *
 * @author Alvaro Gonzalez
 */
public interface GroupRoleService {
    /**
     * Creates a new group for the tenant passed as parameter and add the roles passed as parameter.
     *
     * @param groupName The new group name
     * @param tenant    The new group belongs to this tenant passed as argument
     * @param roles     List of roles that are associated with the group name
     * @return The new GroupRole instance
     */
    GroupRole createGroupMapping(String groupName, String tenant, List<String> roles) throws GroupRoleException;

    /**
     * Update the group mapping data base on the identifier passed as a parameter.
     *
     * @param groupId    the identifier of the group that is going to be updated
     * @param tenantName the tenant name
     * @param roles      List of roles associated to the group
     * @return a group-role instance
     */
    GroupRole updateGroupMapping(ObjectId groupId, String tenantName, List<String> roles) throws GroupRoleException;

    /**
     * Gets a group-role list which are roles assigned to a profile passed as argument and those roles that are in
     * one role-group as well
     *
     * @param profileId  The identifier for the profile
     * @param tenantName tenant name
     * @param groups     Groups names
     * @return a group-role list
     */
    List<String> getGroupRoleMapping(String profileId, String tenantName, String[] groups) throws GroupRoleException, ProfileException;

    /**
     * Gets a list of Group of roles belonging to the tenant name passed as parameter
     *
     * @param tenantName tenant name used to query the group of roles
     * @return The group of roles for the tenant
     */
    Iterable<GroupRole> getGroupRoleMapping(String tenantName) throws GroupRoleException;

    /**
     * Gets a group-role list which are roles assigned to a profile passed as
     * argument
     *
     * @param profileId  The identifier for the profile
     * @param tenantName tenant name
     * @return a list of group-roles
     */
    List<String> getGroupRoleMapping(String profileId, String tenantName) throws GroupRoleException, ProfileException;

    /**
     * Deletes a group-role mapping
     *
     * @param id unique identifier of the group-role that is going to be deleted.
     */
    void deleteGroupMapping(String id) throws GroupRoleException;

    /**
     * Gets a group-role instance
     *
     * @param groupId unique identifier
     * @return a group-role instance
     */
    GroupRole getGroupItem(String groupId) throws GroupRoleException;

}
