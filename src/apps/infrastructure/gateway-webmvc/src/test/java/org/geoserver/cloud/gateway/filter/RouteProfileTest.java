/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

/** @since 3.0.0 */
class RouteProfileTest {

    @Test
    void activeMatch_proceeds() throws Exception {
        ServerResponse response = invokeFilter("dev", 403, "dev");
        assertThat(response.statusCode().value()).isEqualTo(200);
    }

    @Test
    void noMatch_returnsStatus() throws Exception {
        ServerResponse response = invokeFilter("dev", 403, "prod");
        assertThat(response.statusCode().value()).isEqualTo(403);
    }

    @Test
    void negated_notActive_proceeds() throws Exception {
        ServerResponse response = invokeFilter("!dev", 404, "prod");
        assertThat(response.statusCode().value()).isEqualTo(200);
    }

    @Test
    void negated_active_returnsStatus() throws Exception {
        ServerResponse response = invokeFilter("!dev", 404, "dev");
        assertThat(response.statusCode().value()).isEqualTo(404);
    }

    @Test
    void blankProfile_returnsStatus() throws Exception {
        ServerResponse response = invokeFilter("", 404, "dev");
        assertThat(response.statusCode().value()).isEqualTo(404);
    }

    @Test
    void multipleProfiles_matches() throws Exception {
        ServerResponse response = invokeFilter("staging", 403, "dev", "staging");
        assertThat(response.statusCode().value()).isEqualTo(200);
    }

    private ServerResponse invokeFilter(String profile, int statusCode, String... activeProfiles) throws Exception {
        HandlerFilterFunction<ServerResponse, ServerResponse> filter =
                GeoServerGatewayFilterFunctions.routeProfile(profile, statusCode);
        ServerRequest request = requestWithProfiles(activeProfiles);
        return filter.filter(request, _ -> ServerResponse.ok().build());
    }

    private ServerRequest requestWithProfiles(String... profiles) {
        StaticWebApplicationContext ctx = new StaticWebApplicationContext();
        ctx.getEnvironment().setActiveProfiles(profiles);
        ctx.refresh();
        MockServletContext servletContext = new MockServletContext();
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ctx);
        return ServerRequest.create(new MockHttpServletRequest(servletContext, "GET", "/test"), List.of());
    }
}
