/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.configuration.extension.kml;

import org.geoserver.spring.config.annotations.TranspileXmlConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(KMLConfiguration_Generated.class)
@TranspileXmlConfig(locations = "jar:gs-kml-.*!/applicationContext.xml")
public class KMLConfiguration {}
