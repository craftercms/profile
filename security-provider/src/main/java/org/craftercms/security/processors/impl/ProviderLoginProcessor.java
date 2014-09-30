package org.craftercms.security.processors.impl;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.crypto.TextEncryptor;
import org.craftercms.commons.http.HttpUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.social.utils.ConnectionUtils;
import org.craftercms.profile.social.utils.TenantResolver;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.LoginFailureHandler;
import org.craftercms.security.authentication.LoginSuccessHandler;
import org.craftercms.security.authentication.impl.DefaultAuthentication;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.AuthenticationSystemException;
import org.craftercms.security.exception.OAuth2Exception;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.RedirectUtils;
import org.craftercms.security.utils.SecurityUtils;
import org.craftercms.security.utils.social.ProviderUserRegistrationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ConnectSupport;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * {@link org.craftercms.security.processors.RequestSecurityProcessor} that uses Spring Social to try to login
 * to Software-as-a-Service (SaaS) API providers such as Facebook, Twitter, and LinkedIn.
 *
 * @author avasquez
 */
public class ProviderLoginProcessor implements RequestSecurityProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ProviderLoginProcessor.class);

    public static final String PROVIDER_ID_PATH_VAR = "providerId";

    public static final String DEFAULT_LOGIN_URL_PATTERN = "/crafter-security-login/{" + PROVIDER_ID_PATH_VAR + "}";

    public static final String CODE_PARAM = "code";
    public static final String ERROR_PARAM = "error";
    public static final String ERROR_DESCRIPTION_PARAM = "error_description";
    public static final String ERROR_URI_PARAM = "error_uri";

    public static final String CONNECTIONS_ATTRIBUTE = "connections";

    public static final String PROFILE_BY_PROVIDER_USER_ID_QUERY = "{attributes.connections.%s.providerUserId: %s}";

    protected String loginUrlPattern;
    protected PathMatcher loginUrlMatcher;
    protected String loginFormUrl;
    protected String registrationFormUrl;
    protected ConnectSupport connectSupport;
    protected ConnectionFactoryLocator connectionFactoryLocator;
    protected ProfileService profileService;
    protected AuthenticationService authenticationService;
    protected LoginSuccessHandler loginSuccessHandler;
    protected LoginFailureHandler loginFailureHandler;
    protected TextEncryptor encryptor;
    protected TenantResolver tenantResolver;

    public ProviderLoginProcessor() {
        loginUrlPattern = DEFAULT_LOGIN_URL_PATTERN;
        loginUrlMatcher = new AntPathMatcher();
    }

    public void setLoginUrlPattern(String loginUrlPattern) {
        this.loginUrlPattern = loginUrlPattern;
    }

    public void setLoginUrlMatcher(PathMatcher loginUrlMatcher) {
        this.loginUrlMatcher = loginUrlMatcher;
    }

    @Required
    public void setLoginFormUrl(String loginFormUrl) {
        this.loginFormUrl = loginFormUrl;
    }

    @Required
    public void setRegistrationFormUrl(String registrationFormUrl) {
        this.registrationFormUrl = registrationFormUrl;
    }

    @Required
    public void setConnectSupport(ConnectSupport connectSupport) {
        this.connectSupport = connectSupport;
    }

    @Required
    public void setConnectionFactoryLocator(ConnectionFactoryLocator connectionFactoryLocator) {
        this.connectionFactoryLocator = connectionFactoryLocator;
    }

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Required
    public void setLoginSuccessHandler(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Required
    public void setLoginFailureHandler(LoginFailureHandler loginFailureHandler) {
        this.loginFailureHandler = loginFailureHandler;
    }

    @Required
    public void setEncryptor(TextEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Required
    public void setTenantResolver(TenantResolver tenantResolver) {
        this.tenantResolver = tenantResolver;
    }

    @Override
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();
        boolean processed = false;

        try {
            if (isLoginRequest(request)) {
                doLogin(context);

                processed = true;
            } else if (isOAuth2CallbackRequest(request)) {
                onOAuth2Callback(context);

                processed = true;
            } else if (isOAuth2ErrorCallbackRequest(request)) {
                onOAuth2ErrorCallback(context);

                processed = true;
            } else if (isOAuth2CancelledCallbackRequest(request)) {
                onOAuth2CancelledCallback(context);

                processed = true;
            }
        } catch (AuthenticationException e) {
            onLoginFailure(context, e);
        }

        if (!processed) {
            processorChain.processRequest(context);
        }
    }

    protected boolean isLoginRequest(HttpServletRequest request) {
        String requestUrl = HttpUtils.getRequestUriWithoutContextPath(request);

        return "POST".equals(request.getMethod()) && loginUrlMatcher.match(loginUrlPattern, requestUrl);
    }

    protected boolean isOAuth2CallbackRequest(HttpServletRequest request) {
        String requestUrl = HttpUtils.getRequestUriWithoutContextPath(request);

        return "GET".equals(request.getMethod()) && loginUrlMatcher.match(loginUrlPattern, requestUrl) &&
            StringUtils.isNotEmpty(request.getParameter(CODE_PARAM));
    }

    protected boolean isOAuth2ErrorCallbackRequest(HttpServletRequest request) {
        String requestUrl = HttpUtils.getRequestUriWithoutContextPath(request);

        return "GET".equals(request.getMethod()) && loginUrlMatcher.match(loginUrlPattern, requestUrl) &&
            StringUtils.isNotEmpty(request.getParameter(ERROR_PARAM));
    }

    protected boolean isOAuth2CancelledCallbackRequest(HttpServletRequest request) {
        String requestUrl = HttpUtils.getRequestUriWithoutContextPath(request);

        return "GET".equals(request.getMethod()) && loginUrlMatcher.match(loginUrlPattern,requestUrl);
    }

    protected String getProviderIdFromRequest(HttpServletRequest request) {
        String requestUrl = HttpUtils.getRequestUriWithoutContextPath(request);

        return loginUrlMatcher.extractUriTemplateVariables(loginUrlPattern, requestUrl).get(PROVIDER_ID_PATH_VAR);
    }

    protected void doLogin(RequestContext context) throws AuthenticationException, IOException {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        String providerId = getProviderIdFromRequest(request);

        logger.debug("Starting OAuth2 login flow for provider {}", providerId);

        ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        String redirectUrl;
        try {
            redirectUrl = connectSupport.buildOAuthUrl(connectionFactory, new ServletWebRequest(request), parameters);
        } catch (Exception e) {
            throw new AuthenticationSystemException("Unable to build authentication URL", e);
        }

        RedirectUtils.redirect(request, response, redirectUrl);
    }

    protected void onOAuth2Callback(RequestContext context) throws AuthenticationException, IOException {
        HttpServletRequest request = context.getRequest();
        String providerId = getProviderIdFromRequest(request);

        logger.debug("Completing OAuth2 login flow for provider {}", providerId);

        OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator
            .getConnectionFactory(providerId);

        try {
            Connection<?> connection = connectSupport.completeConnection(connectionFactory,
                new ServletWebRequest(request));

            onLoginSuccess(context, connection);
        } catch (Exception e) {
            throw new AuthenticationSystemException("Error while completing OAuth 2 connection", e);
        }
    }

    protected void onOAuth2ErrorCallback(RequestContext context) throws AuthenticationException, IOException {
        HttpServletRequest request = context.getRequest();
        String error = request.getParameter(ERROR_PARAM);
        String errorDescription = request.getParameter(ERROR_DESCRIPTION_PARAM);
        String errorUri = request.getParameter(ERROR_URI_PARAM);

        throw new OAuth2Exception(error, errorDescription, errorUri);
    }

    protected void onOAuth2CancelledCallback(RequestContext context) throws AuthenticationException, IOException {
        RedirectUtils.redirect(context.getRequest(), context.getResponse(), loginFormUrl);
    }

    protected void saveException(AuthenticationException e, HttpServletRequest request) {
        logger.debug("Saving authentication exception in session for later use");

        HttpSession session = request.getSession(true);
        session.setAttribute(SecurityUtils.AUTHENTICATION_EXCEPTION_SESSION_ATTRIBUTE, e);
    }

    protected void clearExceptions(HttpServletRequest request) {
        logger.debug("Removing any authentication exceptions from session, not needed anymore");

        HttpSession session = request.getSession();
        session.removeAttribute(SecurityUtils.AUTHENTICATION_EXCEPTION_SESSION_ATTRIBUTE);
    }

    protected void onLoginSuccess(RequestContext context, Connection<?> connection) throws Exception {
        logger.info("Login successful for user " + connection.getDisplayName() + " at provider " + connection
            .getKey().getProviderId());

        HttpServletRequest request = context.getRequest();
        String tenant = SecurityUtils.getTenant(request);

        clearExceptions(request);

        List<Profile> profiles = findProfilesForConnection(tenant, connection);
        if (CollectionUtils.isEmpty(profiles)) {
            doOnRegistrationRequired(context, connection);
        } else if (profiles.size() == 1) {
            doPostLogin(context, profiles.get(0), connection);
        } else {
            doOnMultipleUsersForConnection(context, connection);
        }
    }

    protected void onLoginFailure(RequestContext context, AuthenticationException e) throws Exception {
        logger.debug("Login failed", e);

        saveException(e, context.getRequest());

        loginFailureHandler.handle(context, e);
    }

    protected List<Profile> findProfilesForConnection(String tenant, Connection<?> connection)
        throws AuthenticationException {
        String providerId = connection.getKey().getProviderId();
        String providerUserId = connection.getKey().getProviderUserId();
        String query = String.format(PROFILE_BY_PROVIDER_USER_ID_QUERY, providerId, providerUserId);

        try {
            return profileService.getProfilesByQuery(tenant, query, null, null, null, null, CONNECTIONS_ATTRIBUTE);
        } catch (ProfileException e) {
            throw new AuthenticationSystemException("Unable to find profile associated to provider user ID '" +
                providerUserId + "' of provider '" + providerId + "'");
        }
    }

    protected void doOnRegistrationRequired(RequestContext context, Connection<?> connection) throws Exception {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        ProviderUserRegistrationSupport registrationSupport = ProviderUserRegistrationSupport.fromConnection(
            connection, encryptor, profileService);
        HttpSession session = request.getSession(true);

        registrationSupport.getProfile().setTenant(tenantResolver.getCurrentTenant());

        session.setAttribute(ProviderUserRegistrationSupport.SESSION_ATTRIBUTE, registrationSupport);

        RedirectUtils.redirect(request, response, registrationFormUrl);
    }

    protected void doPostLogin(RequestContext context, Profile profile, Connection<?> connection) throws Exception {
        ConnectionUtils.addConnectionData(profile, connection.createData(), encryptor);

        profileService.updateAttributes(profile.getId().toString(), profile.getAttributes());

        Ticket ticket = authenticationService.createTicket(profile.getId().toString());
        Authentication auth = new DefaultAuthentication(ticket.getId().toString(), profile);

        SecurityUtils.setAuthentication(context.getRequest(), auth);

        loginSuccessHandler.handle(context, auth);
    }

    protected void doOnMultipleUsersForConnection(RequestContext context, Connection<?> connection) throws Exception {
        String url = URIBuilder.fromUri(registrationFormUrl).queryParam(ERROR_PARAM, "multiple_users").build().toString();

        RedirectUtils.redirect(context.getRequest(), context.getResponse(), url);
    }

}
