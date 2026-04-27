/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.autoconfigure.gwc.integration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.cloud.autoconfigure.gwc.ConditionalOnWMTSIntegrationEnabled;
import org.geoserver.configuration.gwc.GwcGeoServerWMTSIntegrationConfiguration;
import org.geowebcache.service.wmts.WMTSService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * @see GwcGeoServerWMTSIntegrationConfiguration
 * @since 1.0
 */
@AutoConfiguration
@ConditionalOnWMTSIntegrationEnabled
@ConditionalOnClass(WMTSService.class)
@Import(GwcGeoServerWMTSIntegrationConfiguration.class)
@Slf4j(topic = "org.geoserver.cloud.autoconfigure.gwc.integration")
@SuppressWarnings("java:S1118") // Suppress SonarLint warning, constructor needs to be public
public class WMTSIntegrationAutoConfiguration {

    public @PostConstruct void log() {
        log.info("GeoWebCache WMTS GeoServer integration enabled");
    }
}
