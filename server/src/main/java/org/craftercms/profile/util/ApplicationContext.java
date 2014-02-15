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
package org.craftercms.profile.util;

import org.craftercms.profile.api.Tenant;

/**
 * Context that holds information of a logged in application.
 *
 * @author avasquez
 */
public class ApplicationContext {

    private static ThreadLocal<ApplicationContext> threadLocal = new ThreadLocal<ApplicationContext>();

    private String application;
    private Tenant tenant;

    /**
     * Returns the context for the current thread.
     */
    public static ApplicationContext getCurrent() {
        return threadLocal.get();
    }

    /**
     * Sets the context for the current thread.
     */
    public static void setCurrent(ApplicationContext current) {
        threadLocal.set(current);
    }

    /**
     * Removes the context from the current thread.
     */
    public static void clear() {
        threadLocal.remove();
    }

    public ApplicationContext(String application, Tenant tenant) {
        this.application = application;
        this.tenant = tenant;
    }

    public String getApplication() {
        return application;
    }

    public Tenant getTenant() {
        return tenant;
    }

}
