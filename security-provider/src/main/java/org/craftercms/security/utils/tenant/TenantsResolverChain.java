package org.craftercms.security.utils.tenant;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.craftercms.profile.social.utils.TenantsResolver;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link org.craftercms.profile.social.utils.TenantsResolver} that uses a chain of other resolvers to resolve the
 * tenants.
 *
 * @author avasquez
 */
public class TenantsResolverChain implements TenantsResolver {

    protected List<TenantsResolver> resolvers;

    @Required
    public void setResolvers(final List<TenantsResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public String[] getTenants() {
        for (TenantsResolver resolver : resolvers) {
            String[] tenants = resolver.getTenants();
            if (ArrayUtils.isNotEmpty(tenants)) {
                return tenants;
            }
        }

        return null;
    }

}
