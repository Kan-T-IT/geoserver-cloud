/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.autoconfigure.gateway;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.server.mvc.config.GatewayMvcProperties;
import org.springframework.web.cors.CorsConfiguration;

/**
 * CORS configuration properties matching the WebFlux gateway's {@code globalcors} structure.
 *
 * <p>SCG Server MVC does not provide a {@code globalcors} config like the WebFlux variant. This class bridges the gap
 * by binding the same YAML structure under {@code spring.cloud.gateway.server.webmvc.globalcors} and registering a
 * {@link org.springframework.web.filter.CorsFilter} from it.
 *
 * <p>Example:
 *
 * <pre>{@code
 * spring.cloud.gateway.server.webmvc:
 *   globalcors:
 *     cors-configurations:
 *       "[/**]":
 *         allowedOrigins: "*"
 *         allowedHeaders: "*"
 *         allowedMethods: GET, POST, PUT, DELETE, OPTIONS, HEAD
 * }</pre>
 *
 * @since 3.0.0
 */
@ConfigurationProperties(GatewayMvcProperties.PREFIX + ".globalcors")
public class GlobalCorsProperties {

    private final Map<String, CorsConfiguration> corsConfigurations = new LinkedHashMap<>();

    public Map<String, CorsConfiguration> getCorsConfigurations() {
        return corsConfigurations;
    }
}
