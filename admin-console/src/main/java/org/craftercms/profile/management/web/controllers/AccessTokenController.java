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

package org.craftercms.profile.management.web.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AccessTokenService;
import org.craftercms.profile.management.exceptions.ResourceNotFoundException;
import org.craftercms.profile.management.security.AuthorizationUtils;
import org.craftercms.profile.management.security.permissions.Action;
import org.craftercms.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * MVC Controller for displaying and modifying tenants.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(AccessTokenController.BASE_URL_ACCESS_TOKEN)
public class AccessTokenController {

    public static final String BASE_URL_ACCESS_TOKEN = "/access_token";

    public static final String PATH_VAR_ID = "id";

    public static final String URL_VIEW_ACCESS_TOKEN_LIST = "/list/view";
    public static final String URL_VIEW_NEW_ACCESS_TOKEN = "/new/view";
    public static final String URL_VIEW_ACCESS_TOKEN = "/view";

    public static final String URL_GET_ALL_ACCESS_TOKENS = "/all";
    public static final String URL_GET_ACCESS_TOKEN = "/{" + PATH_VAR_ID + "}";
    public static final String URL_CREATE_ACCESS_TOKEN = "/create";
    public static final String URL_DELETE_ACCESS_TOKEN = "/{" + PATH_VAR_ID + "}/delete";

    public static final String VIEW_ACCESS_TOKEN_LIST = "access-token-list";
    public static final String VIEW_NEW_ACCESS_TOKEN = "new-access-token";
    public static final String VIEW_ACCESS_TOKEN = "access-token";

    public static final String MODEL_MESSAGE = "message";

    public static final String MSG_ACCESS_TOKEN_CREATED_FORMAT = "Access token '%s' created";
    public static final String MSG_ACCESS_TOKEN_DELETED_FORMAT = "Access token '%s' deleted";

    private AccessTokenService accessTokenService;

    @Required
    public void setAccessTokenService(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @RequestMapping(value = URL_VIEW_ACCESS_TOKEN_LIST, method = RequestMethod.GET)
    public String viewAccessTokenList() throws ProfileException {
        return VIEW_ACCESS_TOKEN_LIST;
    }

    @RequestMapping(value = URL_VIEW_NEW_ACCESS_TOKEN, method = RequestMethod.GET)
    public String viewNewAccessToken() throws ProfileException {
        return VIEW_NEW_ACCESS_TOKEN;
    }

    @RequestMapping(value = URL_VIEW_ACCESS_TOKEN, method = RequestMethod.GET)
    public String viewAccessToken() throws ProfileException {
        return VIEW_ACCESS_TOKEN;
    }

    @RequestMapping(value = URL_GET_ALL_ACCESS_TOKENS, method = RequestMethod.GET)
    @ResponseBody
    public List<AccessToken> getAllAccessTokens() throws ProfileException {
        checkIfAllowed(null, Action.GET_ALL_ACCESS_TOKENS);

        return accessTokenService.getAllTokens();
    }

    @RequestMapping(value = URL_GET_ACCESS_TOKEN, method = RequestMethod.GET)
    @ResponseBody
    public AccessToken getAccessToken(@PathVariable(PATH_VAR_ID) String id) throws ProfileException {
        checkIfAllowed(id, Action.GET_PROFILE);

        AccessToken token = accessTokenService.getToken(id);
        if (token != null) {
            return token;
        } else {
            throw new ResourceNotFoundException("No access token found with ID '" + id + "'");
        }
    }

    @RequestMapping(value = URL_CREATE_ACCESS_TOKEN, method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> createAccessToken(@RequestBody AccessToken token) throws ProfileException {
        checkIfAllowed(null, Action.CREATE_ACCESS_TOKEN);

        token = accessTokenService.createToken(token);

        return Collections.singletonMap(MODEL_MESSAGE, String.format(MSG_ACCESS_TOKEN_CREATED_FORMAT, token.getId()));
    }

    @RequestMapping(value = URL_DELETE_ACCESS_TOKEN, method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> deleteAccessToken(@PathVariable(PATH_VAR_ID) String id) throws ProfileException {
        checkIfAllowed(id, Action.DELETE_ACCESS_TOKEN);

        accessTokenService.deleteToken(id);

        return Collections.singletonMap(MODEL_MESSAGE, String.format(MSG_ACCESS_TOKEN_DELETED_FORMAT, id));
    }

    private void checkIfAllowed(String tokenId, Action action) throws ActionDeniedException {
        if (!AuthorizationUtils.isSuperadmin(SecurityUtils.getCurrentProfile())) {
            if (tokenId != null) {
                throw new ActionDeniedException(action.toString(), tokenId);
            } else {
                throw new ActionDeniedException(action.toString());
            }
        }
    }

}
