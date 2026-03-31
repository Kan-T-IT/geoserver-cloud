/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.configuration.core.wms;

import org.geoserver.spring.config.annotations.TranspileXmlConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Transpiled XML configuration from {@literal jar:gs-wms-gml.*!/applicationContext.xml}
 *
 * @see WmsGmlConfiguration_Generated
 */
@Configuration(proxyBeanMethods = false)
@TranspileXmlConfig(locations = "jar:gs-wms-gml.*!/applicationContext.xml")
@Import(WmsGmlConfiguration_Generated.class)
@SuppressWarnings({"java:S1118", "java:S6830"})
public class WmsGmlConfiguration {}
