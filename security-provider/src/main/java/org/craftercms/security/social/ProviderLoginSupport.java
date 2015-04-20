package org.craftercms.security.social;

import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.exception.AuthenticationException;
import org.springframework.util.MultiValueMap;

/**
 * Helper class that allows for executing logins with a social service provider like Facebook, Twitter, LinkedIn, etc.
 *
 * @author avasquez
 */
public interface ProviderLoginSupport {

    /**
     * Starts the OAuth login process. Returns a URL that the app should redirect to.
     *
     * @param tenant        the current tenant the authenticated user belongs too
     * @param providerId    the social service provider ID: facebook, twitter, linkedin
     * @param request       the current request
     *
     * @return the provider specific URL the current app should redirect too.
     */
    String start(String tenant, String providerId, HttpServletRequest request) throws AuthenticationException;

    /**
     * Starts the OAuth login process. Returns a URL that the app should redirect to.
     *
     * @param tenant                the current tenant the authenticated user belongs too
     * @param providerId            the social service provider ID: facebook, twitter, linkedin
     * @param request               the current request
     * @param additionalUrlParams   additional parameters that should be added to the redirect URL
     *
     * @return the provider specific URL the current app should redirect too.
     */
    String start(String tenant, String providerId, HttpServletRequest request,
                 MultiValueMap<String, String> additionalUrlParams) throws AuthenticationException;

    /**
     * Completes the OAuth authentication, returning the resulting {@link Authentication} object, or null if it
     * couldn't be completed.
     *
     * @param tenant        the current tenant the authenticated user belongs too
     * @param providerId    the social service provider ID: facebook, twitter, linkedin
     * @param request       the current request
     *
     * @return the authentication
     */
    Authentication complete(String tenant, String providerId,
                            HttpServletRequest request) throws AuthenticationException;

    /**
     * Completes the OAuth authentication, returning the resulting {@link Authentication} object, or null if it
     * couldn't be completed.
     *
     * @param tenant            the current tenant the authenticated user belongs too
     * @param providerId        the social service provider ID: facebook, twitter, linkedin
     * @param request           the current request
     * @param newUserRoles      roles to add to a new user
     * @param newUserAttributes attributes to add to a new user
     *
     * @return the authentication
     */
    Authentication complete(String tenant, String providerId, HttpServletRequest request, Set<String> newUserRoles,
                            Map<String, Object> newUserAttributes) throws AuthenticationException;

}

