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
import java.util.List;
import java.util.Map;

import org.craftercms.profile.impl.ProfileRestClientImpl;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.profile.impl.domain.Subscriptions;
import org.craftercms.profile.impl.domain.Target;


public class ProfileRestClientTest {
    public static void main(String[] args) {
        ProfileRestClientImpl c = new ProfileRestClientImpl();
        c.setPort(8090);
        try {

            String appToken = c.getAppToken("crafterengine", "crafterengine");
            String ticket = c.getTicket(appToken, "admin", "admin", "craftercms");
            
            
//            Subscriptions subscriptions = profile.getSubscriptions();
//            subscriptions.setFrequency("instant");
            
//          Subscriptions s = new Subscriptions();
//          s.setAction("email");
//          s.setFormat("single");
//          s.setFrequency("instant");
//          Target t2= new Target();
//          t2.setTargetId("isaca");
//          t2.setTargetDescription("isaca");
//          t2.setTargetUrl("isaca.com");
//          s.getSubscription().add(t2);
//          Target t3= new Target();
//          t3.setTargetId("mycontent");
//          t3.setTargetDescription("craftercms");
//          t3.setTargetUrl("craftercms.com");
//          s.getSubscription().add(t3);
//            
//          c.setSubscriptions(appToken, "526ef0da92b3de5a34b2354e", s);
            
          Profile profile = c.getProfile(appToken, "526ef0da92b3de5a34b2354e");
          c.createOrUpdateSubscription(appToken, "5270088303642582af45fb4c", "algo", "algodescription", "hola.com");
          
//          Subscriptions subscriptions = profile.getSubscriptions();
//          if (subscriptions==null) {
//        	  System.out.println("ES NULL");
//          } else {
//        	  System.out.println("GREAT");
//          }
//          
//          ArrayList<String> rolesList = new ArrayList<String>();
//          rolesList.add("SOCIAL_ADMIN");
//          
//          Map<String, Serializable> params = new HashMap<String, Serializable>();
//          params.put("tenantName", "craftercms");
//          params.put("roles", rolesList);
//        params.put("userName", "testsubscript28");
//        params.put("password", "sancarlos");
//        params.put("email", "agonsales.r@gmail.com");
//        params.put("active", "true");
//        params.put("profileId", profile.getId());
//        c.updateProfile(appToken, params);
                        
//            ArrayList<String> periods = new ArrayList<String>();
//            periods.add("instant");
//            ArrayList<String> action = new ArrayList<String>();
//            action.add("email");
//            ArrayList<String> format = new ArrayList<String>();
//            format.add("single");
//            ArrayList<String> target = new ArrayList<String>();
//            target.add("craftercms");
//            HashMap<String, Serializable> subscriptionData = new HashMap<String, Serializable>();
//            subscriptionData.put("period", periods);
//            subscriptionData.put("action", action);
//            subscriptionData.put("format", format);
//            subscriptionData.put("target", target);
    		
            
//            Target t= new Target();
//            t.setTargetId("isaca");
//            t.setTargetDescription("isaca");
//            t.setTargetUrl("isaca.com");
            //Profile p100 = c.addSubscription(appToken, p.getId(), t);
//            Target t1= new Target();
//            t.setTargetId("isaca1");
//            t.setTargetDescription("isaca1");
//            t.setTargetUrl("isaca1.com");
            //Profile p101 = c.addSubscription(appToken, p.getId(), t);
            
            
//            Map<String, Serializable> resultMap = new HashMap<String, Serializable>();
//    		
//    		ArrayList<String> rolesList = new ArrayList<String>();
//    		rolesList.add("SOCIAL_USER");
//    		resultMap.put("roles", rolesList);
//            Map<String, Serializable> attributes = new HashMap<String, Serializable>();
//            Profile p = c.createProfile(appToken, "myisaca71", "myisaca511", true, "isaca", "aagonzalezrojas@gmail.com",attributes);
//            Subscriptions s = new Subscriptions();
//            s.setAction("email");
//            s.setFormat("single");
//            s.setFrequency("instant");
//            Target t2= new Target();
//            t2.setTargetId("isaca");
//            t2.setTargetDescription("isaca");
//            t2.setTargetUrl("isaca.com");
//            s.getSubscription().add(t2);
//            Target t3= new Target();
//            t3.setTargetId("isaca1");
//            t3.setTargetDescription("isaca1");
//            t3.setTargetUrl("isaca1.com");
//            s.getSubscription().add(t3);
//            Subscriptions a = c.setSubscriptions(appToken, p.getId(), s);
//            System.out.print(a.getSubscription().get(0).getTargetId());
//            
//            Target t= new Target();
//          t.setTargetId("updateisaca");
//          t.setTargetDescription("updateisaca");
//          t.setTargetUrl("updateisaca.com");
//
//          Target t1= new Target();
//          t1.setTargetId("updateisaca1");
//          t1.setTargetDescription("updateisaca1");
//          t1.setTargetUrl("updateisaca1.com");
//          s.getSubscription().clear();
//          s.getSubscription().add(t);
//          s.getSubscription().add(t1);
//          a = c.setSubscriptions(appToken, p.getId(), s);
          

            
            //c.removeSubscription(appToken, p.getId(), "craftercms");
//            c.addSubscription(appToken, p.getId(), "isaca","isaca title", "isaca.com");
            
//            period: "["instant", "daily", "weekly"]",
// 	       action: "["email" (potentially "im", "sms" in the future)]"
// 	       format: "["single", "short", "long"]"
            
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
            //System.out.println(ticket);
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
            
            
//			BufferedReader br = new BufferedReader(new InputStreamReader(
//			(response.getEntity().getContent())));
//
//	// Read in all of the post results into a String.
//	String output = "";
//	Boolean keepGoing = true;
//	while (keepGoing) {
//		String currentLine = br.readLine();
//
//		if (currentLine == null)
//			keepGoing = false;
//		else
//			output += currentLine;
//	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}