package org.craftercms.security.utils.tenant;

import org.craftercms.commons.http.RequestContext;

/**
 * {@link TenantsResolver} that resolves the tenants through a param.
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
        RequestContext context = RequestContext.getCurrent();
        if (context != null) {
            return context.getRequest().getParameterValues(tenantNameParam);
        } else {
            return null;
        }
    }

}
