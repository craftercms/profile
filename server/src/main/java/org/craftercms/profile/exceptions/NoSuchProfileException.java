/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.profile.exceptions;

import org.craftercms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown if no profile with a specified ID was found.
 *
 * @author avasquez
 */
public class NoSuchProfileException extends I10nProfileException {

    public static final String KEY_BY_ID = "profile.profile.noSuchProfileById";
    public static final String KEY_BY_QUERY = "profile.profile.noSuchProfileByQuery";
    public static final String KEY_BY_USERNAME = "profile.profile.noSuchProfileByUsername";
    public static final String KEY_BY_TICKET = "profile.profile.noSuchProfileByTicket";

    protected NoSuchProfileException(String key, Object... args) {
        super(key, args);
    }

    public static class ById extends NoSuchProfileException {

        public ById(String id) {
            super(KEY_BY_ID, id);
        }

    }

    public static class ByQuery extends NoSuchProfileException {

        public ByQuery(String tenantName, String query) {
            super(KEY_BY_QUERY, tenantName, query);
        }

    }

    public static class ByUsername extends NoSuchProfileException {

        public ByUsername(String tenantName, String query) {
            super(KEY_BY_USERNAME, tenantName, query);
        }

    }

    public static class ByTicket extends NoSuchProfileException {

        public ByTicket(String id) {
            super(KEY_BY_TICKET, id);
        }

    }

}
