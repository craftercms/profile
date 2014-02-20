package org.craftercms.profile.api.services;

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
     * Creates a new user for a specific tenant.
     *
     * @param tenant            the tenant to add the user to
     * @param userId            the user's ID
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
    User createUser(String tenant, String userId, String password, String email, boolean enabled, List<String> roles,
                    List<String> groups, String verifyAccountUrl);

    /**
     * Update the user's info.
     *
     * @param tenant    the tenant's name
     * @param userId    the user's ID
     * @param password  the new password for the user, or null if the password shouldn't be updated
     * @param email     the new email for the user, or null if the email shouldn't be updated
     * @param enabled   if the user should be enabled or not
     * @param roles     the new roles for the user, or null if the roles shouldn't be updated
     * @param groups    the new groups for the user, or null if the groups shouldn't be updated
     *
     * @return the updated user
     */
    User updateUser(String tenant, String userId, String password, String email, boolean enabled, List<String> roles,
                    List<String> groups);

    /**
     * Enables a user.
     *
     * @param tenant    the tenant's name
     * @param userId    the user's ID
     *
     * @return the enabled user
     */
    User enableUser(String tenant, String userId);

    /**
     * Disables a user.
     *
     * @param tenant    the tenant's name
     * @param userId    the user's ID
     *
     * @return the disabled user
     */
    User disableUser(String tenant, String userId);

    /**
     * Assigns roles to the user.
     *
     * @param tenant    the tenant's name
     * @param userId    the user's ID
     * @param roles     the roles to assign
     *
     * @return the updated user
     */
    User addRoles(String tenant, String userId, List<String> roles);

    /**
     * Removes assigned roles from a user.
     *
     * @param tenant    the tenant's name
     * @param userId    the user's ID
     * @param roles     the roles to remove
     *
     * @return the updated user
     */
    User removeRoles(String tenant, String userId, List<String> roles);

    /**
     * Returns the attributes of a user.
     *
     * @param tenant    the tenant's name
     * @param userId    the user's ID
     *
     * @return  the user's attributes
     */
    Map<String, Object> getAttributes(String tenant, String userId);

    /**
     * Updates the attributes of a user, by merging the specified attributes with the existing attributes.
     *
     * @param tenant        the tenant's name
     * @param userId        the user's ID
     * @param attributes    the new attributes
     *
     * @return the updated attributes
     */
    Map<String, Object> updateAttributes(String tenant, String userId, Map<String, Object> attributes);

    // TODO: Delete attributes (pass a string with a dot notation for sub-attributes, like a.b.c?)"

    /**
     * Deletes a user.
     *
     * @param tenant    the tenant's name
     * @param userId    the user's ID
     *                  
     * @return the deleted user
     */
    User deleteUser(String tenant, String userId);

    /**
     * Returns the user for the specified id.
     *
     * @param tenant    the tenant's name
     * @param userId    the user's ID
     *
     * @return  the user, or null if not found
     */
    User getUser(String tenant, String userId);

    /**
     * Returns the user for the specified tenant and username
     *
     * @param tenant    the tenant's name
     * @param username  the user's username
     *
     * @return the user, or null if not found
     */
    User getUserByTenantAndUsername(String tenant, String username);

    /**
     * Returns the user for the specified ticket.
     *
     * @param tenant    the tenant's name
     * @param ticket    the ticket of the authenticated user
     *
     * @return  the user, or null if not found
     */
    User getUserByTicket(String tenant, String ticket);

    /**
     * Returns the number of users of the specified tenant.
     *
     * @param tenant    the tenant's name
     *
     * @return the number of users of the specified tenant
     */
    int getUserCount(String tenant);

    /**
     * Returns a list of users for the specified list of ids.
     *
     * @param tenant    the tenant's name
     * @param userIds   the ids of the users to look for
     * @param sortBy    user attribute to sort the list by (optional)
     * @param sortOrder the sort order (either ASC or DESC) (optional)
     *
     * @return the list of users (can be smaller than the list of ids if some where not found)
     */
    List<User> getUsers(String tenant, List<String> userIds, String sortBy, SortOrder sortOrder);

    /**
     * Returns a list of all users for the specified tenant.
     *
     * @param tenant    the tenant's name
     * @param sortBy    user attribute to sort the list by (optional)
     * @param sortOrder the sort order (either ASC or DESC) (optional)
     * @param start     from the entire list of results, the position where the actual results should start
     *                  (useful for pagination) (optional)
     * @param count     the number of users to return (optional)
     *
     * @return the list of users
     */
    List<User> getAllUsers(String tenant, String sortBy, String sortOrder, Integer start, Integer count);

    /**
     * Returns a list of users for a specific role and tenant.
     *
     * @param tenant    the tenant's name
     * @param role      the role's name
     * @param sortBy    user attribute to sort the list by (optional)
     * @param sortOrder the sort order (either ASC or DESC) (optional)
     * @param start     from the entire list of results, the position where the actual results should start
     *                  (useful for pagination) (optional)
     * @param count     the number of users to return (optional)
     *
     * @return the list of users
     */
    List<User> getUsersByRole(String tenant, String role, String sortBy, SortOrder sortOrder, Integer start,
                              Integer count);

    /**
     * Returns a list of users for a specific group and tenant.
     *
     * @param tenant    the tenant's name
     * @param group     the group's name
     * @param sortBy    user attribute to sort the list by (optional)
     * @param sortOrder the sort order (either ASC or DESC) (optional)
     * @param start     from the entire list of results, the position where the actual results should start
     *                  (useful for pagination) (optional)
     * @param count     the number of users to return (optional)
     *
     * @return the list of users
     */
    List<User> getUsersByGroup(String tenant, String group, String sortBy, SortOrder sortOrder, Integer start,
                               Integer count);

    /**
     * Common forgot password functionality: sends the user an email with an URL to reset their password.
     *
     * @param tenant    the tenant's name
     * @param userId            the user's ID
     * @param changePasswordUrl the base URL to use to build the final URL the user will use to reset their password.
     */
    void forgotPassword(String tenant, String userId, String changePasswordUrl);

    /**
     * Resets a user's password.
     *
     * @param tenant    the tenant's name
     * @param resetToken    the encrypted token used to identify the user and the time the password reset was
     *                      initiated
     * @param newPassword   the new password
     */
    void resetPassword(String tenant, String resetToken, String newPassword);

}
