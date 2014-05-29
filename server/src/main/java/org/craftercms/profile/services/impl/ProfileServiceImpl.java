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
package org.craftercms.profile.services.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.commons.crypto.CipherUtils;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.logging.Logged;
import org.craftercms.commons.mail.EmailUtils;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.*;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.exceptions.*;
import org.craftercms.profile.permissions.Application;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.services.VerificationService;
import org.craftercms.profile.services.VerificationSuccessCallback;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * Default implementation of {@link org.craftercms.profile.api.services.ProfileService}.
 *
 * @author avasquez
 */
@Logged
public class ProfileServiceImpl implements ProfileService {

    private static final I10nLogger logger = new I10nLogger(ProfileServiceImpl.class,
            "crafter.profile.messages.logging");

    public static final String LOG_KEY_PROFILE_CREATED =            "profile.profile.profileCreated";
    public static final String LOG_KEY_PROFILE_UPDATED =            "profile.profile.profileUpdated";
    public static final String LOG_KEY_PROFILE_VERIFIED =           "profile.profile.profileVerified";
    public static final String LOG_KEY_PROFILE_ENABLED =            "profile.profile.profileEnabled";
    public static final String LOG_KEY_PROFILE_DISABLED =           "profile.profile.profileDisabled";
    public static final String LOG_KEY_PROFILE_ROLES_ADDED =        "profile.profile.rolesAdded";
    public static final String LOG_KEY_PROFILE_ROLES_REMOVED =      "profile.profile.rolesRemoved";
    public static final String LOG_KEY_PROFILE_ATTRIBS_UPDATED =    "profile.profile.attributesUpdated";
    public static final String LOG_KEY_PROFILE_ATTRIBS_REMOVED =    "profile.profile.attributesRemoved";
    public static final String LOG_KEY_PROFILE_DELETED =            "profile.profile.profileDeleted";

    public static final String ERROR_KEY_CREATE_PROFILE_ERROR =             "profile.profile.createProfileError";
    public static final String ERROR_KEY_GET_PROFILE_ERROR =                "profile.profile.getProfileError";
    public static final String ERROR_KEY_UPDATE_PROFILE_ERROR =             "profile.profile.updateProfileError";
    public static final String ERROR_KEY_DELETE_PROFILE_ERROR =             "profile.profile.deleteProfileError";
    public static final String ERROR_KEY_GET_PROFILE_BY_USERNAME_ERROR =    "profile.profile.getProfileByUsernameError";
    public static final String ERROR_KEY_GET_PROFILE_COUNT_ERROR =          "profile.profile.getProfileCountError";
    public static final String ERROR_KEY_GET_PROFILES_ERROR =               "profile.profile.getProfilesError";
    public static final String ERROR_KEY_GET_PROFILE_RANGE_ERROR =          "profile.profile.getProfileRangeError";
    public static final String ERROR_KEY_GET_PROFILES_BY_ROLE_ERROR =       "profile.profile.getProfilesByRoleError";
    public static final String ERROR_KEY_GET_PROFILES_BY_EXISTING_ATTRIB_ERROR =
            "profile.profile.getProfilesByExistingAttributeError";
    public static final String ERROR_KEY_GET_PROFILES_BY_ATTRIB_VALUE_ERROR =
            "profile.profile.getProfilesByAttributeValueError";
    public static final String ERROR_KEY_RESET_PASSWORD_ERROR =             "profile.profile.resetPasswordError";

    protected PermissionEvaluator<Application, String> tenantPermissionEvaluator;
    protected PermissionEvaluator<Application, AttributeDefinition> attributePermissionEvaluator;
    protected ProfileRepository profileRepository;
    protected TenantService tenantService;
    protected AuthenticationService authenticationService;
    protected VerificationService newProfileVerificationService;
    protected VerificationService resetPasswordVerificationService;

    @Required
    public void setTenantPermissionEvaluator(PermissionEvaluator<Application, String> tenantPermissionEvaluator) {
        this.tenantPermissionEvaluator = tenantPermissionEvaluator;
    }

