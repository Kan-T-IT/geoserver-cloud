/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.configuration.core.wms;

import java.util.List;
import org.geoserver.config.GeoServer;
import org.geoserver.platform.Service;
import org.geoserver.spring.config.annotations.TranspileXmlConfig;
import org.geoserver.wms.WMSServiceExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.PropertyResolver;

/**
 * Transpiled XML configuration from {@literal jar:gs-wms1_1-.*!/applicationContext.xml}
 *
 * @see WMS11Configuration_Generated
 */
@Configuration(proxyBeanMethods = false)
@TranspileXmlConfig(
        locations = "jar:gs-wms1_1-.*!/applicationContext.xml",
        excludes = {"wms11ExceptionHandler"})
@Import(WMS11Configuration_Generated.class)
@SuppressWarnings({"java:S1118", "java:S101"})
public class WMS11Configuration {
    /**
     * Overrides the {@code @TranspileXmlConfig} excluded {@code wms11ExceptionHandler} bean with a
     * {@link StatusCodeWmsExceptionHandler} to support setting a non 200 status code on http responses.
     *
     * <p>The original bean definition is as follows, which this bean method respects:
     *
     * <pre>
     * <code>
     *  <!-- service exception handler -->
     *  <bean id="wms11ExceptionHandler" class=
     * "org.geoserver.wms.WMSServiceExceptionHandler">
     *          <constructor-arg>
     *                  <list>
     *                          <ref bean="wms-1_1_1-ServiceDescriptor"/>
     *                  </list>
     *          </constructor-arg>
     *          <constructor-arg ref="geoServer"/>
     *  </bean>
     * </code>
     * </pre>
     *
     * @param propertyResolver
     * @return
     */
    @Bean
    WMSServiceExceptionHandler wms11ExceptionHandler(
            @SuppressWarnings("java:S6830") @Qualifier("wms-1_1_1-ServiceDescriptor") Service wms11,
            GeoServer geoServer,
            PropertyResolver propertyResolver) {
        return new StatusCodeWmsExceptionHandler(List.of(wms11), geoServer, propertyResolver);
    }
}
