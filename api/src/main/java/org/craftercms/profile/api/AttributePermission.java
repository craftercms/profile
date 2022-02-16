/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.profile.api;

import org.craftercms.commons.security.permissions.DefaultPermission;

/**
 * {@link org.craftercms.commons.security.permissions.Permission} specific for attributes.
 *
 * @author avasquez
 */
public class AttributePermission extends DefaultPermission {

    public static final String ANY_APPLICATION = "*";

    protected String application;

    public AttributePermission() {
        application = ANY_APPLICATION;
    }

    public AttributePermission(String application) {
        this.application = application;
    }

    public String getApplication() {
        return application;
    }

    @Override
    public String toString() {
        return "AttributePermission{" +
                "application='" + application + '\'' +
                ", allowedActions=" + allowedActions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        AttributePermission that = (AttributePermission) o;

        if (!application.equals(that.application)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + application.hashCode();
        return result;
    }

}
