package org.craftercms.profile.api.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.VerificationToken;
import org.craftercms.profile.api.exceptions.ProfileException;

/**
 * Service for handling profiles.
 *
 * @author avasquez
 */
public interface ProfileService {

    /**
     * Creates a new profile for a specific tenant name.
     *
     * @param tenantName        the name of the tenant to add the profile to
     * @param username          the profile's username
     * @param password          the profile's password
     * @param email             the profile's email
     * @param enabled           if the profile should be enabled or not
     * @param roles             the profile's roles (optional)
     * @param attributes        the additional attributes to add to the profile (optional)
     * @param verificationUrl   the URL (sans token) the user needs to go in case it needs to verify the created
     *                          profile (verification depends on tenant) (optional).
     * @return the newly created profile
     */
    Profile createProfile(String tenantName, String username, String password, String email, boolean enabled,
                          Set<String> roles, Map<String, Object> attributes, String verificationUrl)
            throws ProfileException;

    /**
     * Updates the profile's info.
     *
     * @param profileId             the profile's ID
     * @param username              the new username for the profile, or null if it shouldn't be updated
     * @param password              the new password for the profile, or null if it shouldn't be updated
     * @param email                 the new email for the profile, or null if it shouldn't be updated
     * @param enabled               if the profile should be enabled or not, or null if it shouldn't be updated
     * @param roles                 the new roles for the profile, or null if the roles shouldn't be updated
     * @param attributes            the attributes to update, or null if no attribute should be updated
     * @param attributesToReturn    the names of the attributes to return (null to return all attributes)
     *
     * @return the updated profile
     */
    Profile updateProfile(String profileId, String username, String password, String email, Boolean enabled,
                          Set<String> roles, Map<String, Object> attributes, String... attributesToReturn)
            throws ProfileException;

    /**
     * Sets the profile as verified if the verification token is valid.
     *
     * @param verificationTokenId   the verification token ID
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return
     *                              all attributes)
     *
     * @return the updated profile associated to the token
     */
    Profile verifyProfile(String verificationTokenId, String... attributesToReturn) throws ProfileException;

    /**
     * Enables a profile.
     *
     * @param profileId             the profile's ID
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return
     *                              all attributes)
     *
     * @return the updated profile
     */
    Profile enableProfile(String profileId, String... attributesToReturn) throws ProfileException;

    /**
     * Disables a profile.
     *
     * @param profileId             the profile's ID
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return
     *                              all attributes)
     *
     * @return the updated profile
     */
    Profile disableProfile(String profileId, String... attributesToReturn) throws ProfileException;

    /**
     * Assigns roles to the profile.
     *
     * @param profileId             the profile's ID
     * @param roles                 the roles to assign
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return
     *                              all attributes)
     *
     * @return the updated profile
     */
    Profile addRoles(String profileId, Collection<String> roles, String... attributesToReturn) throws ProfileException;

    /**
     * Removes assigned roles from a profile.
     *
     * @param profileId             the profile's ID
     * @param roles                 the roles to remove
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return
     *                              all attributes)
     *
     * @return the updated profile
     */
    Profile removeRoles(String profileId, Collection<String> roles, String... attributesToReturn)
            throws ProfileException;

    /**
     * Returns the attributes of a profile.
     *
     * @param profileId             the profile's ID
     * @param attributesToReturn    the names of the attributes to return (null to return all attributes)
     *
     * @return the profile's attributes
     */
    Map<String, Object> getAttributes(String profileId, String... attributesToReturn) throws ProfileException;

    /**
     * Updates the attributes of a profile, by merging the specified attributes with the existing attributes.
     *
     * @param profileId             the profile's ID
     * @param attributes            the new attributes
     * @param attributesToReturn    the names of the attributes to return withe the profile (null to return all
     *                              attributes)
     *
     * @return the updated profile
     */
    Profile updateAttributes(String profileId, Map<String, Object> attributes, String... attributesToReturn)
            throws ProfileException;

    /**
     * Removes a list of attributes of a profile.
     *
     * @param profileId             the profile's ID
     * @param attributeNames        the names of the attributes to remove
     * @param attributesToReturn    the names of the attributes to return withe the profile (null to return all
     *                              attributes)
     *
     * @return the updated profile
     */
    Profile removeAttributes(String profileId, Collection<String> attributeNames, String... attributesToReturn)
            throws ProfileException;

    /**
     * Deletes a profile.
     *
     * @param profileId     the profile's ID
     */
    void deleteProfile(String profileId) throws ProfileException;

    /**
     * Returns the single profile that matches the specified query
     *
     * @param tenantName            the tenant's name
     * @param query                 the Mongo query used to search for the profiles. Must not contain the $where
     *                              operator, the tenant's name (already specified) or any non-readable attribute
     *                              by the application
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return all
     *                              attributes)
     *
     * @return the profile, or null if not found
     */
    Profile getProfileByQuery(String tenantName, String query, String... attributesToReturn) throws ProfileException;

    /**
     * Returns the profile for the specified ID.
     *
     * @param profileId             the profile's ID
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return all
     *                              attributes)
     *
     * @return the profile, or null if not found
     */
    Profile getProfile(String profileId, String... attributesToReturn) throws ProfileException;

    /**
     * Returns the user for the specified tenant and username
     *
     * @param tenantName            the tenant's name
     * @param username              the profile's username
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return all
     *                              attributes)
     *
     * @return the profile, or null if not found
     */
    Profile getProfileByUsername(String tenantName, String username, String... attributesToReturn)
            throws ProfileException;

