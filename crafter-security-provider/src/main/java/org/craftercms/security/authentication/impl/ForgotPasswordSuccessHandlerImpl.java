package org.craftercms.security.authentication.impl;

import java.io.IOException;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpStatus;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.SecurityConstants;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.authentication.BaseHandler;
import org.craftercms.security.authentication.ForgotPasswordSuccessHandler;
import org.craftercms.security.exception.CrafterSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ForgotPasswordSuccessHandlerImpl extends BaseHandler implements ForgotPasswordSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordSuccessHandlerImpl.class);

    protected String defaultTargetUrl;

    public ForgotPasswordSuccessHandlerImpl() {
        super();
    }

    /**
     * The target URL to redirect to if it's not possible to redirect to the previous request.
     */
    @Required
    public void setDefaultTargetUrl(String defaultTargetUrl) {
        this.defaultTargetUrl = defaultTargetUrl;
    }

    @Override
    public void onForgotPasswordSuccess(UserProfile profile, RequestContext context) throws CrafterSecurityException,
        IOException {

        HttpSession session = context.getRequest().getSession();
        session.setAttribute(SecurityConstants.PROFILE_FORGOT_PASSWORD, profile);
        if (this.isRedirectRequired) {
            String redirectUrl = context.getRequest().getContextPath() + defaultTargetUrl;

            if (logger.isDebugEnabled()) {
                logger.debug("Redirecting to URL: " + redirectUrl);
            }
            context.getResponse().sendRedirect(redirectUrl);
        } else {
            this.sendResponse(context);
        }


    }

    private void sendResponse(RequestContext context) {
        context.getResponse().setContentType("application/json");
        context.getResponse().setStatus(HttpStatus.SC_OK);
    }

}
