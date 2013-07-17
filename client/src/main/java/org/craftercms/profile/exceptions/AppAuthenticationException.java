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
package org.craftercms.profile.exceptions;

/**
 * Thrown whenever the <code>ProfileClient</code> receives FORBIDDEN (403) error code from
 * the profile server response.
 * 
 * @author Alvaro Gonzalez
 *
 */
public class AppAuthenticationException extends RuntimeException {
	
	public AppAuthenticationException() {
		super();
	}

	public AppAuthenticationException(String msg, Throwable thr) {
		super(msg, thr);
	}

	public AppAuthenticationException(String msg) {
		super(msg);
	}

	public AppAuthenticationException(Throwable thr) {
		super(thr);
	}

}
