package org.craftercms.profile.api.service;

import org.craftercms.profile.api.User;
import org.craftercms.profile.api.utils.SortOrder;

import java.util.List;
import java.util.Map;

/**
 * Service for handling users.
 *
 * @author avasquez
 */
public interface UserService {

    /**
     * Creates a new user for the current tenant.
     * @param username          the user's username
     * @param password          the user's password
     * @param email             the user's email
     * @param enabled           if the user is enabled or not
     * @param roles             the user's roles
     * @param groups            the user's groups
     * @param verifyAccountUrl  the url the user needs to go in case it needs to verify the created account
     *                          (verification depends on tenant).
     *
     * @return the newly created user
     */
    User createUser(String username, String password, String email, boolean enabled, List<String> roles,
                    List<String> groups, String verifyAccountUrl);

    /**
     * Update the user's info.
     *
     * @param username  the user's username
     * @param password  the new password for the user, or null if the password shouldn't be updated
     * @param email     the new email for the user, or null if the email shouldn't be updated
     * @param enabled   if the user should be enabled or not
     * @param roles     the new roles for the user, or null if the roles shouldn't be updated
     * @param groups    the new groups for the user, or null if the groups shouldn't be updated
     *
     * @return the updated user
     */
    User updateUser(String username, String password, String email, boolean enabled, List<String> roles,
                    List<String> groups);

    /**
     * Enables a user
     *
     * @param username  the username of the user to enable
     *
     * @return the updated user
     */
    User enableUser(String username);

    /**
     * Disables a user
     *
     * @param username  the user's username
     *
     * @return the updated user
     */
    User disableUser(String username);

    /**
     * Assigns roles to the user.
     *
     * @param username  the user's username
     * @param roles     the roles to assign
     *
     * @return the updated user
     */
    User addRoles(String username, List<String> roles);

    /**
     * Removes assigned roles from a user.
     *
     * @param username  the user's username
     * @param roles     the roles to remove
     *
     * @return the updated user
     */
    User removeRoles(String username, List<String> roles);

    /**
     * Assigns groups to the user.
     *
     * @param username  the user's username
     * @param groups    the groups to assign
     *
     * @return the updated user
     */
    User addGroups(String username, List<String> groups);

    /**
     * Removes assigned groups from a user.
     *
     * @param username  the user's username
     * @param groups    the groups to remove
     *
     * @return the updated user
     */
    User removeGroups(String username, List<String> groups);

    /**
     * Returns the attributes of a user.
     *
     * @param username      the user's username
     * @param attributes    the names of the attributes to return, or null to return all attributes
     *
     * @return  the user's attributes
     */
    Map<String, Object> getAttributes(String username, List<String> attributes);

    /**
     * Updates the attributes of a user. Normally, the new attributes are merged with the existing attributes,
     * unless the {@code replace} flag is set, which will replace the existing attributes with the new attributes.
     *
     * @param username      the user's username
     * @param attributes    the new attributes
     * @param replace       if the existing attributes should be replaced with the new ones instead of merged
     *
     * @return the updated attributes
     */
    Map<String, Object> updateAttributes(String username, Map<String, Object> attributes, boolean replace);

    /**
     * Deletes some attributes of a user.
     *
     * @param username      the user's username
     * @param attributes    the attributes to delete
     */
    void deleteAttributes(String username, List<String> attributes);

    /**
     * Deletes a user.
     *
     * @param username  the user's username
     */
    void deleteUser(String username);

    /**
     * Returns the user for the specified username.
     *
     * @param username      the user's username
     * @param attributes    the names of the attributes to include, or null to include all attributes
     *
     * @return  the user, or null if not found
     */
    User getUser(String username, List<String> attributes);

    /**
     * Returns the user for the specified ticket.
     *
     * @param ticket        the ticket of the authenticated user
     * @param attributes    the names of the attributes to include, or null to include all attributes
     *
     * @return  the user, or null if not found
     */
    User getUserByTicket(String ticket, List<String> attributes);

    /**
     * Returns the number of users of the specified tenant.
     *
     * @param tenant    the tenant's name
     *
     * @return the number of users of the specified tenant
     */
    int getUserCount(String tenant);

    /**
     * Returns a list of users for the specified list of usernames.
     *
     * @param usernames     the usernames of the users to look for
     * @param attributes    the names of the user attributes to include, or null to include all attributes
     * @param sortBy        user attribute to sort the list by (optional)
     * @param sortOrder     the sort order (either ASC or DESC) (optional)
     *
     * @return the list of users (can be smaller than the list of usernames if some where not found)
     */
    List<User> getUsers(List<String> usernames, List<String> attributes, String sortBy, SortOrder sortOrder);

    /**
     * Returns a list of all users for the current tenant.
     *
     * @param attributes    the names of the user attributes to include, or null to include all attributes
     * @param sortBy        user attribute to sort the list by (optional)
     * @param sortOrder     the sort order (either ASC or DESC) (optional)
     * @param start         from the entire list of results, the position where the actual results should start
     *                      (useful for pagination) (optional)
     * @param count         the number of users to return (optional)
     *
     * @return
     */
    List<User> getAllUsers(List<String> attributes, String sortBy, String sortOrder, Integer start, Integer count);

    List<User> getUsersByRole(String role, List<String> attributes, String sortBy, SortOrder sortOrder,
                              Integer start, Integer count);

    List<User> getUsersByGroup(String group, List<String> attributes, String sortBy, SortOrder sortOrder,
                               Integer start, Integer count);

    User forgotPassword(String username, String changePasswordUrl);

    User resetPassword(String token, String newPassword);

}
