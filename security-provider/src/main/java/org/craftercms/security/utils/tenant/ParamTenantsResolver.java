package org.craftercms.security.utils.tenant;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.social.utils.TenantsResolver;

/**
 * {@link org.craftercms.profile.social.utils.TenantsResolver} that resolves the tenants through a param.
 *
 * @author avasquez
 */
public class ParamTenantsResolver implements TenantsResolver {

    public static final String DEFAULT_TENANT_NAME_PARAM = "tenantName";

    protected String tenantNameParam;

    public ParamTenantsResolver() {
        tenantNameParam = DEFAULT_TENANT_NAME_PARAM;
    }

    public void setTenantNameParam(String tenantNameParam) {
        this.tenantNameParam = tenantNameParam;
    }

    @Override
    public String[] getTenants() {
        return RequestContext.getCurrent().getRequest().getParameterValues(tenantNameParam);
    }

}
