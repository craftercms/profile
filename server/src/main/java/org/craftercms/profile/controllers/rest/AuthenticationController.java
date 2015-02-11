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
package org.craftercms.profile.controllers.rest;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import org.craftercms.profile.api.PersistentLogin;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.craftercms.profile.api.ProfileConstants.BASE_URL_AUTHENTICATION;
import static org.craftercms.profile.api.ProfileConstants.PARAM_PASSWORD;
import static org.craftercms.profile.api.ProfileConstants.PARAM_PROFILE_ID;
import static org.craftercms.profile.api.ProfileConstants.PARAM_TENANT_NAME;
import static org.craftercms.profile.api.ProfileConstants.PARAM_USERNAME;
import static org.craftercms.profile.api.ProfileConstants.PATH_VAR_ID;
import static org.craftercms.profile.api.ProfileConstants.URL_AUTH_AUTHENTICATE;
import static org.craftercms.profile.api.ProfileConstants.URL_AUTH_CREATE_PERSISTENT_LOGIN;
import static org.craftercms.profile.api.ProfileConstants.URL_AUTH_CREATE_TICKET;
import static org.craftercms.profile.api.ProfileConstants.URL_AUTH_DELETE_PERSISTENT_LOGIN;
import static org.craftercms.profile.api.ProfileConstants.URL_AUTH_GET_PERSISTENT_LOGIN;
import static org.craftercms.profile.api.ProfileConstants.URL_AUTH_GET_TICKET;
import static org.craftercms.profile.api.ProfileConstants.URL_AUTH_INVALIDATE_TICKET;
import static org.craftercms.profile.api.ProfileConstants.URL_AUTH_REFRESH_PERSISTENT_LOGIN_TOKEN;

/**
 * REST controller for the authentication service.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(BASE_URL_AUTHENTICATION)
@Api(value = "authentication", basePath = BASE_URL_AUTHENTICATION, description = "Authentication operations")
public class AuthenticationController {

    protected AuthenticationService authenticationService;

    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @ApiOperation("Authenticates the user, and returns a ticket identifying the authentication")
    @RequestMapping(value = URL_AUTH_AUTHENTICATE, method = RequestMethod.POST)
    @ResponseBody
    public Ticket authenticate(@ApiParam("The tenant's name") @RequestParam(PARAM_TENANT_NAME) String tenantName,
                               @ApiParam("The username") @RequestParam(PARAM_USERNAME) String username,
                               @ApiParam("The password") @RequestParam(PARAM_PASSWORD)
                               String password) throws ProfileException {
        return authenticationService.authenticate(tenantName, username, password);
    }

    @ApiOperation(value = "Create a new ticket for the specified profile", notes = "This method should only be " +
        "used when authentication is done through other means (like when authenticating through Facebook or " +
        "Twitter) different than profile.")
    @RequestMapping(value = URL_AUTH_CREATE_TICKET, method = RequestMethod.POST)
    @ResponseBody
    public Ticket createTicket(
        @ApiParam("The ID of the profile") @RequestParam(PARAM_PROFILE_ID) String profileId) throws ProfileException {
        return authenticationService.createTicket(profileId);
    }

    @ApiOperation("Returns the ticket object for the given ticket ID")
    @RequestMapping(value = URL_AUTH_GET_TICKET, method = RequestMethod.GET)
    @ResponseBody
    public Ticket getTicket(
        @ApiParam("The ID of the ticket") @PathVariable(PATH_VAR_ID) String ticketId) throws ProfileException {
        return authenticationService.getTicket(ticketId);
    }

    @ApiOperation("Invalidates the given ticket")
    @RequestMapping(value = URL_AUTH_INVALIDATE_TICKET, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void invalidateTicket(
        @ApiParam("The ID of the ticket") @PathVariable(PATH_VAR_ID) String ticketId) throws ProfileException {
        authenticationService.invalidateTicket(ticketId);
    }

    @ApiOperation("Creates a persistent login, use for remember me functionality")
    @RequestMapping(value = URL_AUTH_CREATE_PERSISTENT_LOGIN, method = RequestMethod.POST)
    @ResponseBody
    public PersistentLogin createPersistentLogin(@ApiParam("The ID ID of the profile") @RequestParam(PARAM_PROFILE_ID)
                                                 String profileId) throws ProfileException {
        return authenticationService.createPersistentLogin(profileId);
    }

    @ApiOperation("Returns the persistent login object for the given ID")
    @RequestMapping(value = URL_AUTH_GET_PERSISTENT_LOGIN, method = RequestMethod.GET)
    @ResponseBody
    public PersistentLogin getPersistentLogin(
        @ApiParam("The ID of the persistent login") @PathVariable(PATH_VAR_ID) String loginId) throws ProfileException {
        return authenticationService.getPersistentLogin(loginId);
    }

    @ApiOperation("Refreshes the token of the specified persistent login")
    @RequestMapping(value = URL_AUTH_REFRESH_PERSISTENT_LOGIN_TOKEN, method = RequestMethod.POST)
    @ResponseBody
    public PersistentLogin refreshPersistentLoginToken(
        @ApiParam("The ID of the persistent login") @PathVariable(PATH_VAR_ID) String loginId) throws ProfileException {
        return authenticationService.refreshPersistentLoginToken(loginId);
    }

    @ApiOperation("Deletes the persistent login")
    @RequestMapping(value = URL_AUTH_DELETE_PERSISTENT_LOGIN, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void deletePersistentLogin(
        @ApiParam("The ID of the persistent login") @PathVariable(PATH_VAR_ID) String loginId) throws ProfileException {
        authenticationService.deletePersistentLogin(loginId);
    }

}
