package org.craftercms.security.utils.tenant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.craftercms.profile.social.utils.TenantsResolver;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link org.craftercms.profile.social.utils.TenantsResolver} that returns the first tenants resolved by any resolver.
 * If the flag {@code chainAllTenants} is set, instead all non empty tenants returned by the resolvers are returned.
 *
 * @author avasquez
 */
public class TenantsResolverChain implements TenantsResolver {

    protected List<TenantsResolver> resolvers;
    protected boolean chainAllTenants;

    @Required
    public void setResolvers(final List<TenantsResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public void setChainAllTenants(final boolean chainAllTenants) {
        this.chainAllTenants = chainAllTenants;
    }

    @Override
    public String[] getTenants() {
        List<String> finalTenants = null;

        for (TenantsResolver resolver : resolvers) {
            String[] tenants = resolver.getTenants();
            if (ArrayUtils.isNotEmpty(tenants)) {
                if (chainAllTenants) {
                    if (finalTenants == null) {
                        finalTenants = new ArrayList<>();
                    }

                    finalTenants.addAll(Arrays.asList(tenants));
                } else {
                    return tenants;
                }
            }
        }

        return finalTenants != null ? finalTenants.toArray(new String[finalTenants.size()]) : null;
    }

}
