package org.craftercms.profile.repositories;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.domain.GroupRole;
import org.craftercms.profile.exceptions.GroupRoleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Impl of GroupRoleRepository.
 */
public class GroupRoleRepositoryImpl extends JongoRepository<GroupRole> implements GroupRoleRepository {
    /**
     * Find by Role name and Tenant Name.
     */
    public static final String PROFILE_GROUPROLES_BY_NAME_AND_TENANT = "profile.grouproles.byNameAndTenantName";
    public static final String PROFILE_GROUPROLES_BY_TENANT_NAME = "profile.grouproles.byTenantName";
    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(GroupRoleRepositoryImpl.class);

    /**
     * Default Ctr.
     *
     * @throws MongoDataException, If parent couldn't get information of the Role class.
     */
    public GroupRoleRepositoryImpl() throws MongoDataException {
    }

    @Override
    public Iterable<GroupRole> findByTenantName(final String tenantName) throws GroupRoleException {
        try {
            log.debug("Searching for RoleGroups of tenant {}", tenantName);
            String query = getQueryFor(PROFILE_GROUPROLES_BY_TENANT_NAME);
            return find(query, tenantName);
        } catch (MongoDataException ex) {
            log.error("Unable to find GroupRoles tenantName " + tenantName, ex);
            throw new GroupRoleException("Unable to find GroupRoles by name and tenants", ex);
        }
    }

    @Override
    public Iterable<GroupRole> findByNamesAndTenantName(final String[] names, final String tenantName) throws
        GroupRoleException {
        try {
            log.debug("Searching for RoleGroups with names {} of tenant {}", names, tenantName);
            String query = getQueryFor(PROFILE_GROUPROLES_BY_NAME_AND_TENANT);
            return find(query, names, tenantName);
        } catch (MongoDataException ex) {
            log.error("Unable to find GroupRoles by name " + StringUtils.join(names) + " and tenant " + tenantName, ex);
            throw new GroupRoleException("Unable to find GroupRoles by name and tenants", ex);
        }
    }
}
