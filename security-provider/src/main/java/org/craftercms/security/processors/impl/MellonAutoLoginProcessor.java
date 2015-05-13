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

    public static final String DEFAULT_MELLON_HEADER_PREFIX = "MELLON_";
    public static final String DEFAULT_USERNAME_HEADER_NAME = DEFAULT_MELLON_HEADER_PREFIX + "username";
    public static final String DEFAULT_EMAIL_HEADER_NAME = DEFAULT_MELLON_HEADER_PREFIX + "email";

    protected TenantService tenantService;
    protected ProfileService profileService;
    protected TenantsResolver tenantsResolver;
    protected AuthenticationManager authenticationManager;
    protected String mellonHeaderPrefix;
    protected String usernameHeaderName;
    protected String emailHeaderName;

    public MellonAutoLoginProcessor() {
        mellonHeaderPrefix = DEFAULT_MELLON_HEADER_PREFIX;
        usernameHeaderName = DEFAULT_USERNAME_HEADER_NAME;
        emailHeaderName = DEFAULT_EMAIL_HEADER_NAME;
    }

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

    public void setMellonHeaderPrefix(String mellonHeaderPrefix) {
        this.mellonHeaderPrefix = mellonHeaderPrefix;
    }

    public void setUsernameHeaderName(String usernameHeaderName) {
        this.usernameHeaderName = usernameHeaderName;
    }

    public void setEmailHeaderName(String emailHeaderName) {
        this.emailHeaderName = emailHeaderName;
    }

    @Override
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();
        String username = request.getHeader(usernameHeaderName);
        Authentication auth = SecurityUtils.getAuthentication(request);

        if (StringUtils.isNotEmpty(username) && (auth == null || !auth.getProfile().getUsername().equals(username))) {
            String[] tenantNames = tenantsResolver.getTenants();
            Tenant tenant = getSsoEnabledTenant(tenantNames);

            if (tenant != null) {
                Profile profile = profileService.getProfileByUsername(tenant.getName(), username);
                if (profile == null) {
                    profile = createProfileWithSsoInfo(username, tenant, request);
                }

                SecurityUtils.setAuthentication(request, authenticationManager.authenticateUser(profile));
            } else {
                logger.warn("An SSO login was attempted, but none of the tenants {} is enabled for SSO", tenantNames);
            }
        }

        processorChain.processRequest(context);
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

    protected Profile createProfileWithSsoInfo(String username, Tenant tenant,
                                               HttpServletRequest request) throws ProfileException {
        Map<String, Object> attributes = null;
        Set<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();

        String email = request.getHeader(emailHeaderName);

        for (AttributeDefinition attributeDefinition : attributeDefinitions) {
            String attributeName = attributeDefinition.getName();
            String attributeValue = request.getHeader(mellonHeaderPrefix + attributeName);

            if (StringUtils.isNotEmpty(attributeValue)) {
                if (attributes == null) {
                    attributes = new HashMap<>();
                }

                attributes.put(attributeName, attributeValue);
            }
        }

        logger.info("Creating new profile from SSO info: username={}, email={}, tenant={}, attributes={}", username,
                    email, tenant.getName(), attributes);

        return profileService.createProfile(tenant.getName(), username, null, email, true, null, attributes, null);
    }

}
