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
package org.craftercms.profile.management.web.model;

import org.craftercms.profile.api.Profile;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Form-backing bean for {@link org.craftercms.profile.api.Profile}s.
 *
 * @author avasquez
 */
public class ProfileForm extends Profile {

    public static final String DATE_TIME_DISPLAY_FORMAT = "MM/dd/yyyy HH:mm:ss";

    private String confirmPassword;

    public ProfileForm() {
    }

    /**
     * Copy fields from existing profile. The password is never copied, since it's not displayed.
     */
    public ProfileForm(Profile profile) {
        setId(profile.getId());
        setUsername(profile.getUsername());
        setEmail(profile.getEmail());
        setVerified(profile.isVerified());
        setEnabled(profile.isEnabled());
        setCreatedOn(profile.getCreatedOn());
        setLastModified(profile.getLastModified());
        setTenant(profile.getTenant());
        setRoles(profile.getRoles());
        setAttributes(profile.getAttributes());
    }

    @Override
    @DateTimeFormat(pattern = DATE_TIME_DISPLAY_FORMAT)
    public Date getCreatedOn() {
        return super.getCreatedOn();
    }

    @Override
    @DateTimeFormat(pattern = DATE_TIME_DISPLAY_FORMAT)
    public Date getLastModified() {
        return super.getLastModified();
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
