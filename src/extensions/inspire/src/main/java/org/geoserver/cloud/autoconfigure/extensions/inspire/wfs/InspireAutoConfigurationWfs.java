/* (c) 2025 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cloud.autoconfigure.extensions.inspire.wfs;

import lombok.extern.slf4j.Slf4j;
import org.geoserver.configuration.extension.inspire.InspireConfigurationWfs;
import org.geoserver.configuration.extension.inspire.InspireCoreConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnInspireWfs
@Import({InspireCoreConfiguration.class, InspireConfigurationWfs.class})
@Slf4j(topic = "org.geoserver.cloud.autoconfigure.extensions.inspire.wfs")
@SuppressWarnings("java:S1118") // Suppress SonarLint warning, constructor needs to be public
public class InspireAutoConfigurationWfs {}
