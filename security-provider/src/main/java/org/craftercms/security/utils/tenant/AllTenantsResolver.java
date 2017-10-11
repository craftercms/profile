package org.craftercms.security.utils.tenant;

import java.util.List;

import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link TenantsResolver} that uses all the available tenants.
 *
 * @author avasquez
 */
public class AllTenantsResolver implements TenantsResolver {

    private static final Logger logger = LoggerFactory.getLogger(AllTenantsResolver.class);

    protected TenantService tenantService;

    @Required
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Override
    public String[] getTenants() {
        try {
            List<String> tenants = TenantUtils.getTenantNames(tenantService);
            if (tenants != null) {
                return tenants.toArray(new String[tenants.size()]);
            } else {
                return null;
            }
        } catch (ProfileException e) {
            logger.warn("Unable to retrieve tenants", e);

            return null;
        }
    }

}
