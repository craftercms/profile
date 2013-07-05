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

import java.util.ArrayList;
import java.util.List;

import org.craftercms.profile.impl.ProfileRestClientImpl;
import org.craftercms.profile.impl.domain.GroupRole;



public class ProfileRestClientTest {
	public static void main(String[] args) {
		ProfileRestClientImpl c = new ProfileRestClientImpl();
		c.setPort(8090);
		try { 
			String appToken = c.getAppToken("crafterengine", "crafterengine");
			c.getTicket(appToken, "admin", "admin", "craftercms");
			List<String> roles = new ArrayList<String>();
			//roles.add("SOCIAL_ADMIN");
			roles.add("SOCIAL_AUTHOR");
			roles.add("SOCIAL_USER");
			//roles.add("ANONYMOUS");
			//GroupRole g = c.createGroupRoleMapping(appToken, "craftercms", "social_high_level_group", roles);
			//GroupRole g = c.updateGroupRoleMapping(appToken, "craftercms", "51d43fe403642fee97a22a3f", roles);
//			System.out.println("NEW GROUP " + g.getId() + " name " + g.getName() );
//			for (String cr:g.getRoles()) {
//				System.out.println("NEW GROUP Roles " + cr);
//			}
			
			List<String> roles1 = c.getRoles(appToken, "51d3380103641b01aa71cc03", "craftercms", new String[]{"social_based_group","social_high_level_group"});
			if(roles1!=null) {
				for(String s:roles1) {
					System.out.println("Role " +s);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}