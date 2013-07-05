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

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.craftercms.profile.domain.Role;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.repositories.RoleRepository;
import org.craftercms.profile.repositories.TenantRepository;
import org.craftercms.profile.services.ProfileService;
import org.craftercms.profile.services.RoleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
public class RoleServiceImpl implements RoleService {
	
	private final transient Logger log = LoggerFactory
			.getLogger(RoleServiceImpl.class);
	
	@Autowired
	private TenantRepository tenantRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private ProfileService profileService;

	@Override
	public Role createRole(String roleName, HttpServletResponse response) {
		Role role = new Role();
		role.setRoleName(roleName);
		try {
			return roleRepository.save(role);
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
	public void deleteRole(String roleName, HttpServletResponse response) {
		List<Tenant> list = tenantRepository.getTenants(new String[]{roleName});
		if (list!=null & list.size() > 0) {
			try {
				response.sendError(HttpServletResponse.SC_CONFLICT);
			} catch(IOException e) {
				log.error(" Can't delete the role but the precondition faile was not sent to the client: " + e.getMessage());
			}
		} else {
		
			Role role = getRole(roleName);
			if (role != null) {
				roleRepository.delete(role);
			}
		}
	}
	
	@Override
	public void deleteAllRoles() {
		List<Role> roles = getAllRoles();
		for (Role r: roles) {
			roleRepository.delete(r);
		}
	}

	@Override
	public List<Role> getAllRoles() {
		
		return roleRepository.findAll();
	}

    public Role getRole(String roleName) {
		return roleRepository.findByRoleName(roleName);
	}

}
