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
package org.craftercms.profile.services.impl;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.commons.crypto.CryptoUtils;
import org.craftercms.commons.entitlements.model.EntitlementType;
import org.craftercms.commons.entitlements.validator.EntitlementValidator;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.logging.Logged;
import org.craftercms.commons.mail.EmailUtils;
import org.craftercms.commons.mongo.DuplicateKeyException;
import org.craftercms.commons.mongo.FileInfo;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.mongo.UpdateHelper;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.AttributeAction;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.TenantAction;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.VerificationToken;
import org.craftercms.profile.api.exceptions.I10nProfileException;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.api.services.ProfileAttachment;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.exceptions.AttributeNotDefinedException;
import org.craftercms.profile.exceptions.InvalidEmailAddressException;
import org.craftercms.profile.exceptions.InvalidQueryException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.exceptions.NoSuchTenantException;
import org.craftercms.profile.exceptions.NoSuchTicketException;
import org.craftercms.profile.exceptions.NoSuchVerificationTokenException;
import org.craftercms.profile.exceptions.ProfileExistsException;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.services.VerificationService;
import org.craftercms.profile.utils.db.ProfileUpdater;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link org.craftercms.profile.api.services.ProfileService}.
 *
 * @author avasquez
 */
@Logged
public class ProfileServiceImpl implements ProfileService {

    private static final I10nLogger logger = new I10nLogger(ProfileServiceImpl.class,
                                                            "crafter.profile.messages.logging");

    public static final String LOG_KEY_PROFILE_CREATED = "profile.profile.profileCreated";
    public static final String LOG_KEY_PROFILE_UPDATED = "profile.profile.profileUpdated";
    public static final String LOG_KEY_PROFILE_VERIFIED = "profile.profile.profileVerified";
    public static final String LOG_KEY_PROFILE_ENABLED = "profile.profile.profileEnabled";
    public static final String LOG_KEY_PROFILE_DISABLED = "profile.profile.profileDisabled";
    public static final String LOG_KEY_PROFILE_ROLES_ADDED = "profile.profile.rolesAdded";
    public static final String LOG_KEY_PROFILE_ROLES_REMOVED = "profile.profile.rolesRemoved";
    public static final String LOG_KEY_PROFILE_ATTRIBS_UPDATED = "profile.profile.attributesUpdated";
    public static final String LOG_KEY_PROFILE_ATTRIBS_REMOVED = "profile.profile.attributesRemoved";
    public static final String LOG_KEY_PROFILE_DELETED = "profile.profile.profileDeleted";
    public static final String LOG_KEY_PASSWORD_CHANGED = "profile.profile.passwordChanged";

    public static final String ERROR_KEY_CREATE_PROFILE_ERROR = "profile.profile.createProfileError";
    public static final String ERROR_KEY_GET_PROFILE_BY_QUERY_ERROR = "profile.profile.getProfileByQueryError";
    public static final String ERROR_KEY_GET_PROFILE_ERROR = "profile.profile.getProfileError";
    public static final String ERROR_KEY_UPDATE_PROFILE_ERROR = "profile.profile.updateProfileError";
    public static final String ERROR_KEY_DELETE_PROFILE_ERROR = "profile.profile.deleteProfileError";
    public static final String ERROR_KEY_GET_PROFILE_COUNT_BY_QUERY_ERROR = "profile.profile" +
                                                                            ".getProfileCountByQueryError";
    public static final String ERROR_KEY_GET_PROFILES_BY_QUERY_ERROR = "profile.profile.getProfilesByQueryError";
    public static final String ERROR_KEY_GET_PROFILE_BY_USERNAME_ERROR = "profile.profile.getProfileByUsernameError";
    public static final String ERROR_KEY_GET_PROFILE_COUNT_ERROR = "profile.profile.getProfileCountError";
    public static final String ERROR_KEY_GET_PROFILES_ERROR = "profile.profile.getProfilesError";
    public static final String ERROR_KEY_GET_PROFILE_RANGE_ERROR = "profile.profile.getProfileRangeError";
    public static final String ERROR_KEY_GET_PROFILES_BY_ROLE_ERROR = "profile.profile.getProfilesByRoleError";
    public static final String ERROR_KEY_GET_PROFILES_BY_EXISTING_ATTRIB_ERROR = "profile.profile" +
                                                                                 ".getProfilesByExistingAttributeError";
    public static final String ERROR_KEY_GET_PROFILES_BY_ATTRIB_VALUE_ERROR = "profile.profile" +
                                                                              ".getProfilesByAttributeValueError";
    public static final String ERROR_KEY_TENANT_NOT_ALLOWED = "profile.profile.query.tenantNotAllowed";
    public static final String ERROR_KEY_WHERE_NOT_ALLOWED = "profile.profile.query.whereNotAllowed";
    public static final String ERROR_KEY_ATTRIBUTE_NOT_ALLOWED = "profile.profile.query.attributeNotAllowed";

