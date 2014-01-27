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
package org.craftercms.profile.repositories;

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.profile.domain.Role;
import org.craftercms.profile.exceptions.RoleException;
import org.springframework.stereotype.Repository;

/**
 * Definition of Role Repositories.
 */
@Repository("roleRepository")
public interface RoleRepository extends CrudRepository<Role> {
    /**
     * Gets a Role by its name.
     *
     * @param roleName Name of the Role.
     * @return The Role with the given name.<b>Null if not found</b>.
     * @throws org.craftercms.profile.exceptions.RoleException if a error is raise while searching.
     */
    Role findByRoleName(final String roleName) throws RoleException;

    /**
     * <b>Removes all the Roles in the collections</b><br/>
     * Think twice before calling this.
     */
    void removeAll() throws RoleException;
}