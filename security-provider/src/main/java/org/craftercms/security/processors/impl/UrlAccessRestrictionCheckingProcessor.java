/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.security.processors.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.craftercms.commons.http.HttpUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.exception.AccessDeniedException;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.craftercms.security.utils.spring.el.AccessRestrictionExpressionRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * Processor that checks if the current user has permission to access the current request URL. To do this,
 * the processor matches the URL against the keys of the {@code restriction} map, which are ANT-style path patterns.
 * If a key matches, the value is interpreted as a Spring EL expression, the expression is executed, and if it returns
 * true, the processor chain is continued, if not an {@link AccessDeniedException} is thrown. The expression should be
 * one of this method calls that return a boolean:
 * <p/>
 * <ol>
 * <li>isAnonymous()</li>
 * <li>isAuthenticated()</li>
 * <li>hasRole('role'})</li>
 * <li>hasAnyRole({'role1', 'role2'})</li>
 * <li>permitAll()</li>
 * <li>denyAll()</li>
 * </ol>
 * <p/>
 * <p>Examples of user URL restrictions:</p>
 * <p/>
 * <pre>
 * &lt;entry key="/static-assets" value="permitAll()"/&gt;
 * &lt;entry key="/user" value="hasAnyRole({'user', 'admin'})"/&gt;
 * &lt;entry key="/admin" value="hasRole('admin')"/&gt;
 * &lt;entry key="/**" value="isAuthenticated()"/&gt;
 * </pre>
 * <strong>WARN: </strong> Remember to put the more general restrictions (like /**) at the end so they're matched last.
 *
 * @author Alfonso Vásquez
 * @see AntPathMatcher
 */
public class UrlAccessRestrictionCheckingProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(UrlAccessRestrictionCheckingProcessor.class);

    protected PathMatcher pathMatcher;
    protected Map<String, Expression> urlRestrictions;

    /**
     * Default constructor. Creates {@link AntPathMatcher} as default path matcher.
     */
    public UrlAccessRestrictionCheckingProcessor() {
        pathMatcher = new AntPathMatcher();
    }

    /**
     * Sets the path matcher to use to match the URLs for restriction checking.
     */
    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    /**
     * Sets the map of restrictions. Each key of the map is ANT-style path pattern, used to match the URLs of incoming
     * requests, and each value is a Spring EL expression.
     */
    @Required
    public void setUrlRestrictions(Map<String, String> restrictions) {
        urlRestrictions = new LinkedHashMap<>();

        ExpressionParser parser = new SpelExpressionParser();

        for (Map.Entry<String, String> entry : restrictions.entrySet()) {
            urlRestrictions.put(entry.getKey(), parser.parseExpression(entry.getValue()));
        }
    }

    protected Map<String, Expression> getUrlRestrictions() {
        return urlRestrictions;
    }

    /**
     * Matches the request URL against the keys of the {@code restriction} map, which are ANT-style path patterns. If
     * a key matches, the value is interpreted as a Spring EL expression, the expression is executed, and if it returns
     * true, the processor chain is continued, if not an {@link AccessDeniedException} is thrown.
     *
     * @param context        the context which holds the current request and response
     * @param processorChain the processor chain, used to call the next processor
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        Map<String, Expression> urlRestrictions = getUrlRestrictions();

        if (MapUtils.isNotEmpty(urlRestrictions)) {
            HttpServletRequest request = context.getRequest();
            String requestUrl = getRequestUrl(context.getRequest());

            logger.debug("Checking access restrictions for URL {}", requestUrl);

            for (Map.Entry<String, Expression> entry : urlRestrictions.entrySet()) {
                String urlPattern = entry.getKey();
                Expression expression = entry.getValue();

                if (pathMatcher.match(urlPattern, requestUrl)) {
                    logger.debug("Checking restriction [{} => {}]", requestUrl, expression.getExpressionString());

                    if (isAccessAllowed(request, expression)) {
                        logger.debug("Restriction [{}' => {}] evaluated to true for user: access allowed", requestUrl,
                                     expression.getExpressionString());

                        break;
                    } else {
                        throw new AccessDeniedException("Restriction ['" + requestUrl + "' => " +
                                                        expression.getExpressionString() + "] evaluated to false " +
                                                        "for user: access denied");
                    }
                }
            }
        }

        processorChain.processRequest(context);
    }

    /**
     * Returns the request URL without the context path.
     */
    protected String getRequestUrl(HttpServletRequest request) {
        return HttpUtils.getRequestUriWithoutContextPath(request);
    }

    protected boolean isAccessAllowed(HttpServletRequest request, Expression expression) {
        Object value = expression.getValue(createExpressionRoot(request));
        if (!(value instanceof Boolean)) {
            throw new IllegalStateException("Expression " + expression.getExpressionString() + " should return a " +
                                            "boolean value");
        }

        return (Boolean)value;
    }

    protected Object createExpressionRoot(HttpServletRequest request) {
        AccessRestrictionExpressionRoot root = new AccessRestrictionExpressionRoot();
        Authentication auth = SecurityUtils.getAuthentication(request);

        if (auth != null) {
            root.setProfile(auth.getProfile());
        }

        return root;
    }

}
