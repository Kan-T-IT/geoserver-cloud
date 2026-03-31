/* (c) 2025 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cloud.autoconfigure.extensions.ogcapi.features;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.cloud.autoconfigure.extensions.ConditionalOnGeoServerWebUI;
import org.geoserver.configuration.extension.ogcapi.features.OgcApiFeaturesWebConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/** @see OgcApiFeaturesWebConfiguration */
@AutoConfiguration
@ConditionalOnOgcApiFeatures
@ConditionalOnGeoServerWebUI
@Import(OgcApiFeaturesWebConfiguration.class)
@Slf4j(topic = "org.geoserver.cloud.autoconfigure.extensions.ogcapi.features")
class OgcApiFeaturesWebUIAutoConfiguration {

    @PostConstruct
    void log() {
        log.info("OGC API Features WEBUI extension enabled");
    }
}
