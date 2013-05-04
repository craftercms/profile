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
package org.craftercms.security.spring.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class CrafterGeneralProfileHandler implements AuthenticationSuccessHandler {
	
	private static final Logger log = Logger.getLogger(CrafterProfileSuccessHandler.class);
	
	private AuthenticationSuccessHandler target;
	private String successUrl;
	
	/* (non-Javadoc)
	 * @see org.springframework.security.web.authentication.AuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
	 */
	public void onAuthenticationSuccess(HttpServletRequest request,
        HttpServletResponse response, Authentication auth) {
    	try {
    		 
	    	if (successUrl != null && !successUrl.equals("")) {
	    		response.sendRedirect(successUrl);
	        } else {
	            target.onAuthenticationSuccess(request, response, auth);
	        }
    	} catch (IOException e) {
			log.error("Error executing authentication success handler. "  + e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
				log.error("Error sending error 500. "  + e);
			} 
		} catch (ServletException e) {
			log.error("Error executing authentication success handler. "  + e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
				log.error("Error sending error 500. "  + e);
			} 
		}
    }
	
	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}
    public void setTarget(AuthenticationSuccessHandler target) {
		this.target = target;
	}

}
