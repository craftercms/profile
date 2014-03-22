package org.craftercms.profile.api.services;

import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.utils.SortOrder;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service for handling profiles.
 *
 * @author avasquez
 */
public interface ProfileService {

    /**
     * Creates a new profile for a specific tenantName.
     *
     * @param tenantName            the name of the tenant to add the profile to
     * @param username              the profile's username
     * @param password              the profile's password
     * @param email                 the profile's email
     * @param enabled               if the profile is enabled or not
     * @param roles                 the profile's roles
     * @param verificationBaseUrl   the base url the user needs to go in case it needs to verify the created profile
     *                              (verification depends on tenant).
     * @return the newly created profile
     */
    Profile createProfile(String tenantName, String username, String password, String email, boolean enabled,
                          Set<String> roles, String verificationBaseUrl) throws ProfileException;

    /**
     * Update the profile's info.
     *
     * @param tenantName            the tenantName's name
     * @param profileId             the profile's ID
     * @param username              the profile's username
     * @param password              the new password for the profile, or null if the password shouldn't be updated
     * @param email                 the new email for the profile, or null if the email shouldn't be updated
     * @param enabled               if the profile should be enabled or not
     * @param roles                 the new roles for the profile, or null if the roles shouldn't be updated
     *
     * @return the updated profile
     */
    void updateProfile(String tenantName, String profileId, String username, String password, String email,
                          Boolean enabled, Set<String> roles) throws ProfileException;

    /**
     * Enables a profile.
     *
     * @param tenantName            the tenant's name
     * @param profileId             the profile's ID
     */
    void enableProfile(String tenantName, String profileId) throws ProfileException;

    /**
     * Disables a profile.
     *
     * @param tenantName            the tenant's name
     * @param profileId             the profile's ID
     */
    void disableProfile(String tenantName, String profileId) throws ProfileException;

    /**
     * Assigns roles to the profile.
     *
     * @param tenantName            the tenant's name
     * @param profileId             the profile's ID
     * @param roles                 the roles to assign
     *
     * @return the updated profile
     */
    void addRoles(String tenantName, String profileId, Set<String> roles) throws ProfileException;

    /**
     * Removes assigned roles from a profile.
     *
     * @param tenantName            the tenant's name
     * @param profileId             the profile's ID
     * @param roles                 the roles to remove
     */
    void removeRoles(String tenantName, String profileId, Set<String> roles) throws ProfileException;

    /**
     * Returns the attributes of a profile.
     *
     * @param tenantName            the tenant's name
     * @param profileId             the profile's ID
     * @param attributesToReturn    the names of the attributes to return (null to return all attributes)
     *
     * @return the profile's attributes
     */
    Map<String, Object> getAttributes(String tenantName, String profileId, String... attributesToReturn)
            throws ProfileException;

    /**
     * Updates the attributes of a profile, by merging the specified attributes with the existing attributes.
     *
     * @param tenantName    the tenant's name
     * @param profileId     the profile's ID
     * @param attributes    the new attributes
     */
    void updateAttributes(String tenantName, String profileId, Map<String, Object> attributes) throws ProfileException;

    /**
     * Deletes a list of attributes of a profile.
     *
     * @param tenantName        the tenant's name
     * @param profileId         the profile's ID
     * @param attributeNames    the names of the attributes to delete
     */
    void deleteAttributes(String tenantName, String profileId, String... attributeNames) throws ProfileException;

    /**
     * Deletes a profile.
     *
     * @param tenantName    the tenant's name
     * @param profileId     the profile's ID
     */
    void deleteProfile(String tenantName, String profileId) throws ProfileException;

    /**
     * Returns the profile for the specified id.
     *
     * @param tenantName            the tenant's name
     * @param profileId             the profile's ID
     * @param attributesToReturn    the names of the attributes to return (null to return all attributes)
     *
     * @return the profile, or null if not found
     */
    Profile getProfile(String tenantName, String profileId, String... attributesToReturn) throws ProfileException;

    /**
     * Returns the user for the specified tenant and username
     *
     * @param tenantName            the tenant's name
     * @param username              the profile's username
     * @param attributesToReturn    the names of the attributes to return (null to return all attributes)
     *
     * @return the profile, or null if not found
     */
    Profile getProfileByUsername(String tenantName, String username, String... attributesToReturn)
            throws ProfileException;

    /**
     * Returns the profile for the specified ticket.
     *
     * @param tenantName            the tenant's name
     * @param ticket                the ticket of the authenticated profile
     * @param attributesToReturn    the names of the attributes to return (null to return all attributes)
     *
     * @return the profile, or null if not found
     */
    Profile getProfileByTicket(String tenantName, String ticket, String... attributesToReturn) throws ProfileException;

    /**
     * Returns the number of profiles of the specified tenant.
     *
     * @param tenantName    the tenant's name
     *
     * @return the number of profiles of the specified tenant
     */
    long getProfileCount(String tenantName) throws ProfileException;

    /**
     * Returns a list of profiles for the specified list of ids.
     *
     * @param tenantName            the tenant's name
     * @param profileIds            the ids of the profiles to look for
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param attributesToReturn    the names of the attributes to return for each profile (null to return all
     *                              attributes)
     *
     * @return the list of profiles (can be smaller than the list of ids if some where not found)
     */
    List<Profile> getProfiles(String tenantName, List<String> profileIds, String sortBy, SortOrder sortOrder,
                              String... attributesToReturn) throws ProfileException;

    /**
     * Returns a range of profiles for the specified tenant.
     *
     * @param tenantName            the tenant's name
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param start                 from the entire list of results, the position where the actual results should start
     *                              (useful for pagination) (optional)
     * @param count                 the number of profiles to return (optional)
     * @param attributesToReturn    the names of the attributes to return for each profile (null to return all
     *                              attributes)
     *
     * @return the list of profiles
     */
    List<Profile> getProfileRange(String tenantName, String sortBy, String sortOrder, Integer start, Integer count,
                                  String... attributesToReturn) throws ProfileException;

    /**
     * Returns a list of profiles for a specific role and tenant.
     *
     * @param tenantName            the tenant's name
     * @param role                  the role's name
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param start                 from the entire list of results, the position where the actual results should start
     *                              (useful for pagination) (optional)
     * @param count                 the number of profiles to return (optional)
     * @param attributesToReturn    the names of the attributes to return for each profile (null to return all
     *                              attributes)
     *
     * @return the list of profiles
     */
    List<Profile> getProfilesByRole(String tenantName, String role, String sortBy, SortOrder sortOrder, Integer start,
                                    Integer count, String... attributesToReturn) throws ProfileException;

    /**
     * Common forgot password functionality: sends the profile an email with an URL to reset their password.
     *
     * @param tenantName        the tenant's name
     * @param profileId         the profile's ID
     * @param changePasswordUrl the base URL to use to build the final URL the profile will use to reset their password.
     */
    void forgotPassword(String tenantName, String profileId, String changePasswordUrl) throws ProfileException;

    /**
     * Resets a profile's password.
     *
     * @param tenantName    the tenant's name
     * @param resetToken    the encrypted token used to identify the profile and the time the password reset was
     *                      initiated
     * @param newPassword   the new password
     */
    void resetPassword(String tenantName, String resetToken, String newPassword) throws ProfileException;

}
