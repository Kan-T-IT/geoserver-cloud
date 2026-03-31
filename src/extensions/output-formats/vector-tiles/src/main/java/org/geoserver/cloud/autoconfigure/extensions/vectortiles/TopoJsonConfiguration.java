/* (c) 2025 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.autoconfigure.extensions.vectortiles;

import org.geoserver.configuration.extension.vectortiles.VectorTilesTopoJsonConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration for TopoJSON Vector Tiles format.
 *
 * @since 2.27.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnVectorTilesTopoJson
@Import(VectorTilesTopoJsonConfiguration.class)
class TopoJsonConfiguration {}
