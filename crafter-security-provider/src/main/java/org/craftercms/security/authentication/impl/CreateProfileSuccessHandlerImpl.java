package org.craftercms.security.authentication.impl;

import java.io.IOException;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.SecurityConstants;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.authentication.BaseHandler;
import org.craftercms.security.authentication.CreateProfileSuccessHandler;
import org.craftercms.security.exception.CrafterSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class CreateProfileSuccessHandlerImpl extends BaseHandler implements CreateProfileSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CreateProfileSuccessHandlerImpl.class);

    protected String defaultTargetUrl;

    public CreateProfileSuccessHandlerImpl() {
        super();
    }

    /* (non-Javadoc)
     * @see org.craftercms.security.authentication.CreateProfileFailureHandler#onResetPasswordFailure(java.lang
     * .Exception, org.craftercms.security.api.RequestContext, java.lang.String)
     */
    @Override
    public void onCreateProfileSuccess(UserProfile profile, RequestContext context) throws CrafterSecurityException,
        IOException {
        HttpSession session = context.getRequest().getSession();
        session.setAttribute(SecurityConstants.PROFILE_FORGOT_PASSWORD, profile);
        if (isRedirectRequired) {
            String redirectUrl = context.getRequest().getContextPath() + defaultTargetUrl;

            if (logger.isDebugEnabled()) {
                logger.debug("Redirecting to URL: " + redirectUrl);
            }

            context.getResponse().sendRedirect(redirectUrl);
        } else {
            sendResponse(context, profile);
        }

    }

    /**
     * The target URL to redirect
     */
    @Required
    public void setDefaultTargetUrl(String defaultTargetUrl) {
        this.defaultTargetUrl = defaultTargetUrl;
    }

    private void sendResponse(RequestContext context, UserProfile profile) {
        try {
            context.getResponse().setContentType("application/json");
            context.getResponse().setStatus(HttpStatus.SC_CREATED);
            ObjectMapper mapper = new ObjectMapper();
            context.getResponse().getWriter().write(mapper.writeValueAsString(profile));
        } catch (IOException e) {
            this.logger.error(e.getMessage());
            context.getResponse().setStatus(HttpStatus.SC_CREATED, "Unable to include profile data");
        }

    }

}
