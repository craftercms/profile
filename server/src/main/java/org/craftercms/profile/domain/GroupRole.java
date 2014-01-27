package org.craftercms.profile.domain;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.Document;
import org.jongo.marshall.jackson.oid.Id;

@XStreamAlias("group")
@XmlRootElement
@Document(collectionName = "groupRole")
public class GroupRole {
    @Id
    private ObjectId id;
    private String name;
    private String tenantName;
    private List<String> roles;

    public ObjectId getId() {
        return id;
    }

    public void setId(final ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(final String tenantName) {
        this.tenantName = tenantName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(final List<String> roles) {
        this.roles = roles;
    }

}
