package org.craftercms.security.utils;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for redirection.
 *
 * @author avasquez
 */
public class RedirectUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedirectUtils.class);

    /**
     * Redirects to the specified URL. If the URL starts with '/', the request context path is added.
     *
     * @param request   the request
     * @param response  the response
     * @param url       the URL to redirect to
     */
    public static void redirect(HttpServletRequest request, HttpServletResponse response,
                               String url) throws IOException {
        String redirectUrl;

        if (url.startsWith("/")) {
            redirectUrl = request.getContextPath() + url;
        } else {
            redirectUrl = url;
        }

        logger.debug("Redirecting to URL: {}", redirectUrl);

        response.sendRedirect(redirectUrl);
    }

}
