package org.craftercms.profile.repositories;

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.profile.domain.GroupRole;
import org.craftercms.profile.exceptions.GroupRoleException;

/**
 * Group Role Repository Services.
 */
public interface GroupRoleRepository extends CrudRepository<GroupRole> {
    /**
     * Finds al the group Roles of the given tenant.
     *
     * @param tenantName Tenant name of the GroupRoles.
     * @return The List of Group Roles the tenant. <b>null</b> if nothing is found.
     * @throws GroupRoleException if GroupRoles couldn't be search for.
     */
    Iterable<GroupRole> findByTenantName(String tenantName) throws GroupRoleException;

    /**
     * Finds all the GroupRoles with the given name of the given tenant.
     *
     * @param names      Names of the GroupRoles to look for.
     * @param tenantName Tenant name of the GroupRoles.
     * @return The List of Group Roles the tenant. <b>null</b> if nothing is found.
     * @throws GroupRoleException
     */
    Iterable<GroupRole> findByNamesAndTenantName(String[] names, String tenantName) throws GroupRoleException;


}
