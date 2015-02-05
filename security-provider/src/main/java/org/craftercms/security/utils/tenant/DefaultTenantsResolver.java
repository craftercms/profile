package org.craftercms.security.utils.tenant;

import org.craftercms.profile.social.utils.TenantsResolver;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link org.craftercms.profile.social.utils.TenantsResolver} that uses default tenant values.
 *
 * @author avasquez
 */
public class DefaultTenantsResolver implements TenantsResolver {

    protected String[] defaultTenantNames;

    @Required
    public void setDefaultTenantNames(String[] defaultTenantNames) {
        this.defaultTenantNames = defaultTenantNames;
    }

    @Override
    public String[] getTenants() {
        return defaultTenantNames;
    }

}
