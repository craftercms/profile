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

package org.craftercms.profile.management.web.controllers;

import org.craftercms.profile.api.Profile;
import org.craftercms.security.utils.SecurityUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * {@link org.springframework.web.bind.annotation.ControllerAdvice} that defines common model attributes for all
 * view controllers.
 *
 * @author avasquez
 */
@ControllerAdvice
public class ModelAttributes {

    public static final String MODEL_LOGGED_IN_USER = "loggedInUser";

    @ModelAttribute(MODEL_LOGGED_IN_USER)
    public Profile getLoggedInUser() {
        return SecurityUtils.getCurrentProfile();
    }

}
