package org.craftercms.security.authentication.impl;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.SecurityConstants;
import org.craftercms.security.authentication.ForgotPasswordFailureHandler;
import org.craftercms.security.exception.CrafterSecurityException;
import org.craftercms.security.exception.PasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForgotPasswordFailureHandlerImpl implements ForgotPasswordFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginFailureHandlerImpl.class);

    protected String targetUrl;

    /**
     * Sets the URL to redirect to.
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Override
    public void onForgotPasswordFailure(Exception e, RequestContext context) throws CrafterSecurityException,
        IOException {

        saveException(e, context);

        if (StringUtils.isNotEmpty(targetUrl)) {
            redirectToTargetUrl(context);
        } else {
            sendError(e, context);
        }

    }

    /**
     * Saves the exception in the session,
     * under the {@link SecurityConstants#FORGOT_PASSWORD_EXCEPTION}
     * 
     */
    protected void saveException(Exception e, RequestContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("Saving forgot password exception in session for use after redirect");
        }

        HttpSession session = context.getRequest().getSession();
        if (e instanceof Exception) {
            session.setAttribute(SecurityConstants.FORGOT_PASSWORD_EXCEPTION, new PasswordException(e.getMessage()));
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

    protected void sendError(Exception e, RequestContext context) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Forgot password error");
        }
        
        context.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }

}
