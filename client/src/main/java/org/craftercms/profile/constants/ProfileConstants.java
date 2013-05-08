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
package org.craftercms.profile.constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.craftercms.profile.domain.Profile;

/**
 * Main constanst for Profile Client project
 * @author Alvaro Gonzalez
 *
 */
public class ProfileConstants {
	public static List<String> ANONYMOUS_ROLES = new ArrayList<String>(){
		{
			add("ANONYMOUS"); 
		}
	};
	public static final Profile ANONYMOUS = new Profile(null, "anonymous", null, true, new Date(), new Date(), null, ANONYMOUS_ROLES);
	
	public static final String ROLES = "roles";
    public static final String DOMAINS = "domains";
	public static final String DEFAULT_ROLE = "USER";
	public static final String SUPER_ADMIN = "SUPERADMIN";
	
	public static final String TENANT_NAME = "tenantName";
	public static final String APP_TOKEN = "appToken";
	public static final String USER_NAME = "userName";
	public static final String PASSWORD = "password";
	public static final String ACTIVE = "active";
	public static final String TICKET = "ticket";
	public static final String ROLE_NAME = "roleName";
	
}
