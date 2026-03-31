/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.gateway.filter;

import static org.geoserver.cloud.gateway.filter.GatewaySharedAuth.sanitizeMethod;
import static org.geoserver.cloud.gateway.filter.GatewaySharedAuth.sanitizeUri;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet filter that catches proxy connection errors and returns 502 Bad Gateway with a concise log message, instead
 * of letting Tomcat log a full stack trace for routine "backend is down" situations.
 *
 * <p>Registered as a bean in {@link org.geoserver.cloud.autoconfigure.gateway.GatewayApplicationAutoconfiguration
 * GatewayApplicationAutoconfiguration}.
 *
 * @since 3.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ProxyExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, jakarta.servlet.ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            Throwable root = rootCause(e);
            if (isConnectionError(root)) {
                log.warn(
                        "{} {} -> 502: {}",
                        sanitizeMethod(request.getMethod()),
                        sanitizeUri(request.getRequestURI()),
                        root.getMessage());
                if (!response.isCommitted()) {
                    response.sendError(HttpServletResponse.SC_BAD_GATEWAY, root.getMessage());
                }
                return;
            }
            throw e;
        }
    }

    private static boolean isConnectionError(Throwable t) {
        return t instanceof java.net.ConnectException
                || t instanceof java.net.NoRouteToHostException
                || t instanceof java.net.SocketTimeoutException
                || t instanceof java.net.UnknownHostException;
    }

    private static Throwable rootCause(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
}
