/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.gateway.filter;

import static org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext;

import java.util.List;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Allows to enable routes only if a given spring profile is enabled.
 *
 * <p>Since {@code spring.cloud.gateway.server.mvc.routes} is a list and not a map/dictionary, routes can't be added in
 * profiles because the list is overwritten fully. This filter allows to enable routes based on profiles from a single
 * list of routes.
 *
 * <p>Example usage in YAML config:
 *
 * <pre>{@code
 * filters:
 *   - RouteProfile=dev,403
 * }</pre>
 *
 * @since 3.0.0
 */
class RouteProfile implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final String profile;
    private final int statusCode;

    public RouteProfile(String profile, int statusCode) {
        this.profile = profile;
        this.statusCode = statusCode;
    }

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        if (profileMatches(request)) {
            return next.handle(request);
        }
        return ServerResponse.status(statusCode).build();
    }

    private boolean profileMatches(ServerRequest request) {
        List<String> activeProfiles = activeProfiles(request);
        String effectiveProfile = profile;
        if (StringUtils.hasText(effectiveProfile)) {
            boolean exclude = effectiveProfile.startsWith("!");
            effectiveProfile = exclude ? effectiveProfile.substring(1) : effectiveProfile;

            boolean profileMatch = activeProfiles.contains(effectiveProfile);
            return (profileMatch && !exclude) || (!profileMatch && exclude);
        }
        return false;
    }

    private static List<String> activeProfiles(ServerRequest request) {
        Environment environment = getEnvironment(request);
        return List.of(environment.getActiveProfiles());
    }

    private static Environment getEnvironment(ServerRequest request) {
        WebApplicationContext ctx =
                getRequiredWebApplicationContext(request.servletRequest().getServletContext());
        return ctx.getEnvironment();
    }
}
