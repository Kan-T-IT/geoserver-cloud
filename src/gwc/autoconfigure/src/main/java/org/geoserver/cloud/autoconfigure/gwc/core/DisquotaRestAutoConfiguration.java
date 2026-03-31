/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cloud.autoconfigure.gwc.core;

import org.geoserver.cloud.autoconfigure.gwc.ConditionalOnDiskQuotaEnabled;
import org.geoserver.cloud.autoconfigure.gwc.ConditionalOnGeoWebCacheEnabled;
import org.geoserver.cloud.autoconfigure.gwc.ConditionalOnGeoWebCacheRestConfigEnabled;
import org.geoserver.configuration.gwc.GwcDiskQuotaRestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Enables disk quota REST API if both {@link ConditionalOnDiskQuotaEnabled disk-quota} and
 * {@link ConditionalOnGeoWebCacheRestConfigEnabled rest-config} are enabled.
 *
 * @see GwcDiskQuotaRestConfiguration
 */
@Configuration
@ConditionalOnGeoWebCacheEnabled
@ConditionalOnDiskQuotaEnabled
@ConditionalOnGeoWebCacheRestConfigEnabled
@Import(GwcDiskQuotaRestConfiguration.class)
@SuppressWarnings("java:S1118")
class DisquotaRestAutoConfiguration {}
