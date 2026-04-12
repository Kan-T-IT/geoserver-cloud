/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.simplejndi;

import java.util.Map;
import java.util.TreeMap;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Root {@link ConfigurationProperties} for JNDI datasources, bound to the {@code jndi} prefix.
 *
 * <p>Each entry under {@code jndi.datasources.<name>} produces a {@link JNDIDatasourceProperties} instance that the
 * {@link JNDIInitializer} turns into a HikariCP {@link javax.sql.DataSource} bound under
 * {@code java:comp/env/jdbc/<name>}.
 *
 * @since 1.0
 */
@Data
@ConfigurationProperties(value = "jndi")
public class JNDIDataSourcesConfigurationProperties {

    /**
     * Datasource configurations keyed by JNDI name (the simple name, not the full {@code java:comp/env/jdbc/} path).
     */
    private Map<String, JNDIDatasourceProperties> datasources = new TreeMap<>();
}
