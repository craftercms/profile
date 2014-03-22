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
package org.craftercms.profile.v2.services.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.crypto.SimpleDigest;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.permissions.annotations.HasPermission;
import org.craftercms.commons.security.permissions.annotations.SecuredObject;
import org.craftercms.profile.api.*;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.api.utils.SortOrder;
import org.craftercms.profile.v2.exceptions.*;
import org.craftercms.profile.v2.repositories.ProfileRepository;
import org.craftercms.profile.v2.repositories.TenantRepository;
import org.craftercms.profile.v2.repositories.TicketRepository;
import org.craftercms.profile.v2.services.VerificationService;
import org.craftercms.profile.v2.utils.EmailUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of {@link org.craftercms.profile.api.services.ProfileService}.
 *
 * @author avasquez
 */
@HasPermission(type = TenantPermission.class, action = TenantActions.MANAGE_PROFILES)
public class ProfileServiceImpl implements ProfileService {

    public static final String PASSWORD_SEP = "|";

    public static final String ERROR_KEY_CREATE_PROFILE_ERROR =             "profile.profile.createProfileError";
    public static final String ERROR_KEY_GET_PROFILE_ERROR =                "profile.profile.getProfileError";
    public static final String ERROR_KEY_UPDATE_PROFILE_ERROR =             "profile.profile.updateProfileError";
    public static final String ERROR_KEY_DELETE_PROFILE_ERROR =             "profile.profile.deleteProfileError";
    public static final String ERROR_KEY_GET_PROFILE_BY_USERNAME_ERROR =    "profile.profile.getProfileByUsernameError";
    public static final String ERROR_KEY_GET_PROFILE_BY_TICKET_ERROR =      "profile.profile.getProfileByTicketError";
    public static final String ERROR_KEY_GET_PROFILE_COUNT_ERROR =          "profile.profile.getProfileCountError";

    protected ProfileRepository profileRepository;
    protected TenantRepository tenantRepository;
    protected TicketRepository ticketRepository;
    protected VerificationService verificationService;

    @Required
    public void setProfileRepository(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Required
    public void setTenantRepository(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Required
    public void setTicketRepository(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Required
    public void setVerificationService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @Override
    public Profile createProfile(@SecuredObject String tenantName, String username, String password, String email,
                                 boolean enabled, Set<String> roles, String verificationBaseUrl)
            throws ProfileException {
        if (!EmailUtils.validateEmail(email)) {
            throw new InvalidEmailAddressException(email);
        }

        Date now = new Date();

        Profile profile = new Profile();
        profile.setTenant(tenantName);
        profile.setUsername(username);
        profile.setPassword(hashPassword(password));
        profile.setEmail(email);
        profile.setCreated(now);
        profile.setModified(now);
        profile.setRoles(roles);
        profile.setVerified(false);

        Tenant tenant = getTenant(tenantName);
        boolean emailNewProfiles = tenant.isVerifyNewProfiles();

        if (emailNewProfiles) {
            profile.setEnabled(false);
        } else {
            profile.setEnabled(enabled);
        }

        try {
            profileRepository.save(profile);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_PROFILE_ERROR, e, username, tenantName);
        }

        if (emailNewProfiles) {
            verificationService.sendVerificationEmail(profile, verificationBaseUrl);
        }

        return profile;
    }

    @Override
    public void updateProfile(@SecuredObject final String tenantName, final String username, final String profileId,
                                 final String password, final String email, final Boolean enabled,
                                 final Set<String> roles) throws ProfileException {
        if (StringUtils.isNotEmpty(email) && !EmailUtils.validateEmail(email)) {
            throw new InvalidEmailAddressException(email);
        }

        updateProfile(tenantName, profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                if (StringUtils.isNotEmpty(tenantName)) {
                    profile.setTenant(tenantName);
                }
                if (StringUtils.isNotEmpty(username)) {
                    profile.setUsername(username);
                }
                if (StringUtils.isNotEmpty(password)) {
                    profile.setPassword(hashPassword(password));
                }
                if (StringUtils.isNotEmpty(email)) {
                    profile.setEmail(email);
                }
                if (enabled != null) {
                    profile.setEnabled(enabled);
                }
                if (CollectionUtils.isNotEmpty(roles)) {
                    profile.setRoles(roles);
                }
            }

        });
    }

