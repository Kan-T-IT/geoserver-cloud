/*
 * (c) 2023 Open Source Geospatial Foundation - all rights reserved This code is licensed under the
 * GPL 2.0 license, available at the root application directory.
 */
package org.geoserver.cloud.autoconfigure.catalog.backend.pgconfig;

import org.geoserver.cloud.config.catalog.backend.core.CatalogProperties;
import org.geoserver.cloud.config.catalog.backend.pgconfig.PgsqlBackendConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * @since 1.4
 */
@AutoConfiguration(after = PgsqlMigrationAutoConfiguration.class)
@ConditionalOnPgsqlBackendEnabled
@EnableConfigurationProperties(CatalogProperties.class)
@Import(PgsqlBackendConfiguration.class)
public class PgsqlBackendAutoConfiguration {}
