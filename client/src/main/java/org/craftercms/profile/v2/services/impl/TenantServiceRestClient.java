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
package org.craftercms.profile.v2.services.impl;

import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.v2.utils.rest.RestClientUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Set;

import static org.craftercms.profile.api.RestConstants.*;

/**
 * REST client implementation of {@link org.craftercms.profile.api.services.TenantService}.
 *
 * @author avasquez
 */
public class TenantServiceRestClient implements TenantService {

    public static final String DEFAULT_EXTENSION = ".json";

    protected String extension;
    protected RestTemplate restTemplate;

    public TenantServiceRestClient() {
        this.extension = DEFAULT_EXTENSION;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Required
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Tenant createTenant(String name, boolean verifyNewProfiles, Set<String> roles) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_TENANT_NAME, name, params);
        RestClientUtils.addValue(PARAM_VERIFY_NEW_PROFILES, verifyNewProfiles, params);
        RestClientUtils.addValues(PARAM_ROLE, roles, params);

        String url = BASE_URL_TENANT + URL_TENANT_CREATE + extension;

        return restTemplate.postForObject(url, params, Tenant.class);
    }

    @Override
    public Tenant getTenant(String name) throws ProfileException {
        String url = BASE_URL_TENANT + URL_TENANT_GET + extension;

        return restTemplate.getForObject(url, Tenant.class, name);
    }

    @Override
    public Tenant updateTenant(Tenant tenant) throws ProfileException {
        String url = BASE_URL_TENANT + URL_TENANT_UPDATE + extension;

        return restTemplate.postForObject(url, tenant, Tenant.class);
    }

    @Override
    public void deleteTenant(String name) throws ProfileException {
        String url = BASE_URL_TENANT + URL_TENANT_DELETE + extension;

        restTemplate.postForLocation(url, null, name);
    }

    @Override
    public long getTenantCount() throws ProfileException {
        String url = BASE_URL_TENANT + URL_TENANT_COUNT + extension;

        return restTemplate.getForObject(url, Long.class);
    }

    @Override
    public Iterable<Tenant> getAllTenants() throws ProfileException {
        String url = BASE_URL_TENANT + URL_TENANT_GET_ALL + extension;

        return restTemplate.getForObject(url, Iterable.class);
    }

    @Override
    public Tenant verifyNewProfiles(String tenantName, boolean verify) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_VERIFY, verify, params);

        String url = BASE_URL_TENANT + URL_TENANT_VERIFY_NEW_PROFILES + extension;

        return restTemplate.postForObject(url, params, Tenant.class, tenantName);
    }

    @Override
    public Tenant addRoles(String tenantName, Collection<String> roles) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValues(PARAM_ROLE, roles, params);

        String url = BASE_URL_TENANT + URL_TENANT_ADD_ROLES + extension;

        return restTemplate.postForObject(url, params, Tenant.class, tenantName);
    }

    @Override
    public Tenant removeRoles(String tenantName, Collection<String> roles) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValues(PARAM_ROLE, roles, params);

        String url = BASE_URL_TENANT + URL_PROFILE_REMOVE_ROLES + extension;

        return restTemplate.postForObject(url, params, Tenant.class, tenantName);
    }

    @Override
    public Tenant addAttributeDefinitions(String tenantName, Collection<AttributeDefinition> attributeDefinitions)
            throws ProfileException {
        String url = BASE_URL_TENANT + URL_TENANT_ADD_ATTRIBUTE_DEFINITIONS + extension;

        return restTemplate.postForObject(url, attributeDefinitions, Tenant.class, tenantName);
    }

    @Override
    public Tenant removeAttributeDefinitions(String tenantName, Collection<String> attributeNames)
            throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_NAME, attributeNames, params);

        String url = BASE_URL_TENANT + URL_TENANT_REMOVE_ATTRIBUTE_DEFINITIONS + extension;

        return restTemplate.postForObject(url, params, Tenant.class, tenantName);
    }

}
