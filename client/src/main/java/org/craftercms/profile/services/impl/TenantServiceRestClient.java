/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
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
package org.craftercms.profile.services.impl;

import org.craftercms.commons.rest.RestClientUtils;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.TenantService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.List;

import static org.craftercms.profile.api.ProfileConstants.*;

/**
 * REST client implementation of {@link org.craftercms.profile.api.services.TenantService}.
 *
 * @author avasquez
 */
public class TenantServiceRestClient extends AbstractProfileRestClientBase implements TenantService {

    public static final ParameterizedTypeReference<List<Tenant>> tenantListTypeRef =
            new ParameterizedTypeReference<List<Tenant>>() {};

    @Override
    public Tenant createTenant(Tenant tenant) throws ProfileException {
        String url = getAbsoluteUrlWithAccessTokenIdParam(BASE_URL_TENANT + URL_TENANT_CREATE);

        return doPostForObject(url, tenant, Tenant.class);
    }

    @Override
    public Tenant getTenant(String name) throws ProfileException {
        String url = getAbsoluteUrlWithAccessTokenIdParam(BASE_URL_TENANT + URL_TENANT_GET);

        return doGetForObject(url, Tenant.class, name);
    }

    @Override
    public void deleteTenant(String name) throws ProfileException {
        String url = getAbsoluteUrl(BASE_URL_TENANT + URL_TENANT_DELETE);

        doPostForLocation(url, createBaseParams(), name);
    }

    @Override
    public long getTenantCount() throws ProfileException {
        String url = getAbsoluteUrlWithAccessTokenIdParam(BASE_URL_TENANT + URL_TENANT_COUNT);

        return doGetForObject(url, Long.class);
    }

    @Override
    public List<Tenant> getAllTenants() throws ProfileException {
        String url = getAbsoluteUrlWithAccessTokenIdParam(BASE_URL_TENANT + URL_TENANT_GET_ALL);

        return doGetForObject(url, tenantListTypeRef);
    }

    @Override
    public Tenant verifyNewProfiles(String tenantName, boolean verify) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_VERIFY, verify, params);

        String url = getAbsoluteUrl(BASE_URL_TENANT + URL_TENANT_VERIFY_NEW_PROFILES);

        return doPostForObject(url, params, Tenant.class, tenantName);
    }

    @Override
    public Tenant addRoles(String tenantName, Collection<String> roles) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValues(PARAM_ROLE, roles, params);

        String url = getAbsoluteUrl(BASE_URL_TENANT + URL_TENANT_ADD_ROLES);

        return doPostForObject(url, params, Tenant.class, tenantName);
    }

    @Override
    public Tenant removeRoles(String tenantName, Collection<String> roles) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValues(PARAM_ROLE, roles, params);

        String url = getAbsoluteUrl(BASE_URL_TENANT + URL_PROFILE_REMOVE_ROLES);

        return doPostForObject(url, params, Tenant.class, tenantName);
    }

    @Override
    public Tenant addAttributeDefinitions(String tenantName, Collection<AttributeDefinition> attributeDefinitions)
            throws ProfileException {
        String url = getAbsoluteUrlWithAccessTokenIdParam(BASE_URL_TENANT + URL_TENANT_ADD_ATTRIBUTE_DEFINITIONS);

        return doPostForObject(url, attributeDefinitions, Tenant.class, tenantName);
    }

    @Override
    public Tenant updateAttributeDefinitions(String tenantName, Collection<AttributeDefinition> attributeDefinitions)
            throws ProfileException {
        String url = getAbsoluteUrlWithAccessTokenIdParam(BASE_URL_TENANT + URL_TENANT_UPDATE_ATTRIBUTE_DEFINITIONS);

        return doPostForObject(url, attributeDefinitions, Tenant.class, tenantName);
    }

    @Override
    public Tenant removeAttributeDefinitions(String tenantName, Collection<String> attributeNames)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_NAME, attributeNames, params);

        String url = getAbsoluteUrl(BASE_URL_TENANT + URL_TENANT_REMOVE_ATTRIBUTE_DEFINITIONS);

        return doPostForObject(url, params, Tenant.class, tenantName);
    }

}
