/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.security.impl.processors;

import org.apache.commons.collections.MapUtils;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.exception.AccessDeniedException;
import org.craftercms.security.exception.CrafterSecurityException;
import org.craftercms.security.utils.spring.el.AccessRestrictionExpressionRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Processor that checks if the current user has permission to access the current request URL. To do this, the processor matches
 * the URL against the keys of the {@code restriction} map, which are ANT-style path patterns. If a key matches, the value is
 * interpreted as a Spring EL expression, the expression is executed, and if it returns true, the processor chain is continued, if not
 * an {@link AccessDeniedException} is thrown. The expression should be one of this method calls that return a boolean:
 *
 * <ol>
 *     <li>isAnonymous()</li>
 *     <li>isAuthenticated()</li>
 *     <li>hasRole('role'})</li>
 *     <li>hasAnyRole({'role1', 'role2'})</li>
 *     <li>permitAll()</li>
 *     <li>denyAll()</li>
 * </ol>
 *
 * <p>Examples of user URL restrictions:</p>
 *
 * <![CDATA[
 * <entry key="/static-assets" value="permitAll()"/>
 * <entry key="/user" value="hasAnyRole({'user', 'admin'})"/>
 * <entry key="/admin" value="hasRole('admin')"/>
 * <entry key="/**" value="isAuthenticated()"/>
 * ]]>
 *
 * <strong>WARN: </strong> Remember to put the more general restrictions (like /**) at the end so they're matched last.
 *
 * @author Alfonso Vásquez
 *
 * @see AntPathMatcher
 */
public class UrlAccessRestrictionCheckingProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(UrlAccessRestrictionCheckingProcessor.class);

    protected PathMatcher pathMatcher;
    protected Map<String, Expression> urlRestrictions;

    public UrlAccessRestrictionCheckingProcessor() {
        pathMatcher = new AntPathMatcher();
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    @Required
    public void setUrlRestrictions(Map<String, String> restrictions) {
        urlRestrictions = new LinkedHashMap<String, Expression>();

        ExpressionParser parser = new SpelExpressionParser();

        for (Map.Entry<String, String> entry : restrictions.entrySet()) {
            urlRestrictions.put(entry.getKey(), parser.parseExpression(entry.getValue()));
        }
    }

    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        if (MapUtils.isNotEmpty(urlRestrictions)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Checking URL access restrictions");
            }

            if (context.getAuthenticationToken() == null) {
                throw new IllegalArgumentException("Request context doesn't contain an authentication token");
            }
            if (context.getAuthenticationToken().getProfile() == null) {
                throw new IllegalArgumentException("Authentication token of request context doesn't contain a user profile");
            }

            String requestUrl = getRequestUrl(context.getRequest());
            UserProfile profile = context.getAuthenticationToken().getProfile();

            for (Map.Entry<String, Expression> entry : urlRestrictions.entrySet()) {
                String urlPattern = entry.getKey();
                Expression expression = entry.getValue();

                if (pathMatcher.match(urlPattern, requestUrl)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Checking restriction ['" + requestUrl + "' => " + expression.getExpressionString() + "] for user " +
                                profile.getUserName());
                    }

                    if (isAccessAllowed(profile, expression)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Restriction ['" + requestUrl + "' => " + expression.getExpressionString() + "] evaluated to " +
                                    "true for user " + profile.getUserName() + ": access allowed");
                        }

                        break;
                    } else {
                        throw new AccessDeniedException("Restriction ['" + requestUrl + "' => " + expression.getExpressionString() +
                                "] evaluated to false for user " + profile.getUserName() + ": access denied");
                    }
                }
            }
        }

        processorChain.processRequest(context);
    }

    protected String getRequestUrl(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    protected boolean isAccessAllowed(UserProfile profile, Expression expression) {
        Object value = expression.getValue(createExpressionRoot(profile));
        if (!(value instanceof Boolean)) {
            throw new CrafterSecurityException("Expression " + expression.getExpressionString() + " should return a boolean value");
        }

        return (Boolean) value;
    }

    protected AccessRestrictionExpressionRoot createExpressionRoot(UserProfile profile) {
        return new AccessRestrictionExpressionRoot(profile);
    }

}
