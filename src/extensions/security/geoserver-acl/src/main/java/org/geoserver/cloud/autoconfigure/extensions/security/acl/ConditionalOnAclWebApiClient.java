/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cloud.autoconfigure.extensions.security.acl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Spring Boot {@link AutoConfiguration @AutoConfiguration} conditional to enable/disable the Web-API client plugin
 * when:
 *
 * <ul>
 *   <li>The {@link ConditionalOnAclExtensionEnabled extension is enabled}
 *   <li>The WebAPI client ACL services are enabled via configuration property {@code geoserver.acl.client.enabled=true}
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnAclExtensionEnabled
@ConditionalOnProperty(
        name = "geoserver.acl.client.enabled",
        havingValue = "true",
        matchIfMissing = AclExtensionConfigurationProperties.DEFAULT)
public @interface ConditionalOnAclWebApiClient {}
