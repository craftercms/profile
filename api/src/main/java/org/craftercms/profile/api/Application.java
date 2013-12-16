package org.craftercms.profile.api;

/**
 * Represents an application that can access profile (like Crafter Social, Crafter Engine, etc.)
 *
 * @author avasquez
 */
public class Application {

    private String name;
    private String tenant;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

}
