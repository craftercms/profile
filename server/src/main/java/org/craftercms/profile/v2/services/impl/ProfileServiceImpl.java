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
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.api.utils.SortOrder;
import org.craftercms.profile.v2.exceptions.*;
import org.craftercms.profile.v2.repositories.ProfileRepository;
import org.craftercms.profile.v2.repositories.TicketRepository;
import org.craftercms.profile.v2.services.VerificationService;
import org.craftercms.profile.v2.services.VerificationSuccessCallback;
import org.craftercms.profile.v2.utils.EmailUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

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
    public static final String ERROR_KEY_GET_PROFILES_ERROR =               "profile.profile.getProfilesError";
    public static final String ERROR_KEY_GET_PROFILE_RANGE_ERROR =          "profile.profile.getProfileRangeError";
    public static final String ERROR_KEY_GET_PROFILES_BY_ROLE_ERROR =       "profile.profile.getProfilesByRoleError";
    public static final String ERROR_KEY_GET_PROFILES_BY_ATTRIB_ERROR =     "profile.profile.getProfilesByAttributeError";
    public static final String ERROR_KEY_PROFILE_UPDATE_ERROR =             "profile.verification.profileUpdateError";
    public static final String ERROR_KEY_RESET_PASSWORD_ERROR =             "profile.profile.resetPasswordError";

    protected ProfileRepository profileRepository;
    protected TenantService tenantService;
    protected TicketRepository ticketRepository;
    protected VerificationService newProfileVerificationService;
    protected VerificationService resetPasswordVerificationService;

    @Required
    public void setProfileRepository(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Required
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Required
    public void setTicketRepository(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Required
    public void setNewProfileVerificationService(VerificationService newProfileVerificationService) {
        this.newProfileVerificationService = newProfileVerificationService;
    }

    @Required
    public void setResetPasswordVerificationService(VerificationService resetPasswordVerificationService) {
        this.resetPasswordVerificationService = resetPasswordVerificationService;
    }

    @Override
    public Profile createProfile(@SecuredObject String tenantName, String username, String password, String email,
                                 boolean enabled, Set<String> roles, String verificationUrl)
            throws ProfileException {
        if (!EmailUtils.validateEmail(email)) {
            throw new InvalidEmailAddressException(email);
        }

        try {
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

            profileRepository.save(profile);

            if (emailNewProfiles) {
                newProfileVerificationService.sendEmail(profile, verificationUrl);
            }

            return profile;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_PROFILE_ERROR, e, username, tenantName);
        }
    }

    @Override
    public Profile updateProfile(@SecuredObject final String tenantName, final String username, final String profileId,
                                 final String password, final String email, final Boolean enabled,
                                 final Set<String> roles, String... attributesToReturn) throws ProfileException {
        if (StringUtils.isNotEmpty(email) && !EmailUtils.validateEmail(email)) {
            throw new InvalidEmailAddressException(email);
        }

        return updateProfile(tenantName, profileId, new UpdateCallback() {

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

        }, attributesToReturn);
    }

    @Override
    public Profile verifyProfile(final String tenantName, String verificationTokenId,
                                 final String... attributesToReturn) throws ProfileException {
        VerificationSuccessCallback callback = new VerificationSuccessCallback() {

            @Override
            public Profile doOnSuccess(VerificationToken token) throws ProfileException {
                try {
                    Profile profile = getNonNullProfile(tenantName, token.getProfileId(), attributesToReturn);
                    profile.setEnabled(true);
                    profile.setVerified(true);

                    profileRepository.save(profile);

                    return profile;
                } catch (MongoDataException e) {
                    throw new I10nProfileException(ERROR_KEY_PROFILE_UPDATE_ERROR, e);
                }
            }

        };

        return newProfileVerificationService.verifyToken(verificationTokenId, callback);
    }

    @Override
    public Profile enableProfile(@SecuredObject String tenantName, String profileId, String... attributesToReturn)
            throws ProfileException {
        return updateProfile(tenantName, profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.setEnabled(true);
            }

        }, attributesToReturn);
    }

    @Override
    public Profile disableProfile(@SecuredObject String tenantName, String profileId, String... attributesToReturn)
            throws ProfileException {
        return updateProfile(tenantName, profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.setEnabled(false);
            }

        }, attributesToReturn);
    }

    @Override
    public Profile addRoles(@SecuredObject String tenantName, String profileId, final Collection<String> roles,
                            String... attributesToReturn) throws ProfileException {
        return updateProfile(tenantName, profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.getRoles().addAll(roles);
            }

        }, attributesToReturn);
    }

    @Override
    public Profile removeRoles(@SecuredObject String tenantName, String profileId, final Collection<String> roles,
                               String... attributesToReturn) throws ProfileException {
        return updateProfile(tenantName, profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.getRoles().removeAll(roles);
            }

        }, attributesToReturn);
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
    public Profile updateAttributes(@SecuredObject String tenantName, String profileId,
                                    final Map<String, Object> attributes, String... attributesToReturn)
            throws ProfileException {
        return updateProfile(tenantName, profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.setAttributes(org.craftercms.commons.collections.MapUtils.deepMerge(
                        profile.getAttributes(), attributes));
            }

        }, attributesToReturn);
    }

    @Override
    public Profile removeAttributes(@SecuredObject String tenantName, String profileId,
                                    final Collection<String> attributeNames, String... attributesToReturn)
            throws ProfileException {
        return updateProfile(tenantName, profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                Map<String, Object> attributes = profile.getAttributes();

                for (String attributeName : attributeNames) {
                    attributes.remove(attributeName);
                }
            }

        }, attributesToReturn);
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
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_BY_USERNAME_ERROR, e, username, tenantName);
        }
    }

    @Override
    public Profile getProfileByTicket(@SecuredObject String tenantName, String ticketId,
                                      String... attributesToReturn) throws ProfileException {
        try {
            Ticket ticket = ticketRepository.findById(ticketId);
            if (ticket != null) {
                return profileRepository.findById(ticket.getUserId(), attributesToReturn);
            } else {
                throw new NoSuchTicketException(ticketId);
            }
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_BY_TICKET_ERROR, e, ticketId, tenantName);
        }
    }

    @Override
    public long getProfileCount(@SecuredObject String tenantName) throws ProfileException {
        try {
            return profileRepository.countByTenant(tenantName);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_COUNT_ERROR, e, tenantName);
        }
    }

    @Override
    public Iterable<Profile> getProfilesByIds(@SecuredObject String tenantName, List<String> profileIds, String sortBy,
                                              SortOrder sortOrder, String... attributesToReturn) throws ProfileException {
        try {
            return profileRepository.findByIds(profileIds, sortBy, sortOrder, attributesToReturn);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILES_ERROR, e, profileIds, tenantName);
        }
    }

    @Override
    public Iterable<Profile> getProfileRange(@SecuredObject String tenantName, String sortBy, SortOrder sortOrder,
                                             Integer start, Integer count, String... attributesToReturn)
            throws ProfileException {
        try {
            return profileRepository.findRange(tenantName, sortBy, sortOrder, start, count, attributesToReturn);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_RANGE_ERROR, e, start, count, tenantName);
        }
    }

    @Override
    public Iterable<Profile> getProfilesByRole(@SecuredObject String tenantName, String role, String sortBy,
                                               SortOrder sortOrder, String... attributesToReturn)
            throws ProfileException {
        try {
            return profileRepository.findByTenantAndRole(tenantName, role, sortBy, sortOrder, attributesToReturn);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILES_BY_ROLE_ERROR, e, role, tenantName);
        }
    }

    @Override
    public Iterable<Profile> getProfilesByAttribute(@SecuredObject String tenantName, String attributeName,
                                                    String attributeValue, String sortBy, SortOrder sortOrder,
                                                    String... attributesToReturn) throws ProfileException {
        try {
            return profileRepository.findByTenantAndAttribute(tenantName, attributeName, attributeValue, sortBy,
                    sortOrder, attributesToReturn);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILES_BY_ATTRIB_ERROR, e, attributeName, attributeValue,
                    tenantName);
        }
    }

    @Override
    public Profile forgotPassword(@SecuredObject String tenantName, String profileId, String resetPasswordUrl,
                                  String... attributesToReturn) throws ProfileException {
        Profile profile = getNonNullProfile(tenantName, profileId, attributesToReturn);

        resetPasswordVerificationService.sendEmail(profile, resetPasswordUrl);

        return profile;
    }

    @Override
    public Profile resetPassword(@SecuredObject final String tenantName, String resetTokenId, final String newPassword,
                                 final String... attributesToReturn) throws ProfileException {
        VerificationSuccessCallback callback = new VerificationSuccessCallback() {

            @Override
            public Profile doOnSuccess(VerificationToken token) throws ProfileException {
                try {
                    Profile profile = getNonNullProfile(tenantName, token.getProfileId(), attributesToReturn);
                    profile.setPassword(hashPassword(newPassword));

                    profileRepository.save(profile);

                    return profile;
                } catch (MongoDataException e) {
                    throw new I10nProfileException(ERROR_KEY_RESET_PASSWORD_ERROR, e);
                }
            }

        };

        return resetPasswordVerificationService.verifyToken(resetTokenId, callback);
    }

    protected Profile getNonNullProfile(String tenantName, String id, String... attributesToReturn)
            throws ProfileException {
        Profile profile = getProfile(tenantName, id, attributesToReturn);
        if (profile != null) {
            return profile;
        } else {
            throw new NoSuchProfileException(id);
        }
    }

    protected Tenant getTenant(String name) throws ProfileException {
        Tenant tenant = tenantService.getTenant(name);
        if (tenant != null) {
            return tenant;
        } else {
            throw new NoSuchTenantException(name);
        }
    }

    protected String hashPassword(String clearPswd) {
        SimpleDigest digest = new SimpleDigest();
        String hashedPswd = digest.digestBase64(clearPswd);

        return hashedPswd + PASSWORD_SEP + digest.getBase64Salt();
    }

    protected Profile updateProfile(String tenantName, String profileId, UpdateCallback callback,
                                    String... attributesToReturn)
            throws ProfileException {
        Profile profile = getNonNullProfile(tenantName, profileId, attributesToReturn);

        callback.doWithProfile(profile);

        try {
            profileRepository.save(profile);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_UPDATE_PROFILE_ERROR, e, profileId, tenantName);
        }

        return profile;
    }

    protected interface UpdateCallback {

        void doWithProfile(Profile profile) throws ProfileException;

    }

}
