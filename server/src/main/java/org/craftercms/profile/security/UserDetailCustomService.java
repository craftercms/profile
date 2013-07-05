package org.craftercms.profile.security;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

public class UserDetailCustomService implements UserDetailsService {
	
	private Map<String,UserDetails> users;
	private static final String USER_PROPERTIES = "crafter/profile/apps.properties";
	private static final String USER_EXTERNAL_PROPERTIES = "crafter/profile/extension/apps.properties";
	
	private String userCredentialFilePath = USER_PROPERTIES;
	private String externalCredentialFilePath = USER_EXTERNAL_PROPERTIES;
	
	private Properties internalUserFile;
	private Properties externalUserFile;
	
	public UserDetailCustomService() {
		super();
		loadUsersFromProperties();
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		//return null;
		return getUser(username);
	}
	
	private void loadUsersFromProperties() {
		users = new HashMap<String, UserDetails>();
		internalUserFile = new Properties();
		
		
		externalUserFile = new Properties();
		try {
			//internalUserFile.load(UserDetailCustomService.class.getClassLoader().getResourceAsStream(
			//		USER_PROPERTIES));
			
			externalUserFile.load(UserDetailCustomService.class.getClassLoader().getResourceAsStream(
					USER_EXTERNAL_PROPERTIES));
		} catch(IOException e) {}
		
		Enumeration<?> e =externalUserFile.propertyNames();
		String key;
		User current;
		while(e.hasMoreElements()) {
			key = (String) e.nextElement();
			current = getUser(key);
			if (current != null) {
				users.put(key, getUser(key));
			}
		}
	}
	
	private User getUser(String key) {
		User u = null;
		String values = (String)externalUserFile.get(key);
//		if (values.isEmpty()) {
//			values = (String)internalUserFile.get(key);
//		}
//		u = null;
//		return u;
		if (values==null || values.isEmpty()) {
			return null;
		} 
		String[] credentials =  values.split(",");
		return new User(key, credentials[0], getAuthorities(credentials)); 
	}
	
	private List<GrantedAuthority> getAuthorities(String[] credentials) {
		//username=password,grantedAuthority,enabled
		List<GrantedAuthority> ga = new ArrayList<GrantedAuthority>();
		for (int i = 1; i < credentials.length - 1; i++ ) {
			ga.add(new GrantedAuthorityImpl(credentials[i]));
		}
		return ga;
			
	}

	public String getUserCredentialFilePath() {
		return userCredentialFilePath;
	}

	public void setUserCredentialFilePath(String userCredentialFilePath) {
		this.userCredentialFilePath = userCredentialFilePath;
	}

	public String getExternalCredentialFilePath() {
		return externalCredentialFilePath;
	}

	public void setExternalCredentialFilePath(String externalCredentialFilePath) {
		this.externalCredentialFilePath = externalCredentialFilePath;
	}

}

