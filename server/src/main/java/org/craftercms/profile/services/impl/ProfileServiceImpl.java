/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.profile.services.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.domain.Ticket;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.repositories.TicketRepository;
import org.craftercms.profile.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private TicketRepository ticketRepository;
	@Override
	public Profile createProfile(String userName, String password, Boolean active, String tenantName, Map<String, Serializable> attributes, List<String> roles) {
	    PasswordEncoder encoder = new Md5PasswordEncoder();
	    String hashedPassword = encoder.encodePassword(password, null);

		Profile profile = new Profile();
		profile.setUserName(userName);
		profile.setPassword(hashedPassword);
		profile.setActive(active);
		profile.setTenantName(tenantName);
		profile.setCreated(new Date());
		profile.setModified(new Date());
		profile.setAttributes(attributes);
		profile.setRoles(roles);
		
		return profileRepository.save(profile);
	}

	@Override
	public List<Profile> getProfileRange(String tenantName, String sortBy, String sortOrder, List<String> attributesList, int start, int end) {
		return profileRepository.getProfileRange(tenantName, sortBy, sortOrder, attributesList, start, end);
	}

	@Override
	public long getProfilesCount(String tenantName) {
		return profileRepository.getProfilesCount(tenantName);
	}

	@Override
	public Profile updateProfile(String profileId, String userName, String password, Boolean active, String tenantName, 
				Map<String, Serializable> attributes, List<String> roles) {
		Profile profile = profileRepository.findOne(new ObjectId(profileId));

		if (profile == null) {
			return profile;
		}
		if (userName != null && !userName.trim().isEmpty()) {
			profile.setUserName(userName);
		}

		if (password != null && !password.trim().isEmpty()) {
		    PasswordEncoder encoder = new Md5PasswordEncoder();
	    	String hashedPassword = encoder.encodePassword(password, null);
			profile.setPassword(hashedPassword);
		}

		if (active != null) {
			profile.setActive(active);
		}
		
		if (tenantName != null && !tenantName.trim().isEmpty()) { 
			profile.setTenantName(tenantName);
		}
		
		if (roles != null) {
			profile.setRoles(roles);
		}
		
		Map<String, Serializable> currentAttributes = profile.getAttributes();
		if (currentAttributes != null) {
			currentAttributes.putAll(attributes);
		} else {
			currentAttributes = attributes;
		}

		profile.setAttributes(currentAttributes);
		profile.setModified(new Date());
		profile = profileRepository.save(profile);
		return profile;
	}

	@Override
	public Profile getProfileByTicket(String ticketStr) {
		Ticket ticket = ticketRepository.getByTicket(ticketStr);
		if (ticket==null) {
			return null;
		}
		return getProfileByUserName(ticket.getUsername(), ticket.getTenantName(), null);
	}

	@Override
	public Profile getProfileByTicket(String ticketStr, List<String> attributes) {
		Ticket ticket = ticketRepository.getByTicket(ticketStr);
		if (ticket==null) {
			return null;
		}
		return getProfileByUserName(ticket.getUsername(), ticket.getTenantName(), attributes);
	}

	@Override
	public Profile getProfileByTicketWithAllAttributes(String ticketString) {
		Ticket ticket = ticketRepository.getByTicket(ticketString);
		if (ticket==null) {
			return null;
		}
		return getProfileByUserNameWithAllAttributes(ticket.getUsername(), ticket.getTenantName());
	}

	@Override
	public Profile getProfile(String profileId) {
		return profileRepository.getProfile(profileId);
	}

	@Override
	public Profile getProfile(String profileId, List<String> attributes) {
		return profileRepository.getProfile(profileId, attributes);
	}

	@Override
	public Profile getProfileWithAllAttributes(String profileId) {
		return profileRepository.findOne(new ObjectId(profileId));
	}

	@Override
	public Profile getProfileByUserName(String userName, String tenantName) {
		return profileRepository.getProfileByUserName(userName, tenantName);
	}

	@Override
	public Profile getProfileByUserName(String userName, String tenantName, List<String> attributes) {
		return profileRepository.getProfileByUserName(userName, tenantName, attributes);
	}

	@Override
	public Profile getProfileByUserNameWithAllAttributes(String userName, String tenantName) {
		return profileRepository.getProfileByUserNameWithAllAttributes(userName, tenantName);
	}

	@Override
	public List<Profile> getProfiles(List<String> profileIdList) {
		return profileRepository.getProfiles(profileIdList);
	}

	@Override
	public List<Profile> getProfilesWithAttributes(List<String> profileIdList) {
		return profileRepository.getProfilesWithAttributes(profileIdList);
	}

	@Override
	public void deleteProfile(String profileId) {
		profileRepository.delete(new ObjectId(profileId));
	}
	
	@Override
	public void deleteProfiles() {
		profileRepository.deleteAll();
	}
	
	@Override
	public void deleteProfiles(String tenantName) {
		profileRepository.delete(getProfilesByTenant(tenantName));
	}
	
	public List<Profile> getProfilesByRoleName(String roleName, String tenantName) {
		return profileRepository.findByRolesAndTenantName(roleName, tenantName);
	}
	
	private List<Profile> getProfilesByTenant(String tenantName) {
		return profileRepository.getProfilesByTenantName(tenantName);
	}

	@Override
	public void setAttributes(String profileId, Map<String, Serializable> attributes) {
		profileRepository.setAttributes(profileId, attributes);
	}

	@Override
	public Map<String, Serializable> getAllAttributes(String profileId) {
		return profileRepository.getAllAttributes(profileId);
	}

	@Override
	public Map<String, Serializable> getAttributes(String profileId, List<String> attributes) {
		return profileRepository.getAttributes(profileId, attributes);
	}

	@Override
	public Map<String, Serializable> getAttribute(String profileId, String attributeKey) {
		return profileRepository.getAttribute(profileId, attributeKey);
	}

	@Override
	public void deleteAllAttributes(String profileId) {
		profileRepository.deleteAllAttributes(profileId);
	}

	@Override
	public void deleteAttributes(String profileId, List<String> attributes) {
		profileRepository.deleteAttributes(profileId, attributes);
	}
	
	@Override
	public void deleteRole(String roleName, String tenantName) {
		List<Profile> profiles = getProfilesByRoleName(roleName, tenantName);
		for (Profile p: profiles) {
			profileRepository.deleteRole(p.getId().toString(), roleName);
		}
	}
}