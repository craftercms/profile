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

import java.util.List;

import org.craftercms.profile.domain.Role;
import org.craftercms.profile.repositories.RoleRepository;
import org.craftercms.profile.services.ProfileService;
import org.craftercms.profile.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleServiceImpl implements RoleService {
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private ProfileService profileService;

	@Override
	public Role createRole(String roleName, String tenantName) {
		Role role = new Role();
		role.setRoleName(roleName);
		role.setTenantName(tenantName);
		return roleRepository.save(role);
	}

	@Override
	public void deleteRole(String roleName, String tenantName) {
		profileService.deleteRole(roleName, tenantName);
		
		Role role = getRole(roleName, tenantName);
		if (role != null) {
			roleRepository.delete(role);
		}
	}
	
	@Override
	public void deleteAllRoles(String tenantName) {
		List<Role> roles = getAllRoles(tenantName);
		for (Role r: roles)
			roleRepository.delete(r);
	}

	@Override
	public List<Role> getAllRoles(String tenantName) {
		
		return roleRepository.findByTenantName(tenantName);
	}

    @Override
    public List<Role> getAllRoles() {

        return roleRepository.findAll();
    }

	public Role getRole(String roleName, String tenantName) {
		return roleRepository.findByRoleNameAndTenantName(roleName, tenantName);
	}

}
