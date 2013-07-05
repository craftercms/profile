package org.craftercms.profile.management.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.exceptions.AppAuthenticationException;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.impl.ProfileRestClientImpl;
import org.craftercms.profile.impl.domain.GroupRole;
import org.craftercms.profile.management.services.GroupRoleMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GroupRoleMappingServiceImpl implements GroupRoleMappingService {
	
	private String username;
	private String password;
	
	private String appToken;
	private ProfileClient profileRestClient;
	private String crafterProfileAppTenantName;
	
	private static final Logger log = Logger.getLogger(GroupRoleMappingServiceImpl.class);
	
	@Override
	public GroupRole createGroupRoleMapping(GroupRole groupRole) 
			throws AppAuthenticationFailedException {
		if (appToken==null) {
			setAppToken();
		}
		try {
        	return profileRestClient.createGroupRoleMapping(appToken, groupRole.getTenantName(), groupRole.getName(), groupRole.getRoles());
	    } catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.createGroupRoleMapping(appToken, groupRole.getTenantName(), groupRole.getName(), groupRole.getRoles());
		}
	}

	@Override
	public GroupRole updateGroupRoleMapping(GroupRole groupRole) 
			throws AppAuthenticationFailedException {
		if (appToken==null) {
			setAppToken();
		}
		try {
        	return profileRestClient.updateGroupRoleMapping(appToken, groupRole.getTenantName(), groupRole.getId(), groupRole.getRoles());
	    } catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.updateGroupRoleMapping(appToken, groupRole.getTenantName(), groupRole.getId(), groupRole.getRoles());
		}
	}

	@Override
	public List<GroupRole> getGroupRoleMapping(String tenant) throws AppAuthenticationFailedException {
		// TODO Auto-generated method stub
		if (appToken==null) {
			setAppToken();
		}
		try {
        	return profileRestClient.getGroupRoleMappingByTenant(appToken, tenant);
	    } catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.getGroupRoleMappingByTenant(appToken, tenant);
		}
	
	}
	
	@Override
	public GroupRole getGroupRoleItem(String groupId) throws AppAuthenticationFailedException {
		// TODO Auto-generated method stub
		if (appToken==null) {
			setAppToken();
		}
		try {
        	return profileRestClient.getGroupRoleMapping(this.appToken, groupId);
	    } catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.getGroupRoleMapping(this.appToken, groupId);
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

    @Value("${crafter.profile.app.tenant.name}")
    public void setCrafterProfileAppTenantName(String crafterProfileAppTenantName) {
        this.crafterProfileAppTenantName = crafterProfileAppTenantName;
    }
    
    public void setAppToken() throws AppAuthenticationFailedException {
		appToken = profileRestClient.getAppToken(username, password);		
	}

	@Override
	public void deleteGroupRoleMapping(List<String> ids)
			throws AppAuthenticationFailedException {
		if (ids == null) {
			return;
		}
		for(String id:ids) {
			deleteGroupRoleMapping(id);
		}
		
	}
	
	public void deleteGroupRoleMapping(String id)
			throws AppAuthenticationFailedException {
		
		if (appToken==null) {
			setAppToken();
		}
		try {
        	profileRestClient.deleteGroupRoleMapping(appToken, id);
	    } catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			profileRestClient.deleteGroupRoleMapping(appToken, id);
		}
	}

}
