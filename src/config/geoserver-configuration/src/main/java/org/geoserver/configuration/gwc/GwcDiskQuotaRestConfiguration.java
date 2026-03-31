/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.configuration.gwc;

import org.geowebcache.diskquota.rest.controller.DiskQuotaController;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/** Enables component scanning for package {@code org.geowebcache.diskquota.rest.controller} */
@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackageClasses = DiskQuotaController.class)
public class GwcDiskQuotaRestConfiguration {}
