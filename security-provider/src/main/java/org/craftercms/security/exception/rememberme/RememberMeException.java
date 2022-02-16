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

package org.craftercms.security.exception.rememberme;

import org.craftercms.security.exception.AuthenticationException;

/**
 * Root exception for remember me related errors.
 *
 * @author avasquez
 */
public class RememberMeException extends AuthenticationException {

    public RememberMeException(String s) {
        super(s);
    }

    public RememberMeException(String s, Throwable throwable) {
        super(s, throwable);
    }
    
}
