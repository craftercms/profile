package org.craftercms.security.authentication.impl;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.SecurityConstants;
import org.craftercms.security.authentication.BaseHandler;
import org.craftercms.security.authentication.VerifyAccountFailureHandler;
import org.craftercms.security.exception.CrafterSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyAccountFailureHandlerImpl extends BaseHandler implements VerifyAccountFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginFailureHandlerImpl.class);

    protected String targetUrl;

    public VerifyAccountFailureHandlerImpl() {
        super();
    }

    /**
     * Sets the URL to redirect to.
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Override
    public void onVerifyAccountFailure(Exception e, RequestContext context,
                                       String token) throws CrafterSecurityException, IOException {
        saveException(e, context);

        if (isRedirectRequired && StringUtils.isNotEmpty(targetUrl)) {
            updateTargetUrl(token);
            redirectToTargetUrl(context);
        } else {
            sendError(e, context);
        }
    }

    /**
     * Saves the exception in the session,
     * under the {@link SecurityConstants#VERIFY_ACCOUNT_EXCEPTION}
     */
    protected void saveException(Exception e, RequestContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("Saving authentication exception in session for use after redirect");
        }

        HttpSession session = context.getRequest().getSession();
        if (e instanceof Exception) {
            session.setAttribute(SecurityConstants.VERIFY_ACCOUNT_EXCEPTION, new SecurityException(e.getMessage()));
        }
    }

    /**
     * Redirects to the target URL.
     */
    protected void redirectToTargetUrl(RequestContext context) throws IOException {
        String redirectUrl = context.getRequest().getContextPath() + targetUrl;

        if (logger.isDebugEnabled()) {
            logger.debug("Redirecting to URL: " + redirectUrl);
        }

        context.getResponse().sendRedirect(redirectUrl);
    }

    /**
     * Sends a error.
     */
    protected void sendError(Exception e, RequestContext context) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Sending error " + e.getMessage());
        }

        context.getResponse().sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
    }

    private void updateTargetUrl(String token) {
        if (token != null && !token.equals("") && targetUrl.contains("?")) {
            targetUrl = targetUrl + "&token=" + token;
        } else if (token != null && !token.equals("") && !targetUrl.contains("?")) {
            targetUrl = targetUrl + "?token=" + token;
        }

    }


}
