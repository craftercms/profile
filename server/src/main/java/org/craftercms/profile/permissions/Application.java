/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
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
package org.craftercms.profile.permissions;

import org.craftercms.profile.api.TenantPermission;

import java.util.List;

/**
 * The application subject, which is the user of Crafter Profile.
 *
 * @author avasquez
 */
public class Application {

    private static final ThreadLocal<Application> threadLocal = new ThreadLocal<>();

    private String name;
    private List<TenantPermission> tenantPermissions;

    public static Application getCurrent() {
        return threadLocal.get();
    }

    public static void setCurrent(Application current) {
        threadLocal.set(current);
    }

    public static void clear() {
        threadLocal.remove();
    }

    public Application(String name, List<TenantPermission> tenantPermissions) {
        this.name = name;
        this.tenantPermissions = tenantPermissions;
    }

    public String getName() {
        return name;
    }

    public List<TenantPermission> getTenantPermissions() {
        return tenantPermissions;
    }

    @Override
    public String toString() {
        return "Application{" +
                "name='" + name + '\'' +
                ", tenantPermissions=" + tenantPermissions +
                '}';
    }

}
