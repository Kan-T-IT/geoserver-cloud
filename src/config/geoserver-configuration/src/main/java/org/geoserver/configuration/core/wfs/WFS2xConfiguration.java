/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.configuration.core.wfs;

import org.geoserver.spring.config.annotations.TranspileXmlConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Transpiled XML configuration from {@literal jar:gs-wfs2_x-.*!/applicationContext.xml}
 *
 * @see WFSCoreConfiguration_Generated
 */
@Configuration(proxyBeanMethods = false)
@TranspileXmlConfig(locations = "jar:gs-wfs2_x-.*!/applicationContext.xml", publicAccess = true)
@Import(WFS2xConfiguration_Generated.class)
@SuppressWarnings({"java:S1118", "java:S101"})
public class WFS2xConfiguration {}
