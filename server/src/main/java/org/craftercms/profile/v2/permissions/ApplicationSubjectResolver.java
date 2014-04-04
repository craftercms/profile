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
package org.craftercms.profile.v2.permissions;

import org.craftercms.commons.security.permissions.SubjectResolver;

/**
 * {@link org.craftercms.commons.security.permissions.SubjectResolver} that resolves to the current
 * {@link org.craftercms.profile.v2.permissions.Application}.
 *
 * @author avasquez
 */
public class ApplicationSubjectResolver implements SubjectResolver<Application> {

    @Override
    public Application getCurrentSubject() {
        return Application.getCurrent();
    }

}
