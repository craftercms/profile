package org.craftercms.profile.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.domain.GroupRole;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.GroupRoleException;
import org.craftercms.profile.exceptions.ProfileException;
import org.craftercms.profile.repositories.GroupRoleRepository;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.services.GroupRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default GroupRoleService implementation.
 *
 * @author Alvaro Gonzalez
 */
@Component
public class GroupRoleServiceImpl implements GroupRoleService {

    private final transient Logger log = LoggerFactory.getLogger(GroupRoleServiceImpl.class);
    @Autowired
    private GroupRoleRepository groupRepository;
    @Autowired
    private ProfileRepository profileRepository;

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.GroupRoleService#createGroupMapping(java.lang.String, java.lang.String,
     * java.util.List, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public GroupRole createGroupMapping(final String groupName, final String tenant,
                                        final List<String> roles) throws GroupRoleException {
        GroupRole newGroupMapping = new GroupRole();
        newGroupMapping.setName(groupName);
        newGroupMapping.setTenantName(tenant);
        newGroupMapping.setRoles(roles);
        try {
            groupRepository.save(newGroupMapping);
            return newGroupMapping;
        } catch (MongoDataException e) {
            log.error("Unable to save new group role ", e);
            throw new GroupRoleException("Unable to save group role", e);
        }
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.GroupRoleService#updateGroupMapping(org.bson.types.ObjectId,
     * java.lang.String, java.util.List)
     */
    @Override
    public GroupRole updateGroupMapping(final ObjectId groupId, final String tenantName,
                                        final List<String> roles) throws GroupRoleException {
        GroupRole updateGroupMapping;
        try {
            updateGroupMapping = groupRepository.findOne(groupId.toString());
        } catch (MongoDataException ex) {
            log.error("Unable to search for group role with id " + groupId.toString(), ex);
            throw new GroupRoleException("Unable to search for group role id for update");
        }
        updateGroupMapping.setTenantName(tenantName);
        updateGroupMapping.setRoles(roles);
        try {
            groupRepository.save(updateGroupMapping);
            return updateGroupMapping;
        } catch (MongoDataException ex) {
            log.error("Unable to update groupRole " + updateGroupMapping.toString(), ex);
            throw new GroupRoleException("Unable to update group role", ex);
        }
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.GroupRoleService#deleteGroupMapping(java.lang.String)
     */
    @Override
    public void deleteGroupMapping(String groupId) throws GroupRoleException {

        try {
            groupRepository.removeById(groupId);
        } catch (MongoDataException e) {
            log.error("Unable to delete groupRole by id " + groupId);
            throw new GroupRoleException("Unable to delete groupRole", e);
        }
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.GroupRoleService#getGroupRoleMapping(java.lang.String, java.lang.String,
     * java.lang.String[])
     */
    @Override
    public List<String> getGroupRoleMapping(final String profileId, final String tenantName,

                                            final String[] groups) throws GroupRoleException, ProfileException {
        Profile profile;

        profile = profileRepository.findById(new ObjectId(profileId));

        List<GroupRole> groupRoles = (List<GroupRole>)groupRepository.findByNamesAndTenantName(groups, tenantName);

        return loadRolesFromGroups(profile.getRoles(), groupRoles);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.GroupRoleService#getGroupRoleMapping(java.lang.String, java.lang.String)
     */
    @Override
    public List<String> getGroupRoleMapping(final String profileId, final String tenantName) throws GroupRoleException, ProfileException {

        Profile profile = profileRepository.findById(new ObjectId(profileId));
        List<GroupRole> groupRoles = (List<GroupRole>)groupRepository.findByTenantName(tenantName);

        return loadRolesFromGroups(profile.getRoles(), groupRoles);
    }

    private List<String> loadRolesFromGroups(final List<String> profileRoles, final List<GroupRole> groupRoles) {
        List<String> result = new ArrayList<>();
        if (groupRoles == null || profileRoles == null) {
            return new ArrayList<>();
        }
        for (GroupRole gr : groupRoles) {
            if (gr.getRoles() != null) {
                loadRolesFromGroups(profileRoles, gr.getRoles(), result);
            }
        }
        return result;
    }

    private void loadRolesFromGroups(final List<String> profileRoles, final List<String> groupRoles,
                                     final List<String> result) {
        for (String role : groupRoles) {
            if (profileRoles.contains(role) && !result.contains(role)) {
                result.add(role);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.GroupRoleService#getGroupRoleMapping(java.lang.String)
     */
    @Override
    public Iterable<GroupRole> getGroupRoleMapping(final String tenantName) throws GroupRoleException {
        return groupRepository.findByTenantName(tenantName);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.GroupRoleService#getGroupItem(java.lang.String)
     */
    @Override
    public GroupRole getGroupItem(final String groupId) throws GroupRoleException {
        try {
            return groupRepository.findById(groupId);
        } catch (MongoDataException e) {
            log.error("Unable to search tenant by id " + groupId);
            throw new GroupRoleException("Unable to search for group role ", e);
        }
    }

}
