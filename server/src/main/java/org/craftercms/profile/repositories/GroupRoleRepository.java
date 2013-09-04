package org.craftercms.profile.repositories;

import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.profile.domain.GroupRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface GroupRoleRepository extends MongoRepository<GroupRole, ObjectId> {

    List<GroupRole> findByTenantName(String tenantName);

    @Query("{name:{$in:?0},tenantName:?1}")
    List<GroupRole> findByNamesAndTenantName(String[] names, String tenantName);

}
