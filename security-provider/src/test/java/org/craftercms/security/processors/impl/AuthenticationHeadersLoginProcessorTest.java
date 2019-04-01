/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.security.processors.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationManager;
import org.craftercms.security.authentication.impl.DefaultAuthentication;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.craftercms.security.utils.tenant.TenantsResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuthenticationHeadersLoginProcessor}.
 *
 * @author avasquez
 */
public class AuthenticationHeadersLoginProcessorTest {

    private static final String TENANT_NAME = "default";

    private static final String FIRST_NAME_ATTRIB_NAME = "firstName";
    private static final String LAST_NAME_ATTRIB_NAME = "lastName";

    private static final String TOKEN = "TOP_SECRET_TOKEN";
    private static final ObjectId PROFILE_ID = ObjectId.get();
    private static final String USERNAME = "jdoe";
    private static final String EMAIL = "john.doe@example.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String TICKET = UUID.randomUUID().toString();

    private AuthenticationHeadersLoginProcessor processor;
    @Mock
    private TenantService tenantService;
    @Mock
    private ProfileService profileService;
    @Mock
    private TenantsResolver tenantsResolver;
    @Mock
    private AuthenticationManager authenticationManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Profile profile = getProfile();

        when(tenantService.getTenant(TENANT_NAME)).thenReturn(getTenant());
        when(profileService.createProfile(TENANT_NAME, USERNAME, null, EMAIL, true, null, getAttributes(), null))
            .thenReturn(profile);
        when(tenantsResolver.getTenants()).thenReturn(new String[] {TENANT_NAME});
        when(authenticationManager.authenticateUser(profile)).thenReturn(new DefaultAuthentication(TICKET, profile));

        processor = new AuthenticationHeadersLoginProcessor();
        processor.setTokenExpectedValue(TOKEN);
        processor.setTenantService(tenantService);
        processor.setProfileService(profileService);
        processor.setTenantsResolver(tenantsResolver);
        processor.setAuthenticationManager(authenticationManager);
    }

    @Test
    public void testProcess() throws Exception {
        RequestContext context = getRequestContext();
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);

        processor.processRequest(context, chain);

        Authentication auth = SecurityUtils.getAuthentication(context.getRequest());

        assertNotNull(auth);
        assertEquals(TICKET, auth.getTicket());
        assertEquals(PROFILE_ID, auth.getProfile().getId());
        assertEquals(USERNAME, auth.getProfile().getUsername());
        assertEquals(EMAIL, auth.getProfile().getEmail());
        assertTrue(auth.getProfile().isEnabled());
        assertEquals(TENANT_NAME, auth.getProfile().getTenant());
        assertEquals(getAttributes(), auth.getProfile().getAttributes());
    }

    private Tenant getTenant() {
        AttributeDefinition firstNameDef = new AttributeDefinition(FIRST_NAME_ATTRIB_NAME);
        AttributeDefinition lastNameDef = new AttributeDefinition(LAST_NAME_ATTRIB_NAME);

        Tenant tenant = new Tenant();
        tenant.setName(TENANT_NAME);
        tenant.setSsoEnabled(true);
        tenant.getAttributeDefinitions().add(firstNameDef);
        tenant.getAttributeDefinitions().add(lastNameDef);

        return tenant;
    }

    private Profile getProfile() {
        Profile profile = new Profile();
        profile.setId(PROFILE_ID);
        profile.setUsername(USERNAME);
        profile.setEmail(EMAIL);
        profile.setEnabled(true);
        profile.setTenant(TENANT_NAME);
        profile.setAttributes(getAttributes());

        return profile;
    }

    private Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(FIRST_NAME_ATTRIB_NAME, FIRST_NAME);
        attributes.put(LAST_NAME_ATTRIB_NAME, LAST_NAME);

        return attributes;
    }

    private RequestContext getRequestContext() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader(AuthenticationHeadersLoginProcessor.DEFAULT_TOKEN_HEADER_NAME, TOKEN);
        request.addHeader(AuthenticationHeadersLoginProcessor.DEFAULT_USERNAME_HEADER_NAME, USERNAME);
        request.addHeader(AuthenticationHeadersLoginProcessor.DEFAULT_EMAIL_HEADER_NAME, EMAIL);
        request.addHeader(AuthenticationHeadersLoginProcessor.DEFAULT_MELLON_HEADER_PREFIX + FIRST_NAME_ATTRIB_NAME, FIRST_NAME);
        request.addHeader(AuthenticationHeadersLoginProcessor.DEFAULT_MELLON_HEADER_PREFIX + LAST_NAME_ATTRIB_NAME, LAST_NAME);

        return new RequestContext(request, response, null);
    }

}
