package org.craftercms.profile.utils;

import javax.servlet.http.HttpServletRequest;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.AccessToken;

/**
 * Utility methods for {@link org.craftercms.profile.api.AccessToken}s.
 *
 * @author avasquez
 */
public class AccessTokenUtils {

    public static final String ACCESS_TOKE_ATTRIBUTE_NAME = "accessToken";

    public static AccessToken getCurrentToken() {
        return getAccessToken(RequestContext.getCurrent().getRequest());
    }

    public static void setCurrentToken(AccessToken accessToken) {
        setAccessToken(RequestContext.getCurrent().getRequest(), accessToken);
    }

    public static AccessToken getAccessToken(HttpServletRequest request) {
        return (AccessToken)request.getAttribute(ACCESS_TOKE_ATTRIBUTE_NAME);
    }

    public static void setAccessToken(HttpServletRequest request, AccessToken accessToken) {
        request.setAttribute(ACCESS_TOKE_ATTRIBUTE_NAME, accessToken);
    }

    private AccessTokenUtils() {
    }

}
