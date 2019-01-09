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

package org.craftercms.profile.controllers.rest;

import java.util.Collections;
import java.util.List;

import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AccessTokenService;
import org.craftercms.profile.exceptions.NoSuchAccessTokenException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.craftercms.profile.api.ProfileConstants.*;

/**
 * REST controller for the access token service.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(BASE_URL_ACCESS_TOKEN)
public class AccessTokenController {

    protected AccessTokenService accessTokenService;

    @Required
    public void setAccessTokenService(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @RequestMapping(value = URL_ACCESS_TOKEN_CREATE, method = RequestMethod.POST)
    @ResponseBody
    public AccessToken createToken(@RequestBody AccessToken token) throws ProfileException {
        return accessTokenService.createToken(token);
    }

    @RequestMapping(value = URL_ACCESS_TOKEN_GET, method = RequestMethod.GET)
    @ResponseBody
    public AccessToken getToken(@PathVariable(PATH_VAR_ID) String id) throws ProfileException {
        AccessToken token = accessTokenService.getToken(id);
        if (token != null) {
            return token;
        } else {
            throw new NoSuchAccessTokenException(id);
        }
    }

    @RequestMapping(value = URL_ACCESS_TOKEN_GET_ALL, method = RequestMethod.GET)
    @ResponseBody
    public List<AccessToken> getAllTokens() throws ProfileException {
        List<AccessToken> tokens = accessTokenService.getAllTokens();
        if (tokens != null) {
            return tokens;
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = URL_ACCESS_TOKEN_DELETE, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void deleteToken(@PathVariable(PATH_VAR_ID) String id) throws ProfileException {
        accessTokenService.deleteToken(id);
    }

}
