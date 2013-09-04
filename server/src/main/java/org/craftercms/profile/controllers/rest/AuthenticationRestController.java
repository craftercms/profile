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
package org.craftercms.profile.controllers.rest;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/api/2/auth")
public class AuthenticationRestController {

    /**
     * Get APP Token
     *
     * @return String app_token
     * @throws AuthenticationException
     */
    @RequestMapping(value = "/app_token", method = RequestMethod.GET)
    @ModelAttribute
    public String authenticateApplication(HttpServletRequest request) throws AuthenticationException {
        String appToken = (String)request.getAttribute("appToken");

        return appToken;
    }

    @RequestMapping(value = "/ticket", method = RequestMethod.GET)
    @ModelAttribute
    public String authenticateProfile(HttpServletRequest request, @RequestParam(value = "username") String userName,
                                      @RequestParam String password) throws AuthenticationException {
        String ticket = (String)request.getAttribute("ticket");

        return ticket;
    }

    @RequestMapping(value = "/ticket/validate", method = RequestMethod.GET)
    @ModelAttribute
    public boolean validateProfileTicket(HttpServletRequest request, @RequestParam String ticket) {
        return request.getAttribute("ticket") != null;
    }

    @RequestMapping(value = "/ticket/invalidate", method = RequestMethod.POST)
    @ModelAttribute
    public void invalidateProfileTicket(HttpServletRequest request, @RequestParam String ticket) {
        return;
    }

}