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
package org.craftercms.profile.management.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.RestException;
import org.craftercms.profile.impl.domain.Attribute;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.profile.impl.domain.Tenant;
import org.craftercms.profile.management.model.FilterForm;
import org.craftercms.profile.management.model.ProfileUserAccountForm;
import org.craftercms.profile.management.model.SchemaForm;
import org.craftercms.profile.management.services.impl.ProfileDAOServiceImpl;
import org.craftercms.profile.management.util.ProfileAccountPaging;
import org.craftercms.profile.management.util.ProfileUserAccountUtil;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProfileAccountService {

    private static final String DEFAULT_VERIFICATION_ACCOUNT_URL = "/crafter-profile-admin-console/verify-account";
    private static final String VERIFICATION_ACCOUNT = "${crafter.profile.verification.account.url}";
    private static final Logger log = LoggerFactory.getLogger(ProfileAccountService.class);
    private String verificationAccountUrl;

    private ProfileDAOServiceImpl profileDao;

    private ProfileAccountPaging profileAccountPaging;

    private TenantDAOService tenantDAOService;

    private String appToken;

    public ProfileAccountService() {
        verificationAccountUrl = DEFAULT_VERIFICATION_ACCOUNT_URL;
    }

    public List<ProfileUserAccountForm> getProfileUsers(String tenantName) throws AppAuthenticationFailedException {

        profileAccountPaging.setTotal(profileDao.getProfileCount(tenantName));
        List<String> attributes = ProfileUserAccountUtil.getAttributes(getSchema(tenantName));
        List<Profile> profileList = profileDao.getUsers(profileAccountPaging.getStart(),
            profileAccountPaging.getEnd(), profileAccountPaging.getSortBy(), profileAccountPaging.getSortOrder(),
            attributes, tenantName);

        return ProfileUserAccountUtil.mapForForm(excludeUsers(profileList));

    }

    public List<ProfileUserAccountForm> getNextUserPage(String tenantName) throws AppAuthenticationFailedException {
        List<String> attributes = ProfileUserAccountUtil.getAttributes(getSchema(tenantName));
        profileAccountPaging.next();
        List<Profile> profileList = profileDao.getUsers(profileAccountPaging.getStart(),
            profileAccountPaging.getEnd(), profileAccountPaging.getSortBy(), profileAccountPaging.getSortOrder(),
            attributes, tenantName);
        profileList = excludeUsers(profileList);
        if (profileList.size() > 0) {
            return ProfileUserAccountUtil.mapForForm(profileList);
        } else {
            return getPrevUserPage(tenantName);
        }
    }

    public List<ProfileUserAccountForm> getPrevUserPage(String tenantName) throws AppAuthenticationFailedException {
        List<String> attributes = ProfileUserAccountUtil.getAttributes(getSchema(tenantName));
        profileAccountPaging.previous();
        List<Profile> profileList = profileDao.getUsers(profileAccountPaging.getStart(),
            profileAccountPaging.getEnd(), profileAccountPaging.getSortBy(), profileAccountPaging.getSortOrder(),
            attributes, tenantName);
        return ProfileUserAccountUtil.mapForForm(excludeUsers(profileList));
    }

    public ProfileUserAccountForm createNewProfileUserAccountForm(Tenant tenant) throws
        AppAuthenticationFailedException {
        ProfileUserAccountForm accountForm = new ProfileUserAccountForm(tenant, tenant.getRoles());
        return accountForm;
    }

    public void createUserAccount(ProfileUserAccountForm account, HttpServletRequest request) throws
        AppAuthenticationFailedException {
        Map<String, Serializable> data = ProfileUserAccountUtil.getUpdateUserData(account);
        addVerificationAccountUrl(data, request);
        profileDao.createUser(data);
    }

    public void updateUserAccount(ProfileUserAccountForm account) throws RestException {
        profileDao.updateUser(ProfileUserAccountUtil.getUpdateUserData(account));
    }

    public void setSchemaAttribute(Attribute attribute, String tenantName) throws AppAuthenticationFailedException {
        profileDao.setSchemaAttribute(attribute, tenantName);
    }

    public void deleteSchemaAttributes(SchemaForm baseUser, List<String> attributes) throws
        AppAuthenticationFailedException {
        profileDao.deleteSchemaAttributes(baseUser.getTenantName(), attributes);
    }

    public ProfileUserAccountForm getUserForUpdate(String username, String tenantName) throws RestException {
        Profile p = profileDao.getUserWithAllAttributes(username, tenantName);
        Tenant tenant = tenantDAOService.getTenantByName(p.getTenantName());
        return new ProfileUserAccountForm(p, tenant, false);
    }

    public void activeUsers(ArrayList<String> userIds, boolean active) throws AppAuthenticationFailedException {
        profileDao.activeUsers(userIds, active);
    }

    public Profile verifyAccount(String token) throws AppAuthenticationFailedException {
        return profileDao.verifyAccount(token);
    }

    public SchemaForm getSchema(String tenantName) throws AppAuthenticationFailedException {
        return new SchemaForm(this.profileDao.getSchema(tenantName));
    }

    public List<ProfileUserAccountForm> getSearchProfileUsers(FilterForm filter,
                                                              String tenantName) throws
        AppAuthenticationFailedException {
        String userName = filter.getUserName();
        if (userName == null || userName.isEmpty() || userName.equals("*")) {
            return getProfileUsers(tenantName);
        }

        List<ProfileUserAccountForm> list = new ArrayList<ProfileUserAccountForm>();
        Profile profile = profileDao.getUser(userName, tenantName);
        if (profile != null) {
            list.add(new ProfileUserAccountForm(profile));
        }
        return list;
    }

    @Autowired
    public void setProfileDAOService(ProfileDAOServiceImpl profileDAO) {
        this.profileDao = profileDAO;
    }

    @Autowired
    public void setProfileAccountPaging(ProfileAccountPaging paging) {
        this.profileAccountPaging = paging;
    }

    @Autowired
    public void setTenantDAOService(TenantDAOService tenantDAOService) {
        this.tenantDAOService = tenantDAOService;
    }

    private List<Profile> excludeUsers(List<Profile> profiles) {
        RequestContext context = RequestContext.getCurrent();
        UserProfile currentUser = context.getAuthenticationToken().getProfile();
        List<Profile> newList = new ArrayList<Profile>();
        for (Profile profile : profiles) {

            if (isDisplayableUser(profile, currentUser)) {
                newList.add(profile);
            }
        }
        return newList;
    }

    private boolean isDisplayableUser(Profile profile, UserProfile currentUser) {
        boolean isDisplayable = true;
        if (profile.getUserName() == null
            // Profile should have username but this is a validation only
            // Profile is user log-in
            || (profile.getUserName().equals(currentUser.getUserName()) && profile.getTenantName().equals(currentUser
            .getTenantName()))) { //
            isDisplayable = false;
        }
        return isDisplayable;
    }

    /**
     * Url to the form confirm the verification of the new account
     *
     * @param url valid url to the form confirm the verification of the new account
     */
    @Value("${crafter.profile.verification.account.url}")
    public void setCrafterProfileConfirmAccountUrl(String url) {
        if (url != null && !url.equals("") && !url.equals(VERIFICATION_ACCOUNT)) {
            this.verificationAccountUrl = url;
        }
    }

    public String getVerificationAccount(HttpServletRequest request) {
        String url = this.verificationAccountUrl;
        int index = request.getRequestURL().indexOf(request.getRequestURI());
        if (index >= 0) {
            String baseUri = request.getRequestURL().substring(0, index);
            if (baseUri.endsWith("/") && verificationAccountUrl.startsWith("/")) {
                url = baseUri + verificationAccountUrl.substring(1);
            } else if (baseUri.endsWith("/")) {
                url = baseUri + verificationAccountUrl;
            } else if (verificationAccountUrl.startsWith("/")) {
                url = baseUri + verificationAccountUrl;
            } else {
                url = baseUri + "/" + verificationAccountUrl;
            }
        }
        return url;
    }

    private void addVerificationAccountUrl(Map<String, Serializable> data, HttpServletRequest request) {
        data.put(ProfileConstants.VERIFICATION_ACCOUNT_URL, getVerificationAccount(request));
    }


}