    public static final String ERROR_KEY_ENTITLEMENT_ERROR = "profile.license.entitlementError";

    public static final Pattern QUERY_TENANT_PATTERN = Pattern.compile("['\"]?tenant['\"]?\\s*:");
    public static final Pattern QUERY_WHERE_PATTERN = Pattern.compile("['\"]?\\$where['\"]?\\s*:");
    public static final String QUERY_ATTRIBUTE_PATTERN_FORMAT = "['\"]?attributes\\.%s(\\.[^'\":]+)?['\"]?\\s*:";
    public static final String QUERY_FINAL_FORMAT = "{$and: [{tenant: '%s'}, %s]}";

    protected PermissionEvaluator<AccessToken, String> tenantPermissionEvaluator;
    protected PermissionEvaluator<AccessToken, AttributeDefinition> attributePermissionEvaluator;
    protected ProfileRepository profileRepository;
    protected TenantService tenantService;
    protected AuthenticationService authenticationService;
    protected VerificationService verificationService;
    protected List<String> validAttachmentMimeTypes;
    protected String newProfileEmailFromAddress;
    protected String newProfileEmailSubject;
    protected String newProfileEmailTemplateName;
    protected String resetPwdEmailFromAddress;
    protected String resetPwdEmailSubject;
    protected String resetPwdEmailTemplateName;

    protected EntitlementValidator entitlementValidator;

    @Required
    public void setTenantPermissionEvaluator(PermissionEvaluator<AccessToken, String> tenantPermissionEvaluator) {
        this.tenantPermissionEvaluator = tenantPermissionEvaluator;
    }

