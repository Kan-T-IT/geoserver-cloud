/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.wcs;

import org.geoserver.configuration.core.wcs.WCS20Configuration;
import org.geoserver.configuration.core.wcs.WCSCoreConfiguration;
import org.geoserver.configuration.extension.wcs.WCS10Configuration;
import org.geoserver.configuration.extension.wcs.WCS11Configuration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({WCSCoreConfiguration.class, WCS10Configuration.class, WCS11Configuration.class, WCS20Configuration.class})
public class WcsApplicationConfiguration {}
