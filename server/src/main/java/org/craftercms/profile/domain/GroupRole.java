package org.craftercms.profile.domain;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.bson.types.ObjectId;
import org.craftercms.profile.constants.GroupRoleConstants;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Field;

@XStreamAlias("group")
@XmlRootElement
@CompoundIndexes({@CompoundIndex(name = "name_tenantName_idx", def = "{'name': 1, 'tenantName': 1}", unique = true)})
public class GroupRole {

    @Field(GroupRoleConstants.FIELD_ID)
    private ObjectId id;
    @Field(GroupRoleConstants.GROUP_NAME)
    private String name;
    @Field(GroupRoleConstants.TENANT)
    private String tenantName;
    @Field(GroupRoleConstants.ROLES)
    private List<String> roles;

    private static final long serialVersionUID = 3370284215738389721L;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
