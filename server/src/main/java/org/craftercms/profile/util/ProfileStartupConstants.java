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
package org.craftercms.profile.util;

public interface ProfileStartupConstants {
	
	static final String ADMIN_USER = "admin.username";
	static final String ADMIN_PASSWORD = "admin.password";
	
	static final String BASE_SCHEMA = "base.schema.name";
	
	static final String TOKEN_USERNAME = "token.username";
	static final String TOKEN_PASSWORD = "token.password";
	
	
	public static final String PORT = "port";
	public static final String SCHEME = "scheme";
	public static final String HOST = "host";
	public static final String APP_PATH = "appPath";
	public static final String PROFILE_USERNAME = "profile-username";
	public static final String PROFILE_PASSWORD = "profile-password";
	public static final String SUPER_ADMIN_USER = "profile-superadmin-user";
	public static final String SUPER_ADMIN_PASSWORD = "profile-superadmin-password";
	
	public static final String PROFILE_APP_USERNAME = "profile-app-username";
	public static final String PROFILE_APP_PASSWORD = "profile-app-password";
	public static final String TARGET = "target";
	public static final String IS_DEFAULT_ROLES = "defaultRoles";
	
	public static final String TENANT_NAME = "tenant-name";
	
	public static final String PROFILES_FILE = "profileNameFile";
	public static final String ADMIN_ROLES = "admin-roles";
	public static final String SUPERADMIN_ROLES = "superadmin-roles";
	public static final String DEFAULT_ROLES = "default-roles";
	
}
