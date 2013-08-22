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

public final class ProfileConstants {
	
	private ProfileConstants(){
	}

	// Domain Constants
	public static final String FIELD_ID = "_id";
	public static final String USER_NAME = "userName";
	public static final String PASSWORD = "password";
	public static final String EMAIL = "email";
	public static final String ACTIVE = "active";
	public static final String CREATED = "created";
	public static final String MODIFIED = "modified";
	public static final String ROLES = "roles";
    public static final String DOMAINS = "domains";
    
	// Request Parameter Constants
	public static final String APP_TOKEN = "appToken";
	public static final String SORT_BY = "sortBy";
	public static final String ATTRIBUTES = "attributes";
	public static final String ATTRIBUTES_DOT = ATTRIBUTES + ".";
	public static final String ROLES_DOT = ROLES + ".";
	public static final String SORT_ORDER = "sortOrder";
	public static final String START = "start";
	public static final String END = "end";
	
	public static final String ROLE_NAME = "roleName";

    //Tenant Constants
    public static final String TENANT_ID = "tenantId";
	public static final String TENANT_NAME = "tenantName";
    public static final String SCHEMA = "schema";
    public static final String SCHEMA_DOT = SCHEMA + ".";

	public static final String PROFILE_ID = "profileId";
	public static final String PROFILE_ID_LIST = "profileIdList";

	// Sort Constants
	public static final String SORT_ORDER_DESC = "DESC";
	
	public static final String[] DEFAULT_ROLES = new String[] {"USER","ADMIN", "MODERATOR"};

    public static final String[] DEFAULT_TENANT_ROLES = new String[] {"SOCIAL_USER"};
    public static final String[] DEFAULT_TENANT_DOMAINS = new String[] {"127.0.0.1", "localhost"};

	public static final String[] DOMAIN_PROFILE_FIELDS = {ProfileConstants.USER_NAME, ProfileConstants.PASSWORD, ProfileConstants.ACTIVE};
	public static final String[] BASE_PROFILE_FIELDS = {ProfileConstants.USER_NAME, ProfileConstants.PASSWORD, ProfileConstants.ACTIVE, ProfileConstants.TENANT_NAME, ProfileConstants.ROLES, ProfileConstants.EMAIL, 
															ProfileConstants.APP_TOKEN, ProfileConstants.PROFILE_ID, ProfileConstants.PROFILE_ID_LIST};

    public static final String[] TENANT_ORDER_BY_FIELDS = {ProfileConstants.FIELD_ID, ProfileConstants.TENANT_NAME};
}