/* (c) 2023 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.config.catalog.backend.pgconfig;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.config.GeoServerLoader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.util.StringUtils;

/** @since 1.4 */
@Configuration(proxyBeanMethods = false)
@Slf4j
public class PconfigDataSourceConfiguration {

    /**
     * Note: due to eager initialization of Catalog objects by {@link GeoServerLoader} we're optionally depending on the
     * {@code jndiInitializer} bean from {@code gs-spring-boot-simplejndi}
     */
    @Bean
    DataSource pgconfigDataSource(
            PgconfigBackendProperties configprops, @Qualifier("jndiInitializer") Optional<Object> jndiInitializer) {
        log.trace("jndiInitializer present: {}", jndiInitializer.isPresent());
        DataSourceProperties config = configprops.getDatasource();
        String jndiName = config.getJndiName();
        if (StringUtils.hasText(jndiName)) {
            log.info("Creating pgconfigDataSource from JNDI reference {}", jndiName);
            return new JndiDataSourceLookup().getDataSource(jndiName);
        }
        return config.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
}
