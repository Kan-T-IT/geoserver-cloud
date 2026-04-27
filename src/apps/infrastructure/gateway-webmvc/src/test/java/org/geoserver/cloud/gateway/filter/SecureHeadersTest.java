/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.geoserver.cloud.autoconfigure.gateway.SecureHeadersProperties;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

/** @since 3.0.0 */
class SecureHeadersTest {

    SecureHeadersProperties config;

    @BeforeEach
    void setup() {
        config = new SecureHeadersProperties();
    }

    @Test
    void addsDefaultHeaders() throws Exception {
        ServerRequest request = requestWithSecureHeadersProperties(config);

        ServerResponse response = invokeFilter(request, writableResponse());

        assertThat(response.headers().getFirst("X-Frame-Options")).isEqualTo("DENY");
        assertThat(response.headers().getFirst("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(response.headers().getFirst("Strict-Transport-Security")).isEqualTo("max-age=631138519");
        assertThat(response.headers().getFirst("X-Xss-Protection")).isEqualTo("1 ; mode=block");
        assertThat(response.headers().getFirst("Referrer-Policy")).isEqualTo("no-referrer");
        assertThat(response.headers().getFirst("X-Download-Options")).isEqualTo("noopen");
        assertThat(response.headers().getFirst("X-Permitted-Cross-Domain-Policies"))
                .isEqualTo("none");
        assertThat(response.headers().getFirst("Content-Security-Policy")).isNotNull();
    }

    @Test
    void doesNotOverwriteExisting() throws Exception {
        ServerRequest request = requestWithSecureHeadersProperties(config);

        HttpHeaders existing = new HttpHeaders();
        existing.set("X-Frame-Options", "SAMEORIGIN");
        ServerResponse response = invokeFilter(request, writableResponse(200, existing));

        assertThat(response.headers().getFirst("X-Frame-Options")).isEqualTo("SAMEORIGIN");
    }

    @Test
    void disabledHeadersOmitted() throws Exception {
        config.setDisable(List.of("content-security-policy"));

        ServerRequest request = requestWithSecureHeadersProperties(config);

        ServerResponse response = invokeFilter(request, writableResponse());

        assertThat(response.headers().getFirst("Content-Security-Policy")).isNull();
        assertThat(response.headers().getFirst("X-Frame-Options")).isEqualTo("DENY");
    }

    @Test
    void customValues() throws Exception {
        config.setFrameOptions("SAMEORIGIN");

        ServerRequest request = requestWithSecureHeadersProperties(config);

        ServerResponse response = invokeFilter(request, writableResponse());

        assertThat(response.headers().getFirst("X-Frame-Options")).isEqualTo("SAMEORIGIN");
    }

    @Test
    void cachedAcrossRequests() throws Exception {
        ServerRequest request1 = requestWithSecureHeadersProperties(config);
        ServerResponse response1 = invokeFilter(request1, writableResponse());
        assertThat(response1.headers().getFirst("X-Frame-Options")).isEqualTo("DENY");

        ServerRequest request2 = requestWithSecureHeadersProperties(config);
        ServerResponse response2 = invokeFilter(request2, writableResponse());
        assertThat(response2.headers().getFirst("X-Frame-Options")).isEqualTo("DENY");
    }

    private ServerRequest requestWithSecureHeadersProperties(SecureHeadersProperties props) {
        StaticWebApplicationContext ctx = new StaticWebApplicationContext();
        ctx.getBeanFactory().registerSingleton("secureHeadersProperties", props);
        ctx.refresh();
        MockServletContext servletContext = new MockServletContext();
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ctx);
        return ServerRequest.create(new MockHttpServletRequest(servletContext, "GET", "/test"), List.of());
    }

    private ServerResponse invokeFilter(ServerRequest request, ServerResponse response) throws Exception {
        HandlerFilterFunction<ServerResponse, ServerResponse> filter = GeoServerGatewayFilterFunctions.secureHeaders();
        return filter.filter(request, _ -> response);
    }

    /** Creates a {@link ServerResponse} with writable headers (unlike {@code ServerResponse.ok().build()}). */
    private ServerResponse writableResponse() {
        return writableResponse(200, new HttpHeaders());
    }

    private static ServerResponse writableResponse(int statusCode, HttpHeaders headers) {
        return new ServerResponse() {
            @Override
            public HttpStatusCode statusCode() {
                return HttpStatusCode.valueOf(statusCode);
            }

            @Override
            public HttpHeaders headers() {
                return headers;
            }

            @Override
            public MultiValueMap<String, Cookie> cookies() {
                return new LinkedMultiValueMap<>();
            }

            @Override
            @Nullable
            public ModelAndView writeTo(HttpServletRequest request, HttpServletResponse response, Context context) {
                return null;
            }
        };
    }
}
