/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.domain.Ticket;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.InvalidEmailException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.repositories.TicketRepository;
import org.craftercms.profile.services.EmailValidatorService;
import org.craftercms.profile.services.ProfileService;
import org.craftercms.profile.services.VerifyAccountService;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
public class ProfileServiceImpl implements ProfileService {

    private final transient Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private EmailValidatorService emailValidatorService;
    
    @Autowired
    private VerifyAccountService verifyAccountService;
    
    private List<String> enabledUsers;

    @Override
    /*
     * (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#createProfile(java.lang.String, java.lang.String, java.lang.Boolean, java.lang.String, java.lang.String, java.util.Map, java.util.List, javax.servlet.http.HttpServletResponse)
     */
    public Profile createProfile(String userName, String password, Boolean active, String tenantName, String email,
                                 Map<String, Serializable> attributes, List<String> roles, String verifyAccountUrl,
                                 HttpServletResponse response, HttpServletRequest request) throws InvalidEmailException, CipherException, MailException, NoSuchProfileException {
        if (!emailValidatorService.validateEmail(email)) {
            throw new InvalidEmailException("Invalid email account format");
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Profile profile = new Profile();
        profile.setUserName(userName);
        profile.setPassword(hashedPassword);
        if (!isEnabledUser(userName)) {
        	//profile.setActive(active);
        	profile.setActive(false); //until the account is verify then the account is actived and user will be able to authenticate
        	profile.setVerify(false);
        } else {
        	profile.setActive(true);
        	profile.setVerify(true);
        }
        profile.setTenantName(tenantName);
        profile.setCreated(new Date());
        profile.setModified(new Date());
        profile.setAttributes(attributes);
        profile.setEmail(email);
        profile.setRoles(roles);
        Profile savedProfile = null;
        try {
        	savedProfile =  profileRepository.save(profile);
            if (!isEnabledUser(userName)) {
            	verifyAccountService.sendVerifyNotification(profile, verifyAccountUrl, request);
            }
            
        } catch (DuplicateKeyException e) {
            try {
                if (response != null) {
                    response.sendError(HttpServletResponse.SC_CONFLICT);
                }
            } catch (IOException e1) {
                log.error("Can't set error status after a DuplicateKey exception was received.");
            }
        } catch (Exception e) {
        	log.error("Error in ProfileService.createProfile: " + e.getMessage());
        }
        return savedProfile;
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfileRange(java.lang.String, java.lang.String, java.lang.String, java.util.List, int, int)
     */
    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfileRange(java.lang.String, java.lang.String, java.lang.String, java.util.List, int, int)
     */
    @Override
    public List<Profile> getProfileRange(String tenantName, String sortBy, String sortOrder,
                                         List<String> attributesList, int start, int end) {
        return profileRepository.getProfileRange(tenantName, sortBy, sortOrder, attributesList, start, end);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfilesCount(java.lang.String)
     */
    @Override
    public long getProfilesCount(String tenantName) {
        return profileRepository.getProfilesCount(tenantName);
    }


    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#updateProfile(java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, java.lang.String, java.lang.String, java.util.Map, java.util.List)
     */
    @Override
    public Profile updateProfile(String profileId, String userName, String password, Boolean active,
                                 String tenantName, String email, Map<String, Serializable> attributes,
                                 List<String> roles) {
        Profile profile = profileRepository.findOne(new ObjectId(profileId));

        if (profile == null) {
            return profile;
        }
        if (userName != null && !userName.trim().isEmpty()) {
            profile.setUserName(userName);
        }

        if (password != null && !password.trim().isEmpty()) {
        	String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            profile.setPassword(hashedPassword);
        }

        if (active != null && !isEnabledUser(userName)) {
            profile.setActive(active);
        }

        if (tenantName != null && !tenantName.trim().isEmpty()) {
            profile.setTenantName(tenantName);
        }

        if (roles != null) {
            profile.setRoles(roles);
        }

        if (email != null) {
            profile.setEmail(email);
        }
        Map<String, Serializable> currentAttributes = profile.getAttributes();
        if (currentAttributes != null && attributes != null) {
            currentAttributes.putAll(attributes);
        } else {
            currentAttributes = attributes;
        }

        profile.setAttributes(currentAttributes);
        profile.setModified(new Date());
        profile = profileRepository.save(profile);
        return profile;
    }
    
    public Profile updateProfile(Profile profile) {
    	Profile p = profileRepository.save(profile);
        return p;
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfileByTicket(java.lang.String)
     */
    @Override
    public Profile getProfileByTicket(String ticketStr) {
        Ticket ticket = ticketRepository.getByTicket(ticketStr);
        if (ticket == null) {
            return null;
        }
        return getProfileByUserName(ticket.getUsername(), ticket.getTenantName(), null);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfileByTicket(java.lang.String, java.util.List)
     */
    @Override
    public Profile getProfileByTicket(String ticketStr, List<String> attributes) {
        Ticket ticket = ticketRepository.getByTicket(ticketStr);
        if (ticket == null) {
            return null;
        }
        return getProfileByUserName(ticket.getUsername(), ticket.getTenantName(), attributes);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfileByTicketWithAllAttributes(java.lang.String)
     */
    @Override
    public Profile getProfileByTicketWithAllAttributes(String ticketString) {
        Ticket ticket = ticketRepository.getByTicket(ticketString);
        if (ticket == null) {
            return null;
        }
        return getProfileByUserNameWithAllAttributes(ticket.getUsername(), ticket.getTenantName());
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfile(java.lang.String)
     */
    @Override
    public Profile getProfile(String profileId) {
        return profileRepository.getProfile(profileId);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfile(java.lang.String, java.util.List)
     */
    @Override
    public Profile getProfile(String profileId, List<String> attributes) {
        return profileRepository.getProfile(profileId, attributes);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfileWithAllAttributes(java.lang.String)
     */
    @Override
    public Profile getProfileWithAllAttributes(String profileId) {
        return profileRepository.findOne(new ObjectId(profileId));
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfileByUserName(java.lang.String, java.lang.String)
     */
    @Override
    public Profile getProfileByUserName(String userName, String tenantName) {
        return profileRepository.getProfileByUserName(userName, tenantName);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfileByUserName(java.lang.String, java.lang.String, java.util.List)
     */
    @Override
    public Profile getProfileByUserName(String userName, String tenantName, List<String> attributes) {
        return profileRepository.getProfileByUserName(userName, tenantName, attributes);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfileByUserNameWithAllAttributes(java.lang.String, java.lang.String)
     */
    @Override
    public Profile getProfileByUserNameWithAllAttributes(String userName, String tenantName) {
        return profileRepository.getProfileByUserNameWithAllAttributes(userName, tenantName);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfiles(java.util.List)
     */
    @Override
    public List<Profile> getProfiles(List<String> profileIdList) {
        return profileRepository.getProfiles(profileIdList);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfilesWithAttributes(java.util.List)
     */
    @Override
    public List<Profile> getProfilesWithAttributes(List<String> profileIdList) {
        return profileRepository.getProfilesWithAttributes(profileIdList);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#activateProfile(java.lang.String, boolean)
     */
    @Override
    public void activateProfile(String profileId, boolean active) {
        Profile p = profileRepository.findOne(new ObjectId(profileId));
        if (p != null) {
            activateProfile(p, active);
        }

    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#activateProfiles(boolean)
     */
    @Override
    public void activateProfiles(boolean active) {
        List<Profile> l = profileRepository.findAll();
        for (Profile p : l) {
            activateProfile(p, active);
        }
    }

    private void activateProfile(Profile p, boolean active) {
    	if (!isEnabledUser(p.getUserName())) {
	        p.setActive(active);
	        profileRepository.save(p);
    	}
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#deleteProfiles(java.lang.String)
     */
    @Override
    public void deleteProfiles(String tenantName) {
        profileRepository.delete(getProfilesByTenant(tenantName));
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getProfilesByRoleName(java.lang.String, java.lang.String)
     */
    public List<Profile> getProfilesByRoleName(String roleName, String tenantName) {
        return profileRepository.findByRolesAndTenantName(roleName, tenantName);
    }

    private List<Profile> getProfilesByTenant(String tenantName) {
        return profileRepository.getProfilesByTenantName(tenantName);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#setAttributes(java.lang.String, java.util.Map)
     */
    @Override
    public void setAttributes(String profileId, Map<String, Serializable> attributes) {
        profileRepository.setAttributes(profileId, attributes);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getAllAttributes(java.lang.String)
     */
    @Override
    public Map<String, Serializable> getAllAttributes(String profileId) {
        return profileRepository.getAllAttributes(profileId);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getAttributes(java.lang.String, java.util.List)
     */
    @Override
    public Map<String, Serializable> getAttributes(String profileId, List<String> attributes) {
        return profileRepository.getAttributes(profileId, attributes);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#getAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, Serializable> getAttribute(String profileId, String attributeKey) {
        return profileRepository.getAttribute(profileId, attributeKey);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#deleteAllAttributes(java.lang.String)
     */
    @Override
    public void deleteAllAttributes(String profileId) {
        profileRepository.deleteAllAttributes(profileId);
    }

    /* (non-Javadoc)
     * @see org.craftercms.profile.services.ProfileService#deleteAttributes(java.lang.String, java.util.List)
     */
    @Override
    public void deleteAttributes(String profileId, List<String> attributes) {
        profileRepository.deleteAttributes(profileId, attributes);
    }
    
    /**
     * @param users
     */
    @Value("#{ssrSettings['enabled-users']}")
    public void setProtectedDisableUsers(String users) {
        this.enabledUsers = convertLineToList(users);

    }
    
    private List<String> convertLineToList(String list) {
        if (list == null || list.length() == 0) {
            return new ArrayList<String>();
        }
        String[] arrayRoles = list.split(",");
        return Arrays.asList(arrayRoles);

    }
    
    private boolean isEnabledUser(String username) {
    	boolean isEnabled = false;
    	if (enabledUsers!=null) {
    		isEnabled = enabledUsers.contains(username);
    	}
    	return isEnabled;

    }

}