    @Required
    public void setAttributePermissionEvaluator(
        PermissionEvaluator<AccessToken, AttributeDefinition> attributePermissionEvaluator) {
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
    public void setVerificationService(final VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @Required
    public void setNewProfileEmailFromAddress(final String newProfileEmailFromAddress) {
        this.newProfileEmailFromAddress = newProfileEmailFromAddress;
    }

    @Required
    public void setNewProfileEmailSubject(final String newProfileEmailSubject) {
        this.newProfileEmailSubject = newProfileEmailSubject;
    }

    @Required
    public void setNewProfileEmailTemplateName(final String newProfileEmailTemplateName) {
        this.newProfileEmailTemplateName = newProfileEmailTemplateName;
    }

    @Required
    public void setResetPwdEmailFromAddress(final String resetPwdEmailFromAddress) {
        this.resetPwdEmailFromAddress = resetPwdEmailFromAddress;
    }

    @Required
    public void setResetPwdEmailSubject(final String resetPwdEmailSubject) {
        this.resetPwdEmailSubject = resetPwdEmailSubject;
    }

    @Required
    public void setResetPwdEmailTemplateName(final String resetPwdEmailTemplateName) {
        this.resetPwdEmailTemplateName = resetPwdEmailTemplateName;
    }

    @Required
    public void setEntitlementValidator(final EntitlementValidator entitlementValidator) {
        this.entitlementValidator = entitlementValidator;
    }

    @Override
    public Profile createProfile(String tenantName, String username, String password, String email, boolean enabled,
                                 Set<String> roles, Map<String, Object> attributes,
                                 String verificationUrl) throws ProfileException {
        checkIfManageProfilesIsAllowed(tenantName);

        try {
            entitlementValidator.validateEntitlement(EntitlementType.USER, 1);
        } catch (Exception e) {
            throw new I10nProfileException(ERROR_KEY_ENTITLEMENT_ERROR, e);
        }

        if (!EmailUtils.validateEmail(email)) {
            throw new InvalidEmailAddressException(email);
        }

        try {
            Tenant tenant = getTenant(tenantName);
            Date now = new Date();

            Profile profile = new Profile();
            profile.setTenant(tenantName);
            profile.setUsername(username);
            profile.setPassword(CryptoUtils.hashPassword(password));
            profile.setEmail(email);
            profile.setCreatedOn(now);
            profile.setLastModified(now);
            profile.setVerified(false);

            boolean emailNewProfiles = tenant.isVerifyNewProfiles();
            if (!emailNewProfiles || StringUtils.isEmpty(verificationUrl)) {
                profile.setEnabled(enabled);
            }

            if (CollectionUtils.isNotEmpty(roles)) {
                profile.setRoles(roles);
            }

            for (AttributeDefinition definition : tenant.getAttributeDefinitions()) {
                if (definition.getDefaultValue() != null) {
                    profile.setAttribute(definition.getName(), definition.getDefaultValue());
                }
            }
            if (MapUtils.isNotEmpty(attributes)) {
                rejectAttributesIfActionNotAllowed(tenant, attributes.keySet(), AttributeAction.WRITE_ATTRIBUTE);

                profile.getAttributes().putAll(attributes);
            }

            profileRepository.insert(profile);

            logger.debug(LOG_KEY_PROFILE_CREATED, profile);

            if (emailNewProfiles && StringUtils.isNotEmpty(verificationUrl)) {
                VerificationToken token = verificationService.createToken(profile);
                verificationService.sendEmail(token, profile, verificationUrl, newProfileEmailFromAddress,
                                              newProfileEmailSubject, newProfileEmailTemplateName);
            }

            return profile;
        } catch (DuplicateKeyException e) {
            throw new ProfileExistsException(tenantName, username);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_PROFILE_ERROR, e, username, tenantName);
        }
    }

    @Override
    public Profile updateProfile(final String profileId, final String username, final String password,
                                 final String email, final Boolean enabled, final Set<String> roles,
                                 final Map<String, Object> attributes,
                                 String... attributesToReturn) throws ProfileException {
        if (StringUtils.isNotEmpty(email) && !EmailUtils.validateEmail(email)) {
            throw new InvalidEmailAddressException(email);
        }

        Profile profile = updateProfile(profileId, profileUpdater -> {
            if (StringUtils.isNotEmpty(username)) {
                profileUpdater.setUsername(username);
            }
            if (StringUtils.isNotEmpty(password)) {
                profileUpdater.setPassword(CryptoUtils.hashPassword(password));
            }
            if (StringUtils.isNotEmpty(email)) {
                profileUpdater.setEmail(email);
            }
            if (enabled != null) {
                profileUpdater.setEnabled(enabled);
            }
            if (roles != null) {
                profileUpdater.setRoles(roles);
            }
            if (MapUtils.isNotEmpty(attributes)) {
                String tenantName = profileUpdater.getProfile().getTenant();

                rejectAttributesIfActionNotAllowed(tenantName, attributes.keySet(),
                                                   AttributeAction.WRITE_ATTRIBUTE);

                profileUpdater.addAttributes(attributes);
            }
        }, attributesToReturn);

        logger.debug(LOG_KEY_PROFILE_UPDATED, profile);

        return profile;
    }

    @Override
    public Profile verifyProfile(String verificationTokenId,
                                 final String... attributesToReturn) throws ProfileException {
        VerificationToken token = verificationService.getToken(verificationTokenId);

        if (token == null) {
            throw new NoSuchVerificationTokenException(verificationTokenId);
        }

        Profile profile = updateProfile(token.getProfileId(), profileUpdater -> {
            profileUpdater.setEnabled(true);
            profileUpdater.setVerified(true);
        }, attributesToReturn);

        verificationService.deleteToken(verificationTokenId);

        logger.debug(LOG_KEY_PROFILE_VERIFIED, profile.getId());

        return profile;
    }

    @Override
    public Profile enableProfile(String profileId, String... attributesToReturn) throws ProfileException {
        Profile profile = updateProfile(profileId, profileUpdater -> profileUpdater.setEnabled(true), attributesToReturn);

        logger.debug(LOG_KEY_PROFILE_ENABLED, profileId);

        return profile;
    }

    @Override
    public Profile setLastFailedLogin(String profileId, final Date lastFailedLogin, String... attributesToReturn)
        throws ProfileException {
        Profile profile = updateProfile(profileId, profileUpdater -> profileUpdater.setLastFailedLogin(lastFailedLogin),
                                        attributesToReturn);

        logger.debug(LOG_KEY_PROFILE_ENABLED, profileId);

        return profile;
    }

    @Override
    public Profile setFailedLoginAttempts(String profileId, final int failedAttempts, String... attributesToReturn)
        throws ProfileException {
        Profile profile = updateProfile(profileId, profileUpdater -> profileUpdater.setFailedLoginAttempts(failedAttempts),
                                        attributesToReturn);

        logger.debug(LOG_KEY_PROFILE_ENABLED, profileId);

        return profile;
    }

    @Override
    public Profile disableProfile(String profileId, String... attributesToReturn) throws ProfileException {
        Profile profile = updateProfile(profileId, profileUpdater -> profileUpdater.setEnabled(false), attributesToReturn);

        logger.debug(LOG_KEY_PROFILE_DISABLED, profileId);

        return profile;
    }

    @Override
    public Profile addRoles(String profileId, final Collection<String> roles,
                            String... attributesToReturn) throws ProfileException {
        Profile profile = updateProfile(profileId, profileUpdater -> profileUpdater.addRoles(roles), attributesToReturn);

        logger.debug(LOG_KEY_PROFILE_ROLES_ADDED, roles, profileId);

        return profile;
    }

    @Override
    public Profile removeRoles(String profileId, final Collection<String> roles,
                               String... attributesToReturn) throws ProfileException {
        Profile profile = updateProfile(profileId, profileUpdater -> profileUpdater.removeRoles(roles), attributesToReturn);

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
        Profile profile = updateProfile(profileId, profileUpdater -> {
            String tenantName = profileUpdater.getProfile().getTenant();

            rejectAttributesIfActionNotAllowed(tenantName, attributes.keySet(), AttributeAction.WRITE_ATTRIBUTE);

            profileUpdater.addAttributes(attributes);
        }, attributesToReturn);

        if (logger.isDebugEnabled()) {
            logger.debug(LOG_KEY_PROFILE_ATTRIBS_UPDATED, attributes.keySet(), profileId);
        }

        return profile;
    }

    @Override
    public Profile removeAttributes(String profileId, final Collection<String> attributeNames,
                                    String... attributesToReturn) throws ProfileException {
        Profile profile = updateProfile(profileId, profileUpdater -> {
            String tenantName = profileUpdater.getProfile().getTenant();

            rejectAttributesIfActionNotAllowed(tenantName, attributeNames, AttributeAction.REMOVE_ATTRIBUTE);

            profileUpdater.removeAttributes(attributeNames);
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
    public Profile getProfileByQuery(String tenantName, String query,
                                     String... attributesToReturn) throws ProfileException {
        checkIfManageProfilesIsAllowed(tenantName);

        Tenant tenant = getTenant(tenantName);

        try {
            Profile profile = profileRepository.findOneByQuery(getFinalQuery(tenant, query), attributesToReturn);
            filterNonReadableAttributes(tenant, profile);

            return profile;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_BY_QUERY_ERROR, e, query);
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
    public Profile getProfileByUsername(String tenantName, String username,
                                        String... attributesToReturn) throws ProfileException {
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
            throw new NoSuchTicketException.Expired(ticketId);
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
    public long getProfileCountByQuery(String tenantName, String query) throws ProfileException {
        checkIfManageProfilesIsAllowed(tenantName);

        Tenant tenant = getTenant(tenantName);

        try {
            return profileRepository.count(getFinalQuery(tenant, query));
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILE_COUNT_BY_QUERY_ERROR, e, tenant, query);
        }
    }

    @Override
    public List<Profile> getProfilesByQuery(String tenantName, String query, String sortBy, SortOrder sortOrder,
                                            Integer start, Integer count,
                                            String... attributesToReturn) throws ProfileException {
        checkIfManageProfilesIsAllowed(tenantName);

        Tenant tenant = getTenant(tenantName);

        try {
            List<Profile> profiles = IterableUtils.toList(profileRepository.findByQuery(getFinalQuery(tenant, query),
                                                                                        sortBy, sortOrder, start,
                                                                                        count, attributesToReturn));
            filterNonReadableAttributes(tenant, profiles);

            return profiles;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILES_BY_QUERY_ERROR, e, tenant, query);
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
                                                                                      start, count,
                                                                                      attributesToReturn));
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
                                                                                                sortBy, sortOrder,
                                                                                                attributesToReturn));
            filterNonReadableAttributes(profiles);

            return profiles;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILES_BY_ROLE_ERROR, e, role, tenantName);
        }
    }

    @Override
    public List<Profile> getProfilesByExistingAttribute(String tenantName, String attributeName, String sortBy,
                                                        SortOrder sortOrder,
                                                        String... attributesToReturn) throws ProfileException {
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
                                                     String sortBy, SortOrder sortOrder,
                                                     String... attributesToReturn) throws ProfileException {
        checkIfManageProfilesIsAllowed(tenantName);

        try {
            List<Profile> profiles = IterableUtils.toList(
                profileRepository.findByTenantAndAttributeValue(tenantName, attributeName, attributeValue, sortBy,
                                                                sortOrder, attributesToReturn));
            filterNonReadableAttributes(profiles);

            return profiles;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PROFILES_BY_ATTRIB_VALUE_ERROR, e, attributeName,
                                           attributeValue, tenantName);
        }
    }

    @Override
    public Profile resetPassword(String profileId, String resetPasswordUrl,
                                 String... attributesToReturn) throws ProfileException {
        Profile profile = getNonNullProfile(profileId, attributesToReturn);
        VerificationToken token = verificationService.createToken(profile);

        verificationService.sendEmail(token, profile, resetPasswordUrl, resetPwdEmailFromAddress,
                                      resetPwdEmailSubject, resetPwdEmailTemplateName);

        return profile;
    }

    @Override
    public Profile changePassword(String resetTokenId, final String newPassword,
                                  final String... attributesToReturn) throws ProfileException {
        VerificationToken token = verificationService.getToken(resetTokenId);

        if (token == null) {
            throw new NoSuchVerificationTokenException(resetTokenId);
        }

        Profile profile = updateProfile(token.getProfileId(),
                                        profileUpdater -> profileUpdater.setPassword(CryptoUtils.hashPassword(newPassword)),
                                        attributesToReturn);

        verificationService.deleteToken(resetTokenId);

        logger.debug(LOG_KEY_PASSWORD_CHANGED, profile.getId().toString());

        return profile;
    }

    @Override
    public VerificationToken createVerificationToken(String profileId) throws ProfileException {
        return verificationService.createToken(getNonNullProfile(profileId));
    }

    @Override
    public VerificationToken getVerificationToken(String tokenId) throws ProfileException {
        return verificationService.getToken(tokenId);
    }

    @Override
    public void deleteVerificationToken(String tokenId) throws ProfileException {
        verificationService.deleteToken(tokenId);
    }

    @Override
    public ProfileAttachment addProfileAttachment(final String profileId, final String attachmentName,
                                                  final InputStream file) throws ProfileException {
        String storeName = "/" + profileId + "/" + FilenameUtils.removeExtension(attachmentName);

        try {
            ObjectId currentId = checkIfAttachmentExist(storeName);
            String mimeType = getAttachmentContentType(attachmentName);
            FileInfo fileInfo;

            if (currentId != null) { // Update !!!
                fileInfo = profileRepository.updateFile(currentId, file, storeName, mimeType, true);
            } else {
                fileInfo = profileRepository.saveFile(file, storeName, mimeType);
            }

            return fileInfoToProfileAttachment(fileInfo);
        } catch (MongoDataException | FileExistsException | FileNotFoundException e) {
            throw new ProfileException("Unable to attach file to profile '" + profileId + "'", e);
        }
    }

    private ObjectId checkIfAttachmentExist(final String storeName) {
        ObjectId attachmentId = null;

        try {
            attachmentId = profileRepository.getFileInfo(storeName).getFileId();
        } catch (FileNotFoundException ex){
            // Nothing since files should be New !!!!
        }

        return attachmentId;
    }


    private ProfileAttachment fileInfoToProfileAttachment(final FileInfo fileInfo) {
        if(fileInfo == null){
            return new ProfileAttachment();
        }

        ProfileAttachment attachment = new ProfileAttachment();
        attachment.setContentType(fileInfo.getContentType());;
        attachment.setMd5(fileInfo.getMd5());
        attachment.setFileName(fileInfo.getStoreName().substring(fileInfo.getStoreName().lastIndexOf("/")+1));
        attachment.setFileSize(fileInfo.getFileSize());
        attachment.setFileSizeBytes(fileInfo.getFileSizeBytes());
        attachment.setId(fileInfo.getFileId().toString());

        return attachment;
    }

    private String getAttachmentContentType(final String attachmentName) throws ProfileException {
        String mimeType = new MimetypesFileTypeMap().getContentType(attachmentName);

        if (validAttachmentMimeTypes.contains(mimeType)) {
            return mimeType;
        }

        throw new ProfileException("File " + attachmentName + " if of content Type " + mimeType + " which is not allowed");
    }

    @Override
    public ProfileAttachment getProfileAttachmentInformation(final String profileId, final String attachmentId) throws ProfileException {
        FileInfo fileInfo;
        try {
            fileInfo = profileRepository.getFileInfo("/" + profileId + "/" + attachmentId);

            return fileInfoToProfileAttachment(fileInfo);
        } catch (FileNotFoundException e) {
            return new ProfileAttachment();
        }
    }

    @Override
    public InputStream getProfileAttachment(final String attachmentId, final String profileId) throws ProfileException {
        try {
            final FileInfo fileInfo = profileRepository.readFile("/" + profileId + "/" + attachmentId);

            return fileInfo.getInputStream();
        } catch (FileNotFoundException e) {
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    @Override
    public List<ProfileAttachment> getProfileAttachments(final String profileId) throws ProfileException {
        List<FileInfo> files = profileRepository.listFilesByName(profileId);
        List<ProfileAttachment> attachments = new ArrayList<>();

        for (FileInfo file : files) {
            attachments.add(fileInfoToProfileAttachment(file));
        }

        return attachments;
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
            throw new NoSuchProfileException.ById(id);
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

    protected Profile updateProfile(String profileId, UpdateCallback callback,
                                    String... attributesToReturn) throws ProfileException {
        // We need to filter the attributes after save, if not, the attributes to return will replace all the
        // attributes
        Profile profile = getNonNullProfile(profileId);
        UpdateHelper updateHelper = new UpdateHelper();
        ProfileUpdater profileUpdater = new ProfileUpdater(profile, updateHelper, profileRepository);

        callback.doWithProfile(profileUpdater);

        profileUpdater.setLastModified(new Date());

        try {
            profileUpdater.update();
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_UPDATE_PROFILE_ERROR, e, profileId);
        }

        return filterAttributes(profile, attributesToReturn);
    }

    protected boolean isAttributeActionAllowed(AttributeDefinition definition, AttributeAction action) {
        return attributePermissionEvaluator.isAllowed(definition, action.toString());
    }

    protected Profile filterAttributes(Profile profile, String[] attributesToReturn) {
        if (ArrayUtils.isNotEmpty(attributesToReturn) && MapUtils.isNotEmpty(profile.getAttributes())) {
            Iterator<String> keyIter = profile.getAttributes().keySet().iterator();

            while (keyIter.hasNext()) {
                String key = keyIter.next();
                if (!ArrayUtils.contains(attributesToReturn, key)) {
                    keyIter.remove();
                }
            }
        }

        return profile;
    }

    protected void filterNonReadableAttributes(Profile profile) throws ProfileException {
        if (profile != null) {
            filterNonReadableAttributes(getTenant(profile.getTenant()), profile);
        }
    }

    protected void filterNonReadableAttributes(Tenant tenant, Profile profile) throws ProfileException {
        if (profile != null) {
            List<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();
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

    protected void filterNonReadableAttributes(Tenant tenant, Iterable<Profile> profiles) throws ProfileException {
        if (profiles != null) {
            for (Profile profile : profiles) {
                filterNonReadableAttributes(tenant, profile);
            }
        }
    }

    protected void rejectAttributesIfActionNotAllowed(String tenantName, Collection<String> attributeNames,
                                                      AttributeAction action) throws ProfileException {
        rejectAttributesIfActionNotAllowed(getTenant(tenantName), attributeNames, action);
    }

    protected void rejectAttributesIfActionNotAllowed(Tenant tenant, Collection<String> attributeNames,
                                                      AttributeAction action) throws ProfileException {
        List<AttributeDefinition> attributeDefinitions = tenant.getAttributeDefinitions();

        for (String attributeName : attributeNames) {
            rejectAttributeIfActionNotAllowed(tenant, attributeName, action, attributeDefinitions);
        }
    }

    protected void filterAttributeIfReadNotAllowed(Tenant tenant, Iterator<String> attributeNamesIter,
                                                   List<AttributeDefinition> attributeDefinitions) throws
        PermissionException, AttributeNotDefinedException {
        String tenantName = tenant.getName();
        String attributeName = attributeNamesIter.next();
        AttributeDefinition definition = findAttributeDefinition(attributeDefinitions, attributeName);

        if (definition != null) {
            if (!isAttributeActionAllowed(definition, AttributeAction.READ_ATTRIBUTE)) {
                attributeNamesIter.remove();
            }
        } else {
            throw new AttributeNotDefinedException(attributeName, tenantName);
        }
    }

    protected void rejectAttributeIfActionNotAllowed(Tenant tenant, String attributeName, AttributeAction action,
                                                     List<AttributeDefinition> attributeDefinitions) throws
        PermissionException, AttributeNotDefinedException {
        AttributeDefinition definition = findAttributeDefinition(attributeDefinitions, attributeName);
        if (definition != null) {
            if (!isAttributeActionAllowed(definition, action)) {
                throw new ActionDeniedException(action.toString(), "attribute \"" + attributeName + "\"");
            }
        } else {
            throw new AttributeNotDefinedException(attributeName, tenant.getName());
        }
    }

    protected String getFinalQuery(Tenant tenant, String query) throws ProfileException {
        validateQuery(tenant, query);
        return String.format(QUERY_FINAL_FORMAT, tenant.getName(), query);
    }

    protected void validateQuery(Tenant tenant, String query) throws ProfileException {
        if (QUERY_TENANT_PATTERN.matcher(query).find()) {
            throw new InvalidQueryException(ERROR_KEY_TENANT_NOT_ALLOWED);
        }

        if (QUERY_WHERE_PATTERN.matcher(query).find()) {
            throw new InvalidQueryException(ERROR_KEY_WHERE_NOT_ALLOWED);
        }

        for (AttributeDefinition definition : tenant.getAttributeDefinitions()) {
            if (!attributePermissionEvaluator.isAllowed(definition, AttributeAction.READ_ATTRIBUTE.toString())) {
                String attributeName = definition.getName();
                Pattern pattern = Pattern.compile(String.format(QUERY_ATTRIBUTE_PATTERN_FORMAT, attributeName));

                if (pattern.matcher(query).find()) {
                    throw new InvalidQueryException(ERROR_KEY_ATTRIBUTE_NOT_ALLOWED, attributeName);
                }
            }
        }
    }

    public void setValidAttachmentMimeTypes(final String validAttachmentMimeTypes) {
        if(StringUtils.isNotBlank(validAttachmentMimeTypes)){
            this.validAttachmentMimeTypes=Arrays.asList(validAttachmentMimeTypes.split(","));
        }else{
            this.validAttachmentMimeTypes = Collections.EMPTY_LIST;
        }
    }

    protected AttributeDefinition findAttributeDefinition(final List<AttributeDefinition> attributeDefinitions,
                                                          final String name) {
        return CollectionUtils.find(attributeDefinitions, new Predicate<AttributeDefinition>() {

            @Override
            public boolean evaluate(AttributeDefinition definition) {
                return definition.getName().equals(name);
            }

        });
    }

    protected interface UpdateCallback {

        void doWithProfile(ProfileUpdater profileUpdater) throws ProfileException;

    }

}
