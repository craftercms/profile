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


import com.mongodb.MongoException;
import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.domain.Role;
import org.craftercms.profile.exceptions.RoleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Definition of Role Repository Services.
 */
public class RoleRepositoryImpl extends JongoRepository<Role> implements RoleRepository {
    /**
     * Find Role by name Query.
     */
    public static final String PROFILE_ROLE_BY_NAME = "profile.role.byName";
    /**
     * le logger.
     */
    private Logger log = LoggerFactory.getLogger(RoleRepositoryImpl.class);

    /**
     * Default Ctr.
     *
     * @throws MongoDataException, If parent couldn't get information of the Role class.
     */
    public RoleRepositoryImpl() throws MongoDataException {

    }

    @Override
    public Role findByRoleName(final String roleName) throws RoleException {
        log.debug("Finding Role named {}", roleName);
        try {
            String query = getQueryFor(PROFILE_ROLE_BY_NAME);
            Role role = findOne(query, roleName);
            log.debug("Role found {}", role);
            return role;
        } catch (MongoDataException ex) {
            log.error("Unable to find role with name " + roleName, ex);
            throw new RoleException("Unable to find role by name", ex);
        }
    }

    @Override
    public void removeAll() throws RoleException {
        log.info("About to delete all Roles !!!");
        try {
            getCollection().remove();
        } catch (MongoException ex) {
            log.error("Unable to delete all roles", ex);
            throw new RoleException("Unable to delete all roles", ex);
        }
    }
}
