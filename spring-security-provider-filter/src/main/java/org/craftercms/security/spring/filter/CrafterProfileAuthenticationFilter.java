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
package org.craftercms.security.spring.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.craftercms.crafterprofile.user.UserProfile;
import org.craftercms.security.spring.constants.SpringSecurityConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 
 * @author Sandra O'Keeffe
 */
public class CrafterProfileAuthenticationFilter extends
		UsernamePasswordAuthenticationFilter {

	
	private static final Logger log = Logger.getLogger(CrafterProfileAuthenticationFilter.class);
	
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
       
		Authentication authentication = super.attemptAuthentication(request, response); 
		
		log.debug("Authenticated user with username=" + ((UserProfile) authentication.getPrincipal()).getUsername());
		request.getSession().setAttribute(SpringSecurityConstants.USER_PROFILE_SESSION_ID, authentication.getPrincipal());
		
		return authentication;
	}
}
