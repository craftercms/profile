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
package org.craftercms.profile.management.util;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.craftercms.profile.impl.domain.Attribute;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.profile.management.model.SchemaForm;
import org.craftercms.profile.management.model.ProfileUserAccountForm;

public class ProfileUserAccountUtil {
	
	private static final Logger log = Logger.getLogger(ProfileUserAccountUtil.class);
	
	public static List<String> getAttributes(SchemaForm schema) {
		List<String> a = new ArrayList<String>();
		Iterator<Attribute> it = schema.getAttributes().iterator();
		while (it.hasNext()) {
	    	a.add(it.next().getName());
	    }
		return a;
	}
	
	public static List<ProfileUserAccountForm> mapForForm(List<Profile> profiles) {
		List<ProfileUserAccountForm> result = new ArrayList<ProfileUserAccountForm>();
		for(Profile p:profiles) {
			result.add(new ProfileUserAccountForm(p));
		}
		
		return result;
	}
	
	/**
	 * Build the map with the user data to update
	 * 
	 * @param profile
	 * @return
	 */
	public static Map<String, Serializable> getUpdateUserData(ProfileUserAccountForm profile) {
		
		Map<String, Serializable> userData = new HashMap<String, Serializable>();
		setProfileUserAccountFormToMap(userData, profile);
		
		return userData;
	}
	
	public static List<String> getItemList(HttpServletRequest request) {
		List<String> items = new ArrayList<String>();
		try {
		    BufferedReader reader = request.getReader();
		    String line;
		    int idx;
		    String[] splitData;
		    while ((line = reader.readLine()) != null) {
		    	line = removeAmpersonChar(line);
		    	idx = line.indexOf("item=");
		    	splitData = idx >= 0 ? line.substring(idx).split("item="):null;
		    	if (splitData == null) {
		    		continue;
		    	}
		    	for(String s: splitData) {
		    		if (s!=null && !s.equals("")) {
		    			items.add(s.replace('+', ' '));
		    		}
		    	}
		    }
		    	
		  } catch (Exception e) { 
			  e.printStackTrace(); 
		  }
		return items;
	}
	
	private static void setProfileUserAccountFormToMap(Map<String, Serializable> userData, ProfileUserAccountForm profile) {
		userData.put(ProfileUserAccountConstants.USERNAME_PROPERTY, profile.getUsername());
		userData.put(ProfileUserAccountConstants.PASSWORD_PROPERTY, profile.getPassword());
		userData.put(ProfileUserAccountConstants.ACTIVE_PROPERTY, String.valueOf(profile.isActive()));
		userData.put(ProfileUserAccountConstants.PROFILE_ID_PROPERTY, profile.getId());
		userData.put(ProfileUserAccountConstants.ROLES, profile.getRoles());
		userData.put(ProfileUserAccountConstants.TENANT_NAME, profile.getTenantName());
        for(String key : profile.getAttributes().keySet()){
            userData.put(key, (Serializable)profile.getAttributes().get(key));
        }
	}
	
	public static Map<String, String> getRolesAdmin() {
        Map<String, String> data = new HashMap<String,String>();
        
        data.put(ProfileUserAccountConstants.ADMIN_ROLE, ProfileUserAccountConstants.ADMIN_ROLE);
        data.put(ProfileUserAccountConstants.DEFAULT_ROLE, ProfileUserAccountConstants.DEFAULT_ROLE);
        return data;
    }
	
	public static Map<String, String> getAttributesSupportedTypes() {
		Map<String, String> types = new HashMap<String, String>();
		types.put("Text","Text");
		return types;
			    
	}
	
	private static String removeAmpersonChar(String data) {
		data = data.replace("&", "");
		return data;
	}

}
