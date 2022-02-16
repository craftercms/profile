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
package org.craftercms.security.utils.profile;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationCache;
import org.craftercms.security.authentication.impl.DefaultAuthentication;
import org.craftercms.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Aspect for {@link org.craftercms.profile.api.services.ProfileService} that refreshes the current authentication
 * object when the profile associated to the authentication is updated.
 *
 * @author avasquez
 */
@Aspect
public class RefreshCurrentAuthenticationOnProfileUpdateAspect {

    protected AuthenticationCache authenticationCache;

    @Required
    public void setAuthenticationCache(AuthenticationCache authenticationCache) {
        this.authenticationCache = authenticationCache;
    }

    @AfterReturning(value = "execution(* org.craftercms.profile.api.services.ProfileService.updateProfile(..)) || " +
            "execution(* org.craftercms.profile.api.services.ProfileService.verifyProfile(..)) || " +
            "execution(* org.craftercms.profile.api.services.ProfileService.enableProfile(..)) || " +
            "execution(* org.craftercms.profile.api.services.ProfileService.disableProfile(..)) || " +
            "execution(* org.craftercms.profile.api.services.ProfileService.addRoles(..)) || " +
            "execution(* org.craftercms.profile.api.services.ProfileService.removeRoles(..)) || " +
            "execution(* org.craftercms.profile.api.services.ProfileService.updateAttributes(..)) || " +
            "execution(* org.craftercms.profile.api.services.ProfileService.removeAttributes(..)) || " +
            "execution(* org.craftercms.profile.api.services.ProfileService.changePassword(..))",
            returning = "updatedProfile")
    public void refreshCurrentAuthentication(Profile updatedProfile) {
        Authentication auth = SecurityUtils.getCurrentAuthentication();

        if (auth != null) {
            Profile profile = auth.getProfile();

            if (profile.equals(updatedProfile)) {
                String ticket = auth.getTicket();
                auth = new DefaultAuthentication(ticket, updatedProfile);

                // Put updated authentication in cache
                authenticationCache.putAuthentication(auth);

                // Update current authentication object
                SecurityUtils.setCurrentAuthentication(auth);
            }
        }
    }

}