    @Required
    public void setAttributePermissionEvaluator(
            PermissionEvaluator<Application, AttributeDefinition> attributePermissionEvaluator) {
        this.attributePermissionEvaluator = attributePermissionEvaluator;
    }

    @Required
    public void setProfileRepository(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Required
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
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
    public Profile createProfile(String tenantName, String username, String password, String email, boolean enabled,
                                 Set<String> roles, String verificationUrl)
            throws ProfileException {
        checkIfManageProfilesIsAllowed(tenantName);

        if (!EmailUtils.validateEmail(email)) {
            throw new InvalidEmailAddressException(email);
        }

        try {
            Date now = new Date();

            Profile profile = new Profile();
            profile.setTenant(tenantName);
            profile.setUsername(username);
            profile.setPassword(CipherUtils.hashPassword(password));
            profile.setEmail(email);
            profile.setCreatedOn(now);
            profile.setLastModified(now);
            profile.setRoles(roles);
            profile.setVerified(false);

            Tenant tenant = getTenant(tenantName);
            boolean emailNewProfiles = tenant.isVerifyNewProfiles();

            if (!emailNewProfiles) {
                profile.setEnabled(enabled);
            }

            profileRepository.insert(profile);

            logger.debug(LOG_KEY_PROFILE_CREATED, profile);

            if (emailNewProfiles) {
                newProfileVerificationService.sendEmail(profile, verificationUrl);
            }

            return profile;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_PROFILE_ERROR, e, username, tenantName);
        }
    }

    @Override
    public Profile updateProfile(final String profileId, final String username, final String password,
                                 final String email, final Boolean enabled, final Set<String> roles,
                                 String... attributesToReturn) throws ProfileException {
        if (StringUtils.isNotEmpty(email) && !EmailUtils.validateEmail(email)) {
            throw new InvalidEmailAddressException(email);
        }

        Profile profile = updateProfile(profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                if (StringUtils.isNotEmpty(username)) {
                    profile.setUsername(username);
                }
                if (StringUtils.isNotEmpty(password)) {
                    profile.setPassword(CipherUtils.hashPassword(password));
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

        logger.debug(LOG_KEY_PROFILE_UPDATED, profile);

        return profile;
    }

    @Override
    public Profile verifyProfile(String verificationTokenId, final String... attributesToReturn)
            throws ProfileException {
        VerificationSuccessCallback callback = new VerificationSuccessCallback() {

            @Override
            public Profile doOnSuccess(VerificationToken token) throws ProfileException {
                try {
                    Profile profile = getNonNullProfile(token.getProfileId(), attributesToReturn);
                    profile.setEnabled(true);
                    profile.setVerified(true);
                    profile.setLastModified(new Date());

                    profileRepository.save(profile);

                    return profile;
                } catch (MongoDataException e) {
                    throw new I10nProfileException(ERROR_KEY_UPDATE_PROFILE_ERROR, e, token.getProfileId());
                }
            }

        };

        Profile profile = newProfileVerificationService.verifyToken(verificationTokenId, callback);

        logger.debug(LOG_KEY_PROFILE_VERIFIED, profile.getId());

        return profile;
    }

    @Override
    public Profile enableProfile(String profileId, String... attributesToReturn)
            throws ProfileException {
        Profile profile = updateProfile(profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.setEnabled(true);
            }

        }, attributesToReturn);

        logger.debug(LOG_KEY_PROFILE_ENABLED, profileId);

        return profile;
    }

