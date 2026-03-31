/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.gateway.app;

import org.geoserver.cloud.app.GeoServerApplicationLauncher;
import org.geoserver.cloud.autoconfigure.gateway.GatewayApplicationAutoconfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Cloud Gateway Server MVC application for GeoServer Cloud
 *
 * @see GatewayApplicationAutoconfiguration
 * @since 3.0.0
 */
@SpringBootApplication
public class GatewayMvcApplication {

    public static void main(String... args) {
        GeoServerApplicationLauncher.run(GatewayMvcApplication.class, args);
    }
}