    /**
     * Returns the profile for the specified ticket.
     *
     * @param ticketId              the ID ticket of the authenticated profile
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return all
     *                              attributes)
     *
     * @return the profile, or null if not found
     */
    Profile getProfileByTicket(String ticketId, String... attributesToReturn) throws ProfileException;

    /**
     * Returns the number of profiles of the specified tenant.
     *
     * @param tenantName    the tenant's name
     *
     * @return the number of profiles of the specified tenant
     */
    long getProfileCount(String tenantName) throws ProfileException;

    /**
     * Returns the number of profiles that match the query for the specified tenant.
     *
     * @param tenantName    the tenant's name
     * @param query         the Mongo query used to search for the profiles. Must not contain the $where
     *                      operator, the tenant's name (already specified) or any non-readable attribute
     *                      by the application
     *
     * @return  the number of profiles of the specified tenant
     */
    long getProfileCountByQuery(String tenantName, String query) throws ProfileException;

    /**
     * Returns the profiles that match the specified query.
     *
     * @param tenantName            the tenant's name
     * @param query                 the Mongo query used to search for the profiles. Must not contain the $where
     *                              operator, the tenant's name (already specified) or any non-readable attribute
     *                              by the application
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param start                 from the entire list of results, the position where the actual results should start
     *                              (useful for pagination) (optional)
     * @param count                 the number of profiles to return (optional)
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return all
     *                              attributes)
     *
     * @return the list of profiles found, or null if none match the query
     */
    List<Profile> getProfilesByQuery(String tenantName, String query, String sortBy, SortOrder sortOrder, Integer start,
                                     Integer count, String... attributesToReturn) throws ProfileException;

    /**
     * Returns a list of profiles for the specified list of IDs.
     *
     * @param profileIds            the IDs of the profiles to look for
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param attributesToReturn    the names of the attributes to return for each profile (null to return all
     *                              attributes)
     *
     * @return the list of profiles (can be smaller than the list of ids if some where not found)
     */
    List<Profile> getProfilesByIds(List<String> profileIds, String sortBy, SortOrder sortOrder,
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
    List<Profile> getProfileRange(String tenantName, String sortBy, SortOrder sortOrder, Integer start, Integer count,
                                  String... attributesToReturn) throws ProfileException;

    /**
     * Returns a list of profiles for a specific role and tenant.
     *
     * @param tenantName            the tenant's name
     * @param role                  the role's name
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param attributesToReturn    the names of the attributes to return for each profile (null to return all
     *                              attributes)
     *
     * @return the list of profiles
     */
    List<Profile> getProfilesByRole(String tenantName, String role, String sortBy, SortOrder sortOrder,
                                    String... attributesToReturn) throws ProfileException;

    /**
     * Returns the list of profiles that have the given attribute, with any value
     *
     * @param tenantName            the tenant's name
     * @param attributeName         the name of the attribute profiles must have
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param attributesToReturn    the names of the attributes to return for each profile (null to return all
     *                              attributes)
     *
     * @return the list of profiles
     */
    List<Profile> getProfilesByExistingAttribute(String tenantName, String attributeName, String sortBy,
                                                 SortOrder sortOrder, String... attributesToReturn)
            throws ProfileException;

    /**
     * Returns the list of profiles that have the given attribute with the given value.
     *
     * @param tenantName            the tenant's name
     * @param attributeName         the name of the attribute profiles must have
     * @param attributeValue        the value of the attribute profiles must have
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param attributesToReturn    the names of the attributes to return for each profile (null to return all
     *                              attributes)
     *
     * @return the list of profiles
     */
    List<Profile> getProfilesByAttributeValue(String tenantName, String attributeName, String attributeValue,
                                              String sortBy, SortOrder sortOrder, String... attributesToReturn)
            throws ProfileException;

    /**
     * Sends an email to the profile's user to indicate that the password needs to be reset.
     *
     * @param profileId             the profile's ID
     * @param resetPasswordUrl      the base URL to use to build the final URL the profile will use to reset
     *                              their password.
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return all
     *                              attributes)
     *
     * @return the updated profile
     */
    Profile resetPassword(String profileId, String resetPasswordUrl, String... attributesToReturn)
            throws ProfileException;

    /**
     * Changes a profile's password, after a reset request has been sent.
     *
     * @param resetTokenId          the reset token ID
     * @param newPassword           the new password
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return all
     *                              attributes)
     *
     * @return the updated profile
     */
    Profile changePassword(String resetTokenId, String newPassword, String... attributesToReturn)
            throws ProfileException;

    /**
     * Creates a token that can be sent to the user in an email as a link. After the user clicks the link, the token
     * then can be passed to {@link #verifyProfile(String, String...)} or {@link #changePassword(String, String,
     * String...)} to verify that the user agrees.
     *
     * @param profileId the profile ID of the user that needs to be contacted
     *
     * @return the created token
     */
    VerificationToken createVerificationToken(String profileId) throws ProfileException;

    /**
     * Deletes a verification token when it's not needed anymore (not necessary to call if
     * {@link #verifyProfile(String, String...)} or {@link #changePassword(String, String, String...)}, since they
     * already delete the token.
     *
     * @param tokenId the ID of the token to delete
     */
    void deleteVerificationToken(String tokenId) throws ProfileException;

}
