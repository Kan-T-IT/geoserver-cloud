/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.simplejndi;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * Auto-configuration that creates the datasources declared under the {@code jndi.datasources.*} property tree and binds
 * them to the JNDI initial context provided by {@link JNDIStaticContextInitializer}.
 *
 * <p>Runs at {@link Ordered#HIGHEST_PRECEDENCE} so the JNDI bindings exist before any other auto-configuration looks
 * them up.
 *
 * @since 1.0
 */
@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(JNDIDataSourcesConfigurationProperties.class)
@SuppressWarnings("java:S1118") // Suppress SonarLint warning, constructor needs to be public
public class JNDIDataSourcesAutoConfiguration {

    @Bean
    JNDIInitializer jndiInitializer(JNDIDataSourcesConfigurationProperties config) {
        return new JNDIInitializer(config);
    }
}
