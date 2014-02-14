package org.craftercms.profile.api;

import java.util.ArrayList;
import java.util.List;

/**
 * The attribute schema for an application and tenant pair.
 *
 * @author avasquez
 */
public class AttributeSchema {

    private String tenant;
    private List<AttributeDefinition> attributes;

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public List<AttributeDefinition> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<AttributeDefinition>();
        }

        return attributes;
    }

    public void setAttributes(List<AttributeDefinition> attributes) {
        this.attributes = attributes;
    }

}
