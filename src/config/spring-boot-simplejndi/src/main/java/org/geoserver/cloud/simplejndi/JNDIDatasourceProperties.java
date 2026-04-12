/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.simplejndi;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;

/**
 * Configuration for a single JNDI-bound datasource, extending Spring Boot's {@link DataSourceProperties} with
 * pool-sizing, startup-wait, and schema settings.
 *
 * <p>Instances are created from {@code jndi.datasources.<name>.*} entries in
 * {@link JNDIDataSourcesConfigurationProperties}; the map key becomes the datasource name and is bound under
 * {@code java:comp/env/jdbc/<name>} (unless an explicit fully-qualified name is provided).
 *
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JNDIDatasourceProperties extends DataSourceProperties {

    /** Whether this datasource should be created and bound to JNDI. Disabled entries are skipped at startup. */
    boolean enabled = true;

    /**
     * Whether to block startup until the database accepts connections, using a {@code DatabaseStartupValidator}. Useful
     * when the database may not be ready when the application boots.
     */
    boolean waitForIt = true;

    /**
     * Maximum time, in seconds, to wait for the database to become available when {@link #waitForIt} is {@code true}.
     */
    int waitTimeout = 60;

    /** Minimum number of idle connections HikariCP keeps in the pool. */
    int minimumIdle = 2;

    /** Maximum size of the HikariCP connection pool, including idle and in-use connections. */
    int maximumPoolSize = 10;

    /** Maximum time, in milliseconds, to wait for a connection from the pool before failing. */
    long connectionTimeout = 250;

    /** Maximum time, in milliseconds, an idle connection may sit in the pool before being evicted. */
    long idleTimeout = 60_000;

    /**
     * Default schema name applied to connections obtained from this datasource, or {@code null} to use the JDBC URL or
     * driver default.
     *
     * @since 1.3
     */
    String schema;
}
