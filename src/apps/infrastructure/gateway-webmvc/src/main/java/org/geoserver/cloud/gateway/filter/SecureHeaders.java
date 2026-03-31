/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.gateway.filter;

import jakarta.servlet.ServletContext;
import java.util.Map;
import org.geoserver.cloud.autoconfigure.gateway.SecureHeadersProperties;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * SCG MVC filter that adds security-related HTTP response headers.
 *
 * <p>This is the {@link HandlerFilterFunction} equivalent of Spring Cloud Gateway WebFlux's
 * {@code SecureHeadersGatewayFilterFactory}. SCG Server MVC does not include the WebFlux {@code filter.secure-headers}
 * support, so this filter bridges the gap by applying the same security headers as a per-route gateway filter.
 *
 * <p>Headers are resolved lazily on first request from {@link SecureHeadersProperties}. The lazy lookup is necessary
 * because SCG MVC's {@code ReflectiveOperationInvoker} passes {@code null} as target when invoking filter methods, so
 * Spring beans cannot be injected at construction time.
 *
 * <p>Headers already present in the response (e.g., set by a backend service) are not overwritten.
 *
 * <p>Example usage in YAML config:
 *
 * <pre>{@code
 * filters:
 *   - SecureHeaders
 * }</pre>
 *
 * @see SecureHeadersProperties
 * @since 3.0.0
 */
class SecureHeaders implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        ServerResponse response = next.handle(request);
        SecureHeadersProperties config = findConfig(request);
        if (config.isEnabled()) {
            Map<String, String> headers = config.resolveHeaders();
            headers.forEach((name, value) -> {
                if (response.headers().getFirst(name) == null) {
                    response.headers().set(name, value);
                }
            });
        }
        return response;
    }

    private static SecureHeadersProperties findConfig(ServerRequest request) {
        ServletContext servletContext = request.servletRequest().getServletContext();
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return ctx.getBean(SecureHeadersProperties.class);
    }
}
