package org.craftercms.profile.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.craftercms.profile.domain.GroupRole;
import org.craftercms.profile.domain.Profile;

import org.craftercms.profile.repositories.GroupRoleRepository;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.services.GroupRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
public class GroupRoleServiceImpl implements GroupRoleService {
	
	private final transient Logger log = LoggerFactory
			.getLogger(GroupRoleServiceImpl.class);
	
	@Autowired
	private GroupRoleRepository groupRepository;
	
	@Autowired
	private ProfileRepository profileRepository;

	@Override
	public GroupRole createGroupMapping(String groupName, String tenant,
			List<String> roles, HttpServletResponse response) {
		GroupRole newGroupMapping = new GroupRole();
		newGroupMapping.setName(groupName);
		newGroupMapping.setTenantName(tenant);
		newGroupMapping.setRoles(roles);
		try {
			return groupRepository.save(newGroupMapping);
		} catch (DuplicateKeyException e) {
			try {
				if (response!=null) {
					response.sendError(HttpServletResponse.SC_CONFLICT);
				}
			} catch(IOException e1) {
				log.error("Can't set error status after a DuplicateKey exception was received.");
			}
		}
		return null;
		
	}

	@Override
	public GroupRole updateGroupMapping(ObjectId groupId,
			String tenantName, List<String> roles) {
		GroupRole updateGroupMapping = groupRepository.findOne(groupId);
		updateGroupMapping.setTenantName(tenantName);
		updateGroupMapping.setRoles(roles);
		groupRepository.save(updateGroupMapping);
		return updateGroupMapping;
	}
	
	@Override
	public void deleteGroupMapping(String groupId) {
		groupRepository.delete(new ObjectId(groupId));
	}

	@Override
	public List<String> getGroupRoleMapping(String profileId, String tenantName, String[] groups) {

		Profile profile = profileRepository.findOne(new ObjectId(profileId));
		List<GroupRole> groupRoles = groupRepository.findByNamesAndTenantName(groups, tenantName);

		return loadRolesFromGroups(profile.getRoles(), groupRoles);
	}
	
	@Override
	public List<String> getGroupRoleMapping(String profileId, String tenantName) {

		Profile profile = profileRepository.findOne(new ObjectId(profileId));
		List<GroupRole> groupRoles = groupRepository.findByTenantName(tenantName);

		return loadRolesFromGroups(profile.getRoles(), groupRoles);
	}
	
	private List<String> loadRolesFromGroups(List<String> profileRoles, List<GroupRole> groupRoles) {
		List<String> result = new ArrayList<String>();
		if (groupRoles == null || profileRoles == null) {
			return new ArrayList<String>(); 
		}
		for (GroupRole gr: groupRoles) {
			if (gr.getRoles()!=null) {
				loadRolesFromGroups(profileRoles, gr.getRoles(), result);
			}
		}
		return result;
	}
	
	private void loadRolesFromGroups(List<String> profileRoles, List<String> groupRoles, List<String> result) {
		for (String role: groupRoles) {
			if (profileRoles.contains(role) && !result.contains(role)) {
				result.add(role);
			} 
		}
	}

	@Override
	public List<GroupRole> getGroupRoleMapping(String tenantName) {
		return groupRepository.findByTenantName(tenantName);
	}

	@Override
	public GroupRole getGroupItem(String groupId) {
		return groupRepository.findOne(new ObjectId(groupId));
	}

}
