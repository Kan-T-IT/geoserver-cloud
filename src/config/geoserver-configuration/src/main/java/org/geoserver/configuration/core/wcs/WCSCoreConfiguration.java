/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.configuration.core.wcs;

import org.geoserver.spring.config.annotations.TranspileXmlConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Transpiles {@literal gs-wcs-.jar!/applicationContext.xml} to {@link WCSCoreConfiguration_Generated}
 *
 * @see WCSCoreConfiguration_Generated
 */
@Configuration(proxyBeanMethods = false)
@Import(WCSCoreConfiguration_Generated.class)
@TranspileXmlConfig(locations = "jar:gs-wcs-.*!/applicationContext.xml", publicAccess = true)
public class WCSCoreConfiguration {}
