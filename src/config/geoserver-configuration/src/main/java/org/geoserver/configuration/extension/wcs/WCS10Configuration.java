/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.configuration.extension.wcs;

import org.geoserver.spring.config.annotations.TranspileXmlConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Transpiled XML configuration from {@literal jar:gs-wcs1_0-.*!/applicationContext.xml}
 *
 * @see WCS10Configuration_Generated
 */
@Configuration(proxyBeanMethods = false)
@TranspileXmlConfig(locations = "jar:gs-wcs1_0-.*!/applicationContext.xml")
@SuppressWarnings("java:S101")
@Import(WCS10Configuration_Generated.class)
public class WCS10Configuration {}
