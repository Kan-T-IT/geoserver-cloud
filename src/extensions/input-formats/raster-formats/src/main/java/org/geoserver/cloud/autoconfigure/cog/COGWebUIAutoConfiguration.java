/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.autoconfigure.cog;

import org.geoserver.configuration.community.cog.COGWebUIConfiguration;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.data.store.cog.panel.CogRasterEditPanel;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * Auto configuration to enable the COG customized store panel when the web-ui is present.
 *
 * @see COGWebUIConfiguration
 */
@AutoConfiguration
@ConditionalOnClass({GeoServerApplication.class, CogRasterEditPanel.class})
@Import(COGWebUIConfiguration.class)
public class COGWebUIAutoConfiguration {}
