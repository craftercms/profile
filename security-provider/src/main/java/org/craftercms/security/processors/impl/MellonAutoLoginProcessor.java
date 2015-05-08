package org.craftercms.security.processors.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationManager;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.craftercms.security.utils.tenant.TenantsResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link org.craftercms.security.processors.RequestSecurityProcessor} that auto logins a user through the headers
 * provided by the Apache mod_auth_mellon SAML authentication module. If the user doesn't exist, it creates it.
 *
 * @author avasquez
 */
public class MellonAutoLoginProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(MellonAutoLoginProcessor.class);

    public static final String HEADER_MELLON_PREFIX = "MELLON_";
    public static final String HEADER_MELLON_USERNAME = HEADER_MELLON_PREFIX + "username";
    public static final String HEADER_MELLON_EMAIL = HEADER_MELLON_PREFIX + "email";

    protected TenantService tenantService;
    protected ProfileService profileService;
    protected TenantsResolver tenantsResolver;
    protected AuthenticationManager authenticationManager;

    @Required
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Required
    public void setTenantsResolver(TenantsResolver tenantsResolver) {
        this.tenantsResolver = tenantsResolver;
    }

    @Required
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();
        String username = getUsername(request);
        String email = getEmail(request);

        if (StringUtils.isNotEmpty(username) || StringUtils.isNotEmpty(email)) {
            String[] tenantNames = tenantsResolver.getTenants();
            Tenant tenant = getSsoEnabledTenant(tenantNames);

            if (tenant != null) {
                if (StringUtils.isEmpty(username)) {
                    username = email;
                }

                Profile profile = profileService.getProfileByUsername(tenant.getName(), username);
                if (profile == null) {
                    profile = createProfileWithSsoInfo(username, email, tenant, request);
                }

                Authentication auth = authenticationManager.authenticateUser(profile);
                SecurityUtils.setAuthentication(request, auth);
            } else {
                logger.warn("An SSO login was attempted, but none of the tenants {} is enabled for SSO", tenantNames);
            }
        }

        processorChain.processRequest(context);
    }

    protected String getUsername(HttpServletRequest request) {
        return request.getHeader(HEADER_MELLON_USERNAME);
    }

    protected String getEmail(HttpServletRequest request) {
        return request.getHeader(HEADER_MELLON_EMAIL);
    }

    protected Tenant getSsoEnabledTenant(String[] tenantNames) throws ProfileException {
        for (String tenantName : tenantNames) {
            Tenant tenant = tenantService.getTenant(tenantName);
            if (tenant != null && tenant.isSsoEnabled()) {
                return tenant;
            }
        }

        return null;
    }

    protected Profile createProfileWithSsoInfo(String username, String email, Tenant tenant,
                                               HttpServletRequest request) throws ProfileException {
        Map<String, Object> attributes = null;
        Set<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();

        for (AttributeDefinition attributeDefinition : attributeDefinitions) {
            String attributeName = attributeDefinition.getName();
            String attributeValue = request.getHeader(HEADER_MELLON_PREFIX + attributeName);

            if (StringUtils.isNotEmpty(attributeValue)) {
                if (attributes == null) {
                    attributes = new HashMap<>();
                }

                attributes.put(attributeName, attributeValue);
            }
        }

        logger.info("Creating new profile from SSO info: username={}, email={}, attributes={}", username, email,
                    attributes);

        return profileService.createProfile(tenant.getName(), username, null, email, true, null, attributes, null);
    }

}
