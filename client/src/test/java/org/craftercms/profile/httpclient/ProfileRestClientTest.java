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
package org.craftercms.profile.httpclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.craftercms.profile.impl.ProfileRestClientImpl;
import org.craftercms.profile.impl.domain.Profile;


public class ProfileRestClientTest {
    public static void main(String[] args) {
        ProfileRestClientImpl c = new ProfileRestClientImpl();
        c.setPort(8090);
        try {

            String appToken = c.getAppToken("crafterengine", "crafterengine");
            String ticket = c.getTicket(appToken, "admin", "admin", "craftercms");
//            Map<String, Serializable> params = new HashMap<String, Serializable>();
//            ArrayList<String> roles = new ArrayList<String>();
//            roles.add("SOCIAL_ADMIN");
//            params.put("appToken", appToken);
//            params.put("tenantName", "craftercms");
//            params.put("userName", "adminactiveuser4");
//            params.put("password", "admin3");
//            params.put("email", "admindeleteuser@profile.com");
//            params.put("active", "true");
//
//            params.put("roles", roles);
//
//            Profile admindeleteuser = c.createProfile(appToken, params);
//
//            c.activeProfile(appToken, admindeleteuser.getId().toString(), false);
//
//            Profile inactive = c.getProfileByUsername(appToken, "adminactiveuser4", "craftercms");
            System.out.println(ticket);
            //assertTrue(inactive.getActive()==false);


            //c.changePassword(appToken,"http://localhost:8090/crafter-profile-admin-console/changepassword.ftl?",
            // "craftercms", "admin");
            //			String ticket = c.getTicket(appToken, "admin", "admin", "craftercms");
            //			Profile p = c.getProfileByUsername(appToken, "test", "craftercms");
            //			Map queryParams = new HashMap<String, Serializable>();
            //			queryParams.put("Direccion", "Address");
            //			c.setAttributesForProfile(appToken, p.getId(), queryParams);

            //			List<String> roles = new ArrayList<String>();
            //			roles.add("SOCIAL_AUTHOR");
            //			roles.add("SOCIAL_USER");
            //
            //
            //			List<String> roles1 = c.getRoles(appToken, "51d3380103641b01aa71cc03", "craftercms",
            // new String[]{"social_based_group","social_high_level_group"});
            //			if(roles1!=null) {
            //				for(String s:roles1) {
            //					System.out.println("Role " +s);
            //				}
            //			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}