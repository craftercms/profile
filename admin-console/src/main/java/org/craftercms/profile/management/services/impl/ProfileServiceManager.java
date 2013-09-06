package org.craftercms.profile.management.services.impl;

import org.apache.log4j.Logger;
import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.springframework.beans.factory.annotation.Value;


public class ProfileServiceManager {

    private static String username;
    private static String password;

    private static String appToken = null;

    private static String crafterProfileAppTenantName;

    private static final Logger log = Logger.getLogger(ProfileServiceManager.class);

    private static ProfileClient profileRestClient;

    private ProfileServiceManager() {

    }

    public static Logger getLogger() {
        return log;
    }

    public static ProfileClient getProfileClient() {
        return profileRestClient;
    }

    public static void setProfileClient(ProfileClient profileClient) {
        profileRestClient = profileClient;
    }

    public static String getAppToken() {
        return appToken;
    }

    public static boolean isAppTokenInit() {
        return !(appToken == null);
    }

    public static void resetAppToken() {
        appToken = null;
    }

    public static void setAppToken() throws AppAuthenticationFailedException {
        appToken = profileRestClient.getAppToken(username, password);
    }


    public static void initStaticValues(ProfileClient profileClient, String username, String password,
                                        String tenantName) {
        ProfileServiceManager.username = username;
        ProfileServiceManager.password = password;
        ProfileServiceManager.crafterProfileAppTenantName = tenantName;
        ProfileServiceManager.profileRestClient = profileClient;
    }

    @Value("${crafter.profile.app.tenant.name}")
    public void setCrafterProfileAppTenantName(String crafterProfileAppTenantName) {
        ProfileServiceManager.crafterProfileAppTenantName = crafterProfileAppTenantName;
    }

    public static String getCrafterProfileAppTenantName() {
        return ProfileServiceManager.crafterProfileAppTenantName;
    }

}
