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
package org.craftercms.security.spring.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.UserAuthenticationFailedException;
import org.craftercms.crafterprofile.user.UserProfile;
import org.craftercms.security.spring.constants.SpringSecurityConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * TODO: Override this to create a customized profile
 * @author Sandra O'Keeffe
 *
 */
public class CrafterProfileAuthenticationProvider implements
		AuthenticationProvider {

	private ProfileClient profileRestClient;
	
	private String crafterProfileAppUsername;
	private String crafterProfileAppPassword;
	private String crafterProfileAppTenantName;
	
	private static final Logger log = Logger.getLogger(CrafterProfileAuthenticationProvider.class);
	
	
	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
	 */
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
	
		String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        // authenticate against crafter profile
        try {
        	// TODO: don't need to get this each time 
        	// get the app token with the super user
        	String appToken = profileRestClient.getAppToken(crafterProfileAppUsername, crafterProfileAppPassword);
        	
    		String ticket = profileRestClient.getTicket(appToken, username, password,this.crafterProfileAppTenantName);
        	
        	Profile profile = profileRestClient.getProfileByUsernameWithAllAttributes(appToken, username, this.crafterProfileAppTenantName);
			
			// put user details in session
			if (profile == null) {
				log.error("Error getting profile for username=" + username + ", with appToken=" + appToken);
				throw new AuthenticationCredentialsNotFoundException("Error getting profile for username=" + username + ", with appToken=" + appToken);
			} else {
				UserProfile userProfile = getUserProfileObject();
				
				createUserProfile(profile, userProfile);
				userProfile.setTicket(ticket);
				
				// the authorities should be the last one if needed
				return new UsernamePasswordAuthenticationToken(userProfile, null, getGrantedAuthorityList(userProfile));
			}
			
        } catch (AppAuthenticationFailedException e) {
        	log.error("Error authenticating at app level=" + username);
        	throw new BadCredentialsException("Error authenticating username=" + username, e);
		} catch (UserAuthenticationFailedException e) {
			log.error("Error authenticating username=" + username);
        	throw new BadCredentialsException("Error authenticating username=" + username, e);
		}
		
	}
	

	/**
	 * Override this to specify a custom user profile object to use
	 * @return
	 */
	protected UserProfile getUserProfileObject() {
		return new UserProfile();
	}

	/**
	 * Sets the base attributes in Crafter Profile
	 * Override this & extend UserProfile to set custom attributes in the 
	 * UserProfile object 
	 * @param profile
	 */
	protected void createUserProfile(Profile profile, UserProfile userProfile) {
		
		// TODO: userProfile should not be null here, should do a check
		// set the base attributes
		userProfile.setActive(profile.getActive());
		userProfile.setCreatedDate(profile.getCreated());
		userProfile.setModifiedDate(profile.getModified());
		userProfile.setId(profile.getId());
		userProfile.setUsername(profile.getUserName());
		userProfile.setTemporaryPassword(profile.getAttributes()==null? false :Boolean.valueOf((String) profile.getAttributes().get(SpringSecurityConstants.TEMPORARY_PASSWORD)));
		userProfile.getRoles().addAll(profile.getRoles());
		userProfile.setTenantName(profile.getTenantName());

	}
	
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);

	}
	
	public void setProfileRestClient(ProfileClient profileRestClient) {
		this.profileRestClient = profileRestClient;
	}
	
	
	@Value("${crafter.profile.app.username}")
	public void setCrafterProfileAppUsername(String crafterProfileAppUsername) {
		this.crafterProfileAppUsername = crafterProfileAppUsername;
	}

	@Value("${crafter.profile.app.password}")
	public void setCrafterProfileAppPassword(String crafterProfileAppPassword) {
		this.crafterProfileAppPassword = crafterProfileAppPassword;
	}
	
	@Value("${crafter.profile.app.tenant.name}")
	public void setCrafterProfileAppTenantName(String crafterProfileAppTenantName) {
		this.crafterProfileAppTenantName = crafterProfileAppTenantName;
	}
	
	public static List<GrantedAuthority> getGrantedAuthorities(List<String> roles) {
		if (roles == null || roles.size() == 0) {
			return null;
		}
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}
	
	private List<GrantedAuthority> getGrantedAuthorityList(UserProfile userProfile) {
		List<GrantedAuthority> lga = null;
		if (userProfile.getRoles().size() > 0) {
			GrantedAuthority ga;
			lga = new ArrayList<GrantedAuthority>();
			for (String r: userProfile.getRoles()) {
				ga = new GrantedAuthorityImpl(r);
				lga.add(ga);
			}
		}
		return lga;
	}

}
