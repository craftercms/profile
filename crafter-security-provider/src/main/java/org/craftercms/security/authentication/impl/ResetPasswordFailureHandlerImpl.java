package org.craftercms.security.authentication.impl;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.SecurityConstants;
import org.craftercms.security.authentication.BaseHandler;
import org.craftercms.security.authentication.ResetPasswordFailureHandler;
import org.craftercms.security.exception.CrafterSecurityException;
import org.craftercms.security.exception.PasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetPasswordFailureHandlerImpl extends BaseHandler implements ResetPasswordFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginFailureHandlerImpl.class);

    protected String targetUrl;

    public ResetPasswordFailureHandlerImpl() {
        super();
    }

    /**
     * Sets the URL to redirect to.
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Override
    public void onResetPasswordFailure(Exception e, RequestContext context,
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
     * Saves the authentication exception in the session,
     * under the {@link SecurityConstants#RESET_PASSWORD_EXCEPTION}
     */
    protected void saveException(Exception e, RequestContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("Saving authentication exception in session for use after redirect");
        }

        HttpSession session = context.getRequest().getSession();
        if (e instanceof Exception) {
            session.setAttribute(SecurityConstants.RESET_PASSWORD_EXCEPTION, new PasswordException(e.getMessage()));
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
     * Sends a 401 UNAUTHORIZED error.
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
