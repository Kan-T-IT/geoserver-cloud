/* (c) 2025 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.configuration.extension.ogcapi.core;

import org.geoserver.catalog.Catalog;
import org.geoserver.config.GeoServer;
import org.geoserver.ogcapi.APIConfigurationSupport;
import org.geoserver.ogcapi.AnnotatedHTMLMessageConverter;
import org.geoserver.ogcapi.ByteArrayMessageConverter;
import org.geoserver.ogcapi.DateTimeConverter;
import org.geoserver.ogcapi.DefaultAPIExceptionHandler;
import org.geoserver.ogcapi.FreemarkerTemplateSupport;
import org.geoserver.ogcapi.HttpHeaderLinksAppender;
import org.geoserver.ogcapi.LandingPageSlashFilter;
import org.geoserver.ogcapi.LinkInfoCallback;
import org.geoserver.ogcapi.LocalWorkspaceCallback;
import org.geoserver.ogcapi.OGCAPIXStreamPersisterInitializer;
import org.geoserver.ogcapi.SortByConverter;
import org.geoserver.ogcapi.SwaggerJSONAPIMessageConverter;
import org.geoserver.ogcapi.SwaggerJSONSchemaMessageConverter;
import org.geoserver.ogcapi.SwaggerYAMLMessageConverter;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.rest.RestConfiguration;
import org.geoserver.spring.config.annotations.TranspileXmlConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Provides all components of {@code gs-ogcapi-core} jar's {@code applicationContext.xml} but avoiding the too wide
 * component scan on package {@code org.geoserver.ogcapi}.
 *
 * <p>We should make the {@code gs-ogcapi-core} module upstream avoid doing a catch-all component scan, since concrete
 * extensions (e.g. gs-ogcapi-features) will make a component scan on their specific packages (e.g.
 * {@code org.geoserver.ogcapi.v1.features}).
 *
 * <p>Since {@code ignoreComponentScan = true} suppresses the XML's {@code <context:component-scan>}, the
 * {@code @Component}-annotated classes from ogcapi-core are registered explicitly as {@code @Bean} methods below.
 *
 * <p>{@link RestConfiguration} is imported because {@link APIConfigurationSupport} extends it and requires the
 * {@code mvcContentNegotiationManager} bean it provides (via
 * {@link org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport}).
 */
@Configuration(proxyBeanMethods = false)
@TranspileXmlConfig(locations = "jar:gs-ogcapi-core-.*!/applicationContext.xml", ignoreComponentScan = true)
@Import({RestConfiguration.class, OgcApiCoreConfiguration_Generated.class})
public class OgcApiCoreConfiguration {

    // Stereotyped components from ogcapi-core that would normally be picked up
    // by component scanning. Registered here explicitly to avoid the too-broad
    // scan on org.geoserver.ogcapi.

    @Bean
    SwaggerJSONAPIMessageConverter swaggerJSONAPIMessageConverter() {
        return new SwaggerJSONAPIMessageConverter();
    }

    @Bean
    SwaggerJSONSchemaMessageConverter swaggerJSONSchemaMessageConverter() {
        return new SwaggerJSONSchemaMessageConverter();
    }

    @Bean
    SwaggerYAMLMessageConverter swaggerYAMLMessageConverter() {
        return new SwaggerYAMLMessageConverter();
    }

    @Bean
    LocalWorkspaceCallback localWorkspaceCallback() {
        return new LocalWorkspaceCallback();
    }

    @Bean
    APIConfigurationSupport apiConfigurationSupport() {
        return new APIConfigurationSupport();
    }

    @Bean
    ByteArrayMessageConverter byteArrayMessageConverter() {
        return new ByteArrayMessageConverter();
    }

    @Bean
    HttpHeaderLinksAppender httpHeaderLinksAppender() {
        return new HttpHeaderLinksAppender();
    }

    @Bean
    DateTimeConverter dateTimeConverter() {
        return new DateTimeConverter();
    }

    @Bean
    SortByConverter sortByConverter() {
        return new SortByConverter();
    }

    @Bean
    OGCAPIXStreamPersisterInitializer ogcApiXStreamPersisterInitializer() {
        return new OGCAPIXStreamPersisterInitializer();
    }

    @Bean
    FreemarkerTemplateSupport freemarkerTemplateSupport(GeoServerResourceLoader loader) {
        return new FreemarkerTemplateSupport(loader);
    }

    @Bean
    LinkInfoCallback linkInfoCallback(GeoServer geoServer) {
        return new LinkInfoCallback(geoServer);
    }

    @Bean
    DefaultAPIExceptionHandler defaultAPIExceptionHandler(GeoServer geoServer) {
        return new DefaultAPIExceptionHandler(geoServer);
    }

    @Bean
    LandingPageSlashFilter landingPageSlashFilter(Catalog catalog) {
        return new LandingPageSlashFilter(catalog);
    }

    @Bean
    @SuppressWarnings("rawtypes")
    AnnotatedHTMLMessageConverter annotatedHTMLMessageConverter(
            FreemarkerTemplateSupport support, GeoServer geoServer) {
        return new AnnotatedHTMLMessageConverter(support, geoServer);
    }
}