    @Override
    public void enableProfile(@SecuredObject String tenantName, String profileId) throws ProfileException {
        updateProfile(tenantName, profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.setEnabled(true);
            }

        });
    }

    @Override
    public void disableProfile(@SecuredObject String tenantName, String profileId) throws ProfileException {
        updateProfile(tenantName, profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.setEnabled(false);
            }

        });
    }

    @Override
    public void addRoles(@SecuredObject String tenantName, String profileId, final Set<String> roles)
            throws ProfileException {
        updateProfile(tenantName, profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.getRoles().addAll(roles);
            }

        });
    }

    @Override
    public void removeRoles(@SecuredObject String tenantName, String profileId, final Set<String> roles)
            throws ProfileException {
        updateProfile(tenantName, profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.getRoles().removeAll(roles);
            }

        });
    }

    @Override
    public Map<String, Object> getAttributes(@SecuredObject String tenantName, String profileId,
                                             String... attributesToReturn) throws ProfileException {
        Map<String, Object> attributes = getNonNullProfile(tenantName, profileId).getAttributes();
        if (MapUtils.isNotEmpty(attributes) && ArrayUtils.isNotEmpty(attributesToReturn)) {
            for (String attributeName : attributesToReturn) {
                attributes.remove(attributeName);
            }
        }

        return attributes;
    }

    @Override
    public void updateAttributes(@SecuredObject String tenantName, String profileId,
                                 final Map<String, Object> attributes) throws ProfileException {
        updateProfile(tenantName, profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.setAttributes(org.craftercms.commons.collections.MapUtils.deepMerge(
                        profile.getAttributes(), attributes));
            }

        });
    }

    @Override
    public void deleteAttributes(@SecuredObject String tenantName, String profileId,
                                 final String... attributeNames) throws ProfileException {
        updateProfile(tenantName, profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                Map<String, Object> attributes = profile.getAttributes();
                if (MapUtils.isNotEmpty(attributes) && ArrayUtils.isNotEmpty(attributeNames)) {
                    for (String attributeName : attributeNames) {
                        attributes.remove(attributeName);
                    }
                }
            }

        });
    }

    @Override
    public void deleteProfile(@SecuredObject String tenantName, String profileId) throws ProfileException {
        try {
            profileRepository.removeById(profileId);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_DELETE_PROFILE_ERROR, e, profileId, tenantName);
        }
    }

    @Override
    public Profile getProfile(@SecuredObject String tenantName, String profileId, String... attributesToReturn)
            throws ProfileException {
        try {
            return profileRepository.findById(profileId, attributesToReturn);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_ERROR, e, profileId, tenantName);
        }
    }

    @Override
    public Profile getProfileByUsername(@SecuredObject String tenantName, String username,
                                        String... attributesToReturn) throws ProfileException {
        try {
            return profileRepository.findByTenantAndUsername(tenantName, username, attributesToReturn);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_BY_USERNAME_ERROR, username, tenantName);
        }
    }

    @Override
    public Profile getProfileByTicket(@SecuredObject String tenantName, String ticket,
                                      String... attributesToReturn) throws ProfileException {
        try {
            Ticket ticketObj = ticketRepository.findById(ticket);
            if (ticketObj != null) {
                return profileRepository.findById(ticketObj.getUserId(), attributesToReturn);
            } else {
                throw new NoSuchTicketException(ticket);
            }
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_BY_TICKET_ERROR, ticket, tenantName);
        }
    }

    @Override
    public long getProfileCount(@SecuredObject String tenantName) throws ProfileException {
        try {
            return profileRepository.countByTenant(tenantName);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_COUNT_ERROR, tenantName);
        }
    }

    @Override
    public List<Profile> getProfiles(@SecuredObject String tenantName, List<String> profileIds, String sortBy,
                                     SortOrder sortOrder, String... attributesToReturn) {
        return null;
    }

    @Override
    public List<Profile> getProfileRange(@SecuredObject String tenantName, String sortBy, String sortOrder,
                                         Integer start, Integer count, String... attributesToReturn) {
        return null;
    }

    @Override
    public List<Profile> getProfilesByRole(@SecuredObject String tenantName, String role, String sortBy,
                                           SortOrder sortOrder, Integer start, Integer count,
                                           String... attributesToReturn) {
        return null;
    }

    @Override
    public void forgotPassword(@SecuredObject String tenantName, String profileId, String changePasswordUrl) {

    }

    @Override
    public void resetPassword(@SecuredObject String tenantName, String resetToken, String newPassword) {

    }

    protected Profile getNonNullProfile(String tenantName, String id) throws ProfileException {
        Profile profile = getProfile(tenantName, id);
        if (profile != null) {
            return profile;
        } else {
            throw new NoSuchProfileException(id);
        }
    }

    protected Tenant getTenant(String name) throws I10nProfileException {
        try {
            Tenant tenant = tenantRepository.findByName(name);
            if (tenant != null) {
                return tenant;
            } else {
                throw new NoSuchTenantException(name);
            }
        } catch (MongoDataException e) {
            throw new I10nProfileException(TenantServiceImpl.ERROR_KEY_GET_TENANT_ERROR, name);
        }
    }

    protected String hashPassword(String clearPswd) {
        SimpleDigest digest = new SimpleDigest();
        String hashedPswd = digest.digestBase64(clearPswd);

        return hashedPswd + PASSWORD_SEP + digest.getBase64Salt();
    }

    protected void updateProfile(String tenantName, String profileId, UpdateCallback callback) throws ProfileException {
        Profile profile = getNonNullProfile(tenantName, profileId);

        callback.doWithProfile(profile);

        try {
            profileRepository.save(profile);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_UPDATE_PROFILE_ERROR, e, profileId, tenantName);
        }
    }

    protected interface UpdateCallback {

        void doWithProfile(Profile profile) throws ProfileException;

    }

}
