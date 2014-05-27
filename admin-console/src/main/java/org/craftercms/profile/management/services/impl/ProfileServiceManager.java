//package org.craftercms.profile.management.services.impl;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.craftercms.profile.client.api.ProfileClient;
//import org.craftercms.profile.client.exceptions.AppAuthenticationFailedException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//
//
//public class ProfileServiceManager {
//
//    private static String username;
//    private static String password;
//
//    private static String appToken = null;
//
//    private static String crafterProfileAppTenantName;
//
//    private static final Logger log = LoggerFactory.getLogger(ProfileServiceManager.class);
//
//    private static ProfileClient profileRestClient;
//
//    private static List<String> protectedActiveUsers;
//
//    private ProfileServiceManager() {
//
//    }
//
//
//    public static ProfileClient getProfileClient() {
//        return profileRestClient;
//    }
//
//    public static void setProfileClient(ProfileClient profileClient) {
//        profileRestClient = profileClient;
//    }
//
//    public static String getAppToken() {
//        return appToken;
//    }
//
//    public static boolean isAppTokenInit() {
//        return !(appToken == null);
//    }
//
//    public static void resetAppToken() {
//        appToken = null;
//    }
//
//    public static void setAppToken() throws AppAuthenticationFailedException {
//        appToken = profileRestClient.getAppToken(username, password);
//    }
//
//
//    public static void initStaticValues(ProfileClient profileClient, String username, String password,
//                                        String tenantName, String protectedActiveUsers) {
//        ProfileServiceManager.username = username;
//        ProfileServiceManager.password = password;
//        ProfileServiceManager.crafterProfileAppTenantName = tenantName;
//        ProfileServiceManager.profileRestClient = profileClient;
//        ProfileServiceManager.protectedActiveUsers = convertLineToList(protectedActiveUsers);
//    }
//
//    @Value("${crafter.profile.app.tenant.name}")
//    public void setCrafterProfileAppTenantName(String crafterProfileAppTenantName) {
//        ProfileServiceManager.crafterProfileAppTenantName = crafterProfileAppTenantName;
//    }
//
//    public static String getCrafterProfileAppTenantName() {
//        return ProfileServiceManager.crafterProfileAppTenantName;
//    }
//
//    private static List<String> convertLineToList(String list) {
//        List<String> values = new ArrayList<String>();
//        if (list == null || list.length() == 0) {
//            return values;
//        }
//        String[] arrayRoles = list.split(",");
//        for (String role : arrayRoles) {
//            values.add(role.trim());
//        }
//        return values;
//    }
//
//    public static boolean isProtectedToKeepActive(String username) {
//        boolean protectedUsername = false;
//        if (protectedActiveUsers == null || protectedActiveUsers.size() == 0) {
//            return protectedUsername;
//        }
//        for (String u : protectedActiveUsers) {
//            if (u.equals(username)) {
//                protectedUsername = true;
//                break;
//            }
//        }
//        return protectedUsername;
//    }
//
//}
