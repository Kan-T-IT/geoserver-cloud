/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.autoconfigure.gwc.web;

import static org.geoserver.cloud.autoconfigure.gwc.GoServerWebUIConfigurationProperties.GWC_WEBUI_ENABLED_PROPERTY;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.cloud.autoconfigure.gwc.ConditionalOnGeoServerWebUIEnabled;
import org.geoserver.cloud.autoconfigure.gwc.GoServerWebUIConfigurationProperties;
import org.geoserver.configuration.gwc.GwcGeoServerWebUIConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Auto configuration to enabled GWC Wicket Web UI components.
 *
 * <p>Conditionals:
 *
 * <ul>
 *   <li>The {@literal gs-web-gwc} jar is in the classpath
 *   <li>{@literal gwc.enabled=true}: Core gwc integration is enabled
 *   <li>{@literal geoserver.web-ui.gwc.=true}: gwc web-ui integration is enabled
 * </ul>
 *
 * @see GwcGeoServerWebUIConfiguration
 * @since 1.0
 */
@AutoConfiguration
@ConditionalOnGeoServerWebUIEnabled
@EnableConfigurationProperties(GoServerWebUIConfigurationProperties.class)
@Import(GwcGeoServerWebUIConfiguration.class)
@Slf4j(topic = "org.geoserver.cloud.autoconfigure.web.gwc")
public class GwcGeoServerWebUIAutoConfiguration {

    public @PostConstruct void log() {
        log.info("{} enabled", GWC_WEBUI_ENABLED_PROPERTY);
    }
}
