/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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

package org.craftercms.profile.controllers.rest;

import org.craftercms.commons.monitoring.rest.MonitoringRestControllerBase;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller to provide monitoring information
 * @author joseross
 */
@RestController
@RequestMapping(MonitoringController.URL_ROOT)
public class MonitoringController extends MonitoringRestControllerBase {

    public final static String URL_ROOT = "/api/1";

    protected String authorizationToken;

    public MonitoringController(final String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    @Override
    public String getConfiguredToken() {
        return authorizationToken;
    }

}
