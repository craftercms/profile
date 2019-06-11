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

package org.craftercms.profile.entitlement;

import java.util.Arrays;
import java.util.List;

import org.craftercms.commons.entitlements.exception.UnsupportedEntitlementException;
import org.craftercms.commons.entitlements.model.EntitlementType;
import org.craftercms.commons.entitlements.model.Module;
import org.craftercms.commons.entitlements.usage.EntitlementUsageProvider;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Required;

import static org.craftercms.commons.entitlements.model.Module.PROFILE;

/**
 * Implementation of {@link EntitlementUsageProvider} for Crafter Profile module.
 *
 * @author joseross
 */
public class ProfileLicenseUsageProvider implements EntitlementUsageProvider {

    /**
     * Current instance of {@link TenantRepository}.
     */
    protected TenantRepository tenantRepository;

    /**
     * Current instance of {@link ProfileRepository}.
     */
    protected ProfileRepository profileRepository;

    @Required
    public void setTenantRepository(final TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Required
    public void setProfileRepository(final ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Module getModule() {
        return PROFILE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EntitlementType> getSupportedEntitlements() {
        return Arrays.asList(EntitlementType.SITE, EntitlementType.USER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doGetEntitlementUsage(final EntitlementType type) throws Exception {
        switch (type) {
            case SITE:
                return (int) tenantRepository.count();
            case USER:
                return (int) profileRepository.count();
            default:
                throw new UnsupportedEntitlementException(PROFILE, type);
        }
    }

}
