package org.craftercms.security.authentication.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.SecurityConstants;
import org.craftercms.security.authentication.BaseHandler;
import org.craftercms.security.authentication.CreateProfileFailureHandler;
import org.craftercms.security.exception.CrafterSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateProfileFailureHandlerImpl extends BaseHandler implements CreateProfileFailureHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(CreateProfileFailureHandlerImpl.class);

    protected String targetUrl;
    
    public CreateProfileFailureHandlerImpl() {
    	super();
    }

    /**
     * Sets the URL to redirect to.
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

	/* (non-Javadoc)
	 * @see org.craftercms.security.authentication.CreateProfileFailureHandler#onResetPasswordFailure(java.lang.Exception, org.craftercms.security.api.RequestContext, java.lang.String)
	 */
	@Override
	public void onCreateProfileFailure(Exception e, RequestContext context) throws CrafterSecurityException, IOException {
		saveException(e, context);

        if (this.isRedirectRequired && StringUtils.isNotEmpty(targetUrl)) {
            redirectToTargetUrl(context);
        } else {
            sendError(e, context);
        }
		
	}
	
	/**
     * Saves the exception in the session,
     * under the {@link SecurityConstants#CREATE_PROFILE_EXCEPTION}
     * 
     */
    protected void saveException(Exception e, RequestContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("Saving create profile in session for use after redirect");
        }

        HttpSession session = context.getRequest().getSession();
        if (e instanceof Exception) {
            session.setAttribute(SecurityConstants.PROFILE_CREATE_EXCEPTION, new CrafterSecurityException(e.getMessage()));
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
            logger.debug("Error creating a profile'");
        }
        context.getResponse().setContentType("application/json");
        context.getResponse().sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
    }

}
