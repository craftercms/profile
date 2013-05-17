/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
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
package org.craftercms.profile.management.services;

import org.apache.log4j.Logger;
import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Schema;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.exceptions.AppAuthenticationException;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.management.model.TenantFilterForm;
import org.craftercms.profile.management.util.AttributeFieldsComparator;
import org.craftercms.profile.management.util.TenantPaging;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author David Escalante
 */
@Service
public class TenantDAOServiceImpl implements TenantDAOService {

    private String username;
    private String password;
    
    private ProfileClient profileRestClient;
    private TenantPaging tenantPaging;

    private String appToken;

    private static final Logger log = Logger.getLogger(TenantDAOServiceImpl.class);

    @Override
    public Tenant createEmptyTenant() {
        return new Tenant();
    }

    @Override
    public Tenant createNewTenant(Tenant tenant) throws AppAuthenticationFailedException{
        if (appToken == null) {
            setAppToken();
        }
        
        Tenant created = null;
        try {
        	created = profileRestClient.createTenant(appToken, tenant.getTenantName(),
                    tenant.getRoles(), tenant.getDomains(), false);
	    } catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			created = profileRestClient.createTenant(appToken, tenant.getTenantName(),
                    tenant.getRoles(), tenant.getDomains(), false);
		}
        if (created != null && created.getTenantName() != null){
            for (Attribute attribute : tenant.getSchema().getAttributes()){
                profileRestClient.setAttributeForSchema(appToken, created.getTenantName(), attribute);
            }
        }
        return created;

    }

    @Override
    public boolean exists(String tenantName) throws AppAuthenticationFailedException {
        if (appToken == null) {
            setAppToken();
        }
        try {
        	return profileRestClient.exitsTenant(appToken, tenantName);
        } catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.exitsTenant(appToken, tenantName);
		}
    }

    @Override
    public long getTenantCount() throws AppAuthenticationFailedException {
        if (appToken == null) {
            setAppToken();
        }
        try {
        	return profileRestClient.getTenantCount(appToken);
        } catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.getTenantCount(appToken);
		}
    }

    @Override
    public List<Tenant> getTenantPage() throws AppAuthenticationFailedException {
        tenantPaging.setTotal(getTenantCount());
        return getTenants(tenantPaging.getSortBy(),
                tenantPaging.getSortOrder(), tenantPaging.getStart(), tenantPaging.getEnd());
    }

    @Override
    public List<Tenant> getNextTenantPage() throws AppAuthenticationFailedException {
        tenantPaging.next();
        return getTenants(tenantPaging.getSortBy(),
                tenantPaging.getSortOrder(), tenantPaging.getStart(), tenantPaging.getEnd());
    }

    @Override
    public List<Tenant> getPrevTenantPage() throws AppAuthenticationFailedException {
        tenantPaging.previous();
        return getTenants(tenantPaging.getSortBy(),
                tenantPaging.getSortOrder(), tenantPaging.getStart(), tenantPaging.getEnd());
    }

    @Override
    public List<Tenant> getSearchTenants(TenantFilterForm tenantFilterForm) throws AppAuthenticationFailedException {
        String tenantName = tenantFilterForm.getTenantName();
        if (tenantName == null || tenantName.isEmpty() || tenantName.equals("*")) {
            return getTenantPage();
        }

        List<Tenant> list = new ArrayList<Tenant>();
        Tenant tenant = getTenantByName(tenantName);
        if (tenant!=null) {
            list.add(tenant);
        }
        return list;
    }

    @Override
    public List<Tenant> getAllTenants() throws AppAuthenticationFailedException {
        if (appToken == null) {
            setAppToken();
        }
        try {
        	return profileRestClient.getAllTenants(appToken);
    	} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
	        return profileRestClient.getAllTenants(appToken);
    	}
    }

    @Override
    public Tenant getTenantByName(String tenantName) throws AppAuthenticationFailedException {
        if (appToken == null) {
            setAppToken();
        }

        Tenant result = profileRestClient.getTenantByName(appToken, tenantName);
        if(result != null)
        {
            Collections.sort(result.getSchema().getAttributes(), new AttributeFieldsComparator());
        }
        return result;
    }

    @Override
    public Tenant getTenantForUpdate(String tenantName) throws AppAuthenticationFailedException {
    	try {
    		return getTenantByName(tenantName);
    	
	    } catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return getTenantByName(tenantName);
		}
        
    }

    @Override
    public Tenant updateTenant(Tenant tenant) throws AppAuthenticationFailedException {
        if (appToken == null) {
            setAppToken();
        }
        try {
        	return profileRestClient.updateTenant(appToken, tenant.getId(), tenant.getTenantName(), tenant.getRoles(), tenant.getDomains());
        } catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.updateTenant(appToken, tenant.getId(), tenant.getTenantName(), tenant.getRoles(), tenant.getDomains());
		}
    }

    @Override
    public Attribute createNewAttribute(Schema schema) {
        Attribute attribute = new Attribute();
        if (schema.getAttributes().size()>0) {
            int idx = schema.getAttributes().size() -1;
            Attribute last = schema.getAttributes().get(idx);
            attribute.setOrder(last.getOrder() + 100);
        } else {
            attribute.setOrder(100);
        }
        return attribute;
    }

    @Override
    public void setSchemaAttribute(Attribute attribute, Tenant tenant) throws AppAuthenticationFailedException{
        Attribute current;
        boolean found = false;
        Iterator<Attribute> it = tenant.getSchema().getAttributes().iterator();
        while (it.hasNext()) {
            current = it.next();
            if (current.getName().trim().equals(attribute.getName().trim())) {
                current.setLabel(attribute.getLabel());
                current.setOrder(attribute.getOrder());
                current.setType(attribute.getType());
                current.setConstraint(attribute.getConstraint());
                current.setRequired(attribute.isRequired());
                found = true;
                break;
            }
        }
        if(!found){
            tenant.getSchema().getAttributes().add(attribute);
            Collections.sort(tenant.getSchema().getAttributes(), new AttributeFieldsComparator());
        }
        if(tenant.getTenantName() != null && exists(tenant.getTenantName())){
            if (appToken == null) {
                setAppToken();
            }
            try {
            	profileRestClient.setAttributeForSchema(appToken, tenant.getTenantName(), attribute);
            } catch(AppAuthenticationException e) {
    			try {
    				
    				setAppToken();
    				
    			} catch (AppAuthenticationFailedException e1) {
    				log.error("could not get an AppToken", e);
    			}
    			profileRestClient.setAttributeForSchema(appToken, tenant.getTenantName(), attribute);
    		}
        }
    }

    @Override
    public void deleteSchemaAttributes(List<String> attributes, Tenant tenant) throws AppAuthenticationFailedException{
        Iterator<Attribute> it = tenant.getSchema().getAttributes().iterator();
        while (it.hasNext()) {
            String attribute = it.next().getName();
            if (attributes.contains(attribute.trim())){
                it.remove();
                if(tenant.getTenantName() != null && exists(tenant.getTenantName())){
                    if (appToken == null) {
                        setAppToken();
                    }
                    try {
                    	profileRestClient.deleteAttributeForSchema(appToken, tenant.getTenantName(), attribute);
	                } catch(AppAuthenticationException e) {
	        			try {
	        				
	        				setAppToken();
	        				
	        			} catch (AppAuthenticationFailedException e1) {
	        				log.error("could not get an AppToken", e);
	        			}
	        			profileRestClient.deleteAttributeForSchema(appToken, tenant.getTenantName(), attribute);
	        		}
                }
            }
        }
    }

    private void setAppToken() throws AppAuthenticationFailedException {
        appToken = profileRestClient.getAppToken(username, password);
    }

    private List<Tenant> getTenants(String sortBy, String sortOrder, int start, int end)
            throws AppAuthenticationFailedException{
        if (appToken == null) {
            setAppToken();
        }
        try {
        	return profileRestClient.getTenantRange(appToken, sortBy, sortOrder, start, end);
        } catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.getTenantRange(appToken, sortBy, sortOrder, start, end);
		}
    }

    @Value("${crafter.profile.app.username}")
    public void setUsername(String username) {
        this.username = username;
    }

    @Value("${crafter.profile.app.password}")
    public void setPassword(String password) {
        this.password = password;
    }

    @Autowired
    public void setProfileRestClient(ProfileClient profileRestClient) {
        this.profileRestClient = profileRestClient;
    }

    @Autowired
    public void setTenantPaging(TenantPaging tenantPaging) {
        this.tenantPaging = tenantPaging;
    }

	@Override
	public void restartAppToken() {
		this.appToken = null;
		
	}


}
