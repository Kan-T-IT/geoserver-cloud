/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.autoconfigure.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Gateway/WebUI Shared Authentication mechanism.
 *
 * <p>For automatic documentation purposes only, as used by the {@literal spring-boot-configuration-processor}. Header
 * name constants are defined in {@link org.geoserver.cloud.gateway.filter.GatewaySharedAuth}.
 *
 * @since 3.0.0
 */
@ConfigurationProperties(prefix = "geoserver.security.gateway-shared-auth")
@Data
public class SharedAuthConfigurationProperties {

    /**
     * Enable or disable the Gateway/WebUI Shared Authentication mechanism, where the Gateway works as mediator to share
     * the authentication from the GeoServer WebUI with the rest of the services.
     */
    private boolean enabled = true;
}
