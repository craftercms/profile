package org.craftercms.security.impl.processors;

import javax.servlet.http.HttpServletRequest;

import org.craftercms.profile.exceptions.PasswordException;
import org.craftercms.security.api.AuthenticationService;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.authentication.ResetPasswordFailureHandler;
import org.craftercms.security.authentication.ResetPasswordSuccessHandler;
import org.craftercms.security.authentication.VerifyAccountFailureHandler;
import org.craftercms.security.authentication.VerifyAccountSuccessHandler;
import org.craftercms.profile.exceptions.VerifyAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Verifies a profile account that was inactive.
 *
 * @author Alvaro Gonzalez
 */
public class VerifyAccountProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(VerifyAccountProcessor.class);

    public static final String DEFAULT_VERIFY_ACCOUNT_URL = "/crafter-verify-account";
    public static final String DEFAULT_VERIFY_ACCOUNT_METHOD = "POST";
    
    public static final String DEFAULT_TOKEN_PARAM = "token";

    protected String verifyAccountUrl;
    protected String verifyAccountMethod;
    
    protected String tokenParameter;
    
    protected AuthenticationService authenticationService;
    protected VerifyAccountSuccessHandler verifyAccountSuccessHandler;
    protected VerifyAccountFailureHandler verifyAccountFailureHandler;

    public VerifyAccountProcessor() {
        this.verifyAccountUrl = DEFAULT_VERIFY_ACCOUNT_URL;
        this.verifyAccountMethod = DEFAULT_VERIFY_ACCOUNT_METHOD;
        
        this.tokenParameter = DEFAULT_TOKEN_PARAM;
    }

   

    /*
     * (non-Javadoc)
     * @see org.craftercms.security.api.RequestSecurityProcessor#processRequest(org.craftercms.security.api.RequestContext, org.craftercms.security.api.RequestSecurityProcessorChain)
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();

        if (isVerifyAccountRequest(request)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Processing login request");
            }


            String token = getToken(request);

            if (token == null) {
                token = "";
            }
            try {
            	if (token.equals("")) {
            		logger.error("Token is a required value to verify the account");
                    throw new SecurityException("Token is a required value to verify the account");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("ResetPassword request for token " + token);
                }

                UserProfile profile = authenticationService.verifyAccount(token);
                
                if (profile.getId() == null) {
                	throw new VerifyAccountException("Error when the account was trying to be verified");
                }

                verifyAccountSuccessHandler.onVerifyAccountSuccess(profile, context);

            } catch (Exception e) {
                logger.error(e.getMessage());
                verifyAccountFailureHandler.onVerifyAccountFailure(e, context, token);
            }
//            } catch (VerifyAccountException e) {
//            	logger.error(e.getMessage());
//            	verifyAccountFailureHandler.onVerifyAccountFailure(e, context, token);
//            }
        } else {
            processorChain.processRequest(context);
        }
    }

    /**
     * Returns the value of the token parameter from the request.
     */
    protected String getToken(HttpServletRequest request) {
        return request.getParameter(tokenParameter);
    }

    protected boolean isVerifyAccountRequest(HttpServletRequest request) {
        return request.getRequestURI().equals(request.getContextPath() + verifyAccountUrl) && request.getMethod()
            .equals(verifyAccountMethod);
    }

    /**
     * Sets the {@link AuthenticationService}, to perform the login against.
     */
    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public String getVerifyAccountUrl() {
		return verifyAccountUrl;
	}

	public void setVerifyAccountUrl(String verifyAccountUrl) {
		this.verifyAccountUrl = verifyAccountUrl;
	}



	public String getVerifyAccountMethod() {
		return verifyAccountMethod;
	}



	public void setVerifyAccountMethod(String verifyAccountMethod) {
		this.verifyAccountMethod = verifyAccountMethod;
	}



	public String getTokenParameter() {
		return tokenParameter;
	}



	public void setTokenParameter(String tokenParameter) {
		this.tokenParameter = tokenParameter;
	}



	public VerifyAccountSuccessHandler getVerifyAccountSuccessHandler() {
		return verifyAccountSuccessHandler;
	}



	public void setVerifyAccountSuccessHandler(
			VerifyAccountSuccessHandler verifyAccountSuccessHandler) {
		this.verifyAccountSuccessHandler = verifyAccountSuccessHandler;
	}



	public VerifyAccountFailureHandler getVerifyAccountFailureHandler() {
		return verifyAccountFailureHandler;
	}



	public void setVerifyAccountFailureHandler(
			VerifyAccountFailureHandler verifyAccountFailureHandler) {
		this.verifyAccountFailureHandler = verifyAccountFailureHandler;
	}



	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

}
