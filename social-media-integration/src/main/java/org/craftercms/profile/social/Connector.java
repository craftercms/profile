package org.craftercms.profile.social;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.profile.social.exceptions.OAuth2Exception;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ConnectSupport;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * Helper class that simplifies Spring Social connection establishment.
 *
 * @author avasquez
 */
public class Connector {

    public static final String PARAM_OAUTH_TOKEN = "oauth_token";
    public static final String PARAM_CODE = "code";
    public static final String PARAM_ERROR = "error";
    public static final String PARAM_ERROR_DESCRIPTION = "error_description";
    public static final String PARAM_ERROR_URI = "error_uri";

    protected ConnectionFactoryLocator connectionFactoryLocator;
    protected ConnectSupport connectSupport;

    public Connector() {
        connectSupport = new ConnectSupport();
    }

    @Required
    public void setConnectionFactoryLocator(ConnectionFactoryLocator connectionFactoryLocator) {
        this.connectionFactoryLocator = connectionFactoryLocator;
    }

    public void setConnectSupport(ConnectSupport connectSupport) {
        this.connectSupport = connectSupport;
    }

    public String startConnection(String providerId, HttpServletRequest request) throws IOException {
        return startConnection(providerId, request, null);
    }

    public String startConnection(String providerId, HttpServletRequest request,
                                  MultiValueMap<String, String> additionalUrlParams) {
        ConnectionFactory<?> connectionFactory = getConnectionFactory(providerId);
        ServletWebRequest webRequest = new ServletWebRequest(request);

        return connectSupport.buildOAuthUrl(connectionFactory, webRequest, additionalUrlParams);
    }

    public Connection<?> completeConnection(String providerId, HttpServletRequest request) throws OAuth2Exception {
        if (StringUtils.isNotEmpty(request.getParameter(PARAM_OAUTH_TOKEN))) {
            OAuth1ConnectionFactory<?> connectionFactory = (OAuth1ConnectionFactory<?>)getConnectionFactory(providerId);
            ServletWebRequest webRequest = new ServletWebRequest(request);

            return connectSupport.completeConnection(connectionFactory, webRequest);
        } else if (StringUtils.isNotEmpty(request.getParameter(PARAM_CODE))) {
            OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>)getConnectionFactory(providerId);
            ServletWebRequest webRequest = new ServletWebRequest(request);

            return connectSupport.completeConnection(connectionFactory, webRequest);
        } else if (StringUtils.isNotEmpty(request.getParameter(PARAM_ERROR))) {
            String error = request.getParameter(PARAM_ERROR);
            String errorDescription = request.getParameter(PARAM_ERROR_DESCRIPTION);
            String errorUri = request.getParameter(PARAM_ERROR_URI);

            throw new OAuth2Exception(error, errorDescription, errorUri);
        } else {
            return null;
        }
    }

    protected ConnectionFactory<?> getConnectionFactory(String providerId) {
        return connectionFactoryLocator.getConnectionFactory(providerId);
    }

}