    @Override
    public Profile disableProfile(String profileId, String... attributesToReturn)
            throws ProfileException {
        Profile profile = updateProfile(profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.setEnabled(false);
            }

        }, attributesToReturn);

        logger.debug(LOG_KEY_PROFILE_DISABLED, profileId);

        return profile;
    }

    @Override
    public Profile addRoles(String profileId, final Collection<String> roles, String... attributesToReturn)
            throws ProfileException {
        Profile profile = updateProfile(profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.getRoles().addAll(roles);
            }

        }, attributesToReturn);

        logger.debug(LOG_KEY_PROFILE_ROLES_ADDED, roles, profileId);

        return profile;
    }

    @Override
    public Profile removeRoles(String profileId, final Collection<String> roles, String... attributesToReturn)
            throws ProfileException {
        Profile profile = updateProfile(profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                profile.getRoles().removeAll(roles);
            }

        }, attributesToReturn);

        logger.debug(LOG_KEY_PROFILE_ROLES_REMOVED, roles, profileId);

        return profile;
    }

    @Override
    public Map<String, Object> getAttributes(String profileId, String... attributesToReturn) throws ProfileException {
        return getNonNullProfile(profileId, attributesToReturn).getAttributes();
    }

    @Override
    public Profile updateAttributes(String profileId, final Map<String, Object> attributes,
                                    String... attributesToReturn) throws ProfileException {
        Profile profile = updateProfile(profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                rejectAttributesIfActionNotAllowed(profile, attributes.keySet(), AttributeAction.WRITE_ATTRIBUTE);

                profile.getAttributes().putAll(attributes);
            }

        }, attributesToReturn);

        if (logger.isDebugEnabled()) {
            logger.debug(LOG_KEY_PROFILE_ATTRIBS_UPDATED, attributes.keySet(), profileId);
        }

        return profile;
    }

    @Override
    public Profile removeAttributes(String profileId, final Collection<String> attributeNames,
                                    String... attributesToReturn) throws ProfileException {
        Profile profile = updateProfile(profileId, new UpdateCallback() {

            @Override
            public void doWithProfile(Profile profile) throws ProfileException {
                rejectAttributesIfActionNotAllowed(profile, attributeNames, AttributeAction.REMOVE_ATTRIBUTE);

                Map<String, Object> attributes = profile.getAttributes();

                for (String attributeName : attributeNames) {
                    attributes.remove(attributeName);
                }
            }

        }, attributesToReturn);

        logger.debug(LOG_KEY_PROFILE_ATTRIBS_REMOVED, attributeNames, profileId);

        return profile;
    }

    @Override
    public void deleteProfile(String profileId) throws ProfileException {
        try {
            Profile profile = getProfile(profileId);
            if (profile != null) {
                profileRepository.removeById(profileId);
            }

            logger.debug(LOG_KEY_PROFILE_DELETED, profileId);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_DELETE_PROFILE_ERROR, e, profileId);
        }
    }

    @Override
    public Profile getProfile(String profileId, String... attributesToReturn) throws ProfileException {
        try {
            Profile profile = profileRepository.findById(profileId, attributesToReturn);
            if (profile != null) {
                checkIfManageProfilesIsAllowed(profile.getTenant());
                filterNonReadableAttributes(profile);
            }

            return profile;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_ERROR, e, profileId);
        }
    }

    @Override
    public Profile getProfileByUsername(String tenantName, String username, String... attributesToReturn)
            throws ProfileException {
        checkIfManageProfilesIsAllowed(tenantName);

        try {
            Profile profile = profileRepository.findByTenantAndUsername(tenantName, username, attributesToReturn);
            filterNonReadableAttributes(profile);

            return profile;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_BY_USERNAME_ERROR, e, username, tenantName);
        }
    }

    @Override
    public Profile getProfileByTicket(String ticketId, String... attributesToReturn) throws ProfileException {
        Ticket ticket = authenticationService.getTicket(ticketId);
        if (ticket != null) {
            return getProfile(ticket.getProfileId(), attributesToReturn);
        } else {
            throw new NoSuchTicketException(ticketId);
        }
    }

    @Override
    public long getProfileCount(String tenantName) throws ProfileException {
        checkIfManageProfilesIsAllowed(tenantName);

        try {
            return profileRepository.countByTenant(tenantName);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_COUNT_ERROR, e, tenantName);
        }
    }

    @Override
    public List<Profile> getProfilesByIds(List<String> profileIds, String sortBy, SortOrder sortOrder,
                                          String... attributesToReturn) throws ProfileException {
        try {
            List<Profile> profiles = IterableUtils.toList(profileRepository.findByIds(profileIds, sortBy, sortOrder,
                    attributesToReturn));
            if (profiles != null) {
                for (Profile profile : profiles) {
                    checkIfManageProfilesIsAllowed(profile.getTenant());
                    filterNonReadableAttributes(profile);
                }
            }

            return profiles;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILES_ERROR, e, profileIds);
        }
    }

    @Override
    public List<Profile> getProfileRange(String tenantName, String sortBy, SortOrder sortOrder, Integer start,
                                         Integer count, String... attributesToReturn) throws ProfileException {
        checkIfManageProfilesIsAllowed(tenantName);

        try {
            List<Profile> profiles = IterableUtils.toList(profileRepository.findRange(tenantName, sortBy, sortOrder,
                    start, count, attributesToReturn));
            filterNonReadableAttributes(profiles);

            return profiles;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_RANGE_ERROR, e, start, count, tenantName);
        }
    }

    @Override
    public List<Profile> getProfilesByRole(String tenantName, String role, String sortBy, SortOrder sortOrder,
                                           String... attributesToReturn) throws ProfileException {
        checkIfManageProfilesIsAllowed(tenantName);

        try {
            List<Profile> profiles = IterableUtils.toList(profileRepository.findByTenantAndRole(tenantName, role,
                    sortBy, sortOrder, attributesToReturn));
            filterNonReadableAttributes(profiles);

            return profiles;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILES_BY_ROLE_ERROR, e, role, tenantName);
        }
    }

    @Override
    public List<Profile> getProfilesByExistingAttribute(String tenantName, String attributeName, String sortBy,
                                                        SortOrder sortOrder, String... attributesToReturn)
            throws ProfileException {
        checkIfManageProfilesIsAllowed(tenantName);

        try {
            List<Profile> profiles = IterableUtils.toList(profileRepository.findByTenantAndExistingAttribute(
                    tenantName, attributeName, sortBy, sortOrder, attributesToReturn));
            filterNonReadableAttributes(profiles);

            return profiles;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILES_BY_EXISTING_ATTRIB_ERROR, e, attributeName,
                    tenantName);
        }
    }

    @Override
    public List<Profile> getProfilesByAttributeValue(String tenantName, String attributeName, String attributeValue,
                                                     String sortBy, SortOrder sortOrder, String... attributesToReturn)
            throws ProfileException {
        checkIfManageProfilesIsAllowed(tenantName);

        try {
            List<Profile> profiles = IterableUtils.toList(profileRepository.findByTenantAndAttributeValue(tenantName,
                    attributeName, attributeValue, sortBy, sortOrder, attributesToReturn));
            filterNonReadableAttributes(profiles);

            return profiles;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILES_BY_ATTRIB_VALUE_ERROR, e, attributeName,
                    attributeValue, tenantName);
        }
    }

    @Override
    public Profile forgotPassword(String profileId, String resetPasswordUrl, String... attributesToReturn)
            throws ProfileException {
        Profile profile = getNonNullProfile(profileId, attributesToReturn);

        resetPasswordVerificationService.sendEmail(profile, resetPasswordUrl);

        return profile;
    }

    @Override
    public Profile resetPassword(String resetTokenId, final String newPassword, final String... attributesToReturn)
            throws ProfileException {
        VerificationSuccessCallback callback = new VerificationSuccessCallback() {

            @Override
            public Profile doOnSuccess(VerificationToken token) throws ProfileException {
                try {
                    Profile profile = getNonNullProfile(token.getProfileId(), attributesToReturn);
                    profile.setPassword(CipherUtils.hashPassword(newPassword));
                    profile.setLastModified(new Date());

                    profileRepository.save(profile);

                    return profile;
                } catch (MongoDataException e) {
                    throw new I10nProfileException(ERROR_KEY_RESET_PASSWORD_ERROR, e);
                }
            }

        };

        return resetPasswordVerificationService.verifyToken(resetTokenId, callback);
    }

    protected void checkIfManageProfilesIsAllowed(String tenantName) {
        if (!tenantPermissionEvaluator.isAllowed(tenantName, TenantAction.MANAGE_PROFILES.toString())) {
            throw new ActionDeniedException(TenantAction.MANAGE_PROFILES.toString(), "tenant \"" + tenantName + "\"");
        }
    }

    protected Profile getNonNullProfile(String id, String... attributesToReturn) throws ProfileException {
        Profile profile = getProfile(id, attributesToReturn);
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

    protected Profile updateProfile(String profileId, UpdateCallback callback, String... attributesToReturn)
            throws ProfileException {
        Profile profile = getNonNullProfile(profileId, attributesToReturn);

        callback.doWithProfile(profile);

        profile.setLastModified(new Date());

        try {
            profileRepository.save(profile);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_UPDATE_PROFILE_ERROR, e, profileId);
        }

        return profile;
    }

    protected void filterNonReadableAttributes(Profile profile) throws ProfileException {
        if (profile != null) {
            Tenant tenant = getTenant(profile.getTenant());
            Set<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();
            Iterator<String> attributeNamesIter = profile.getAttributes().keySet().iterator();

            while (attributeNamesIter.hasNext()) {
                filterAttributeIfReadNotAllowed(tenant, attributeNamesIter, attributeDefinitions);
            }
        }
    }

    protected void filterNonReadableAttributes(Iterable<Profile> profiles) throws ProfileException {
        if (profiles != null) {
            for (Profile profile : profiles) {
                filterNonReadableAttributes(profile);
            }
        }
    }

    protected void rejectAttributesIfActionNotAllowed(Profile profile, Collection<String> attributeNames,
                                                      AttributeAction action) throws ProfileException {
        if (profile != null) {
            Tenant tenant = getTenant(profile.getTenant());
            Set<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();

            for (String attributeName : attributeNames) {
                rejectAttributeIfActionNotAllowed(tenant, attributeName, action, attributeDefinitions);
            }
        }
    }

    protected void filterAttributeIfReadNotAllowed(Tenant tenant, Iterator<String> attributeNamesIter,
                                                   Set<AttributeDefinition> attributeDefinitions)
            throws PermissionException, AttributeNotDefinedException {
        String tenantName = tenant.getName();
        String attributeName = attributeNamesIter.next();
        AttributeDefinition definition = findAttributeDefinition(attributeDefinitions, attributeName);

        if (definition != null) {
            if (!attributePermissionEvaluator.isAllowed(definition, AttributeAction.READ_ATTRIBUTE.toString())) {
                attributeNamesIter.remove();
            }
        } else {
            throw new AttributeNotDefinedException(attributeName, tenantName);
        }
    }

    protected void rejectAttributeIfActionNotAllowed(Tenant tenant, String attributeName, AttributeAction action,
                                                     Set<AttributeDefinition> attributeDefinitions)
            throws PermissionException, AttributeNotDefinedException {
        AttributeDefinition definition = findAttributeDefinition(attributeDefinitions, attributeName);
        if (definition != null) {
            if (!attributePermissionEvaluator.isAllowed(definition, action.toString())) {
                throw new ActionDeniedException(action.toString(), "attribute \"" + attributeName + "\"");
            }
        } else {
            throw new AttributeNotDefinedException(attributeName, tenant.getName());
        }
    }

    protected AttributeDefinition findAttributeDefinition(final Set<AttributeDefinition> attributeDefinitions,
                                                          final String name) {
        return CollectionUtils.find(attributeDefinitions, new Predicate<AttributeDefinition>() {

            @Override
            public boolean evaluate(AttributeDefinition definition) {
                return definition.getName().equals(name);
            }

        });
    }

    protected interface UpdateCallback {

        void doWithProfile(Profile profile) throws ProfileException;

    }

}
