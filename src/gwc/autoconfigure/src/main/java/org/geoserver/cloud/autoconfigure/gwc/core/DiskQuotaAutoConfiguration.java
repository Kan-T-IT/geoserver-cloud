/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.autoconfigure.gwc.core;

import org.geoserver.cloud.autoconfigure.gwc.ConditionalOnDiskQuotaEnabled;
import org.geoserver.cloud.autoconfigure.gwc.ConditionalOnGeoWebCacheRestConfigEnabled;
import org.geoserver.configuration.gwc.GwcDiskQuotaContextConfiguration;
import org.geoserver.configuration.gwc.GwcDiskQuotaRestConfiguration;
import org.geoserver.gwc.config.GeoserverXMLResourceProvider;
import org.geowebcache.config.ConfigurationException;
import org.geowebcache.diskquota.DiskQuotaMonitor;
import org.geowebcache.layer.TileLayerDispatcher;
import org.geowebcache.storage.DefaultStorageFinder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @see GwcDiskQuotaContextConfiguration
 * @see GwcDiskQuotaRestConfiguration
 * @see ConditionalOnDiskQuotaEnabled
 * @see ConditionalOnGeoWebCacheRestConfigEnabled
 * @since 1.0
 */
@Configuration(proxyBeanMethods = false)
@Import({GwcDiskQuotaContextConfiguration.class})
@SuppressWarnings({"java:S1118", "java:S6830"})
public class DiskQuotaAutoConfiguration {

    static {
        /*
         * Disable disk-quota by brute force for now. We need to resolve how and where
         * to store the configuration and database.
         */
        System.setProperty(DiskQuotaMonitor.GWC_DISKQUOTA_DISABLED, "true");
    }

    /**
     * Override {@literal DiskQuotaConfigLoader}, {@code GwcConfigurationTranspilerAggregator} chooses the wrong
     * constructor so it's excluded there
     */
    @Bean(name = "DiskQuotaConfigLoader")
    org.geowebcache.diskquota.ConfigLoader diskQuotaConfigLoader( //
            @Qualifier("DiskQuotaConfigResourceProvider")
                    GeoserverXMLResourceProvider diskQuotaConfigResourceProvider, //
            @Qualifier("gwcDefaultStorageFinder") DefaultStorageFinder storageFinder, //
            TileLayerDispatcher tld)
            throws ConfigurationException {

        return new org.geowebcache.diskquota.ConfigLoader(diskQuotaConfigResourceProvider, storageFinder, tld);
    }
}
