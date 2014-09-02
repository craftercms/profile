package org.craftercms.security.utils.social;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.social.utils.TenantResolver;
import org.craftercms.security.utils.SecurityUtils;

/**
 * {@link org.craftercms.profile.social.utils.TenantResolver} that resolves the current tenant to the result of
 * {@link org.craftercms.security.utils.SecurityUtils#getTenant(javax.servlet.http.HttpServletRequest)}.
 *
 * @author avasquez
 */
public class RequestTenantResolver implements TenantResolver {

    @Override
    public String getCurrentTenant() {
        return SecurityUtils.getTenant(RequestContext.getCurrent().getRequest());
    }

}
