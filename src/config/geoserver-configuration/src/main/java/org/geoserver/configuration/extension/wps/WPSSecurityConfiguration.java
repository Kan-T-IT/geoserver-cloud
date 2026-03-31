/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.configuration.extension.wps;

import org.geoserver.spring.config.annotations.TranspileXmlConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Transpiled java configuration for {@code jar:gs-wps-.*!/applicationSecurityContext.xml}
 *
 * @see WPSSecurityConfiguration_Generated
 * @since 3.0.0
 */
@Configuration(proxyBeanMethods = false)
@TranspileXmlConfig(locations = "jar:gs-wps-.*!/applicationSecurityContext.xml")
@Import(WPSSecurityConfiguration_Generated.class)
public class WPSSecurityConfiguration {}
