/* (c) 2025 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.configuration.extension.ogcapi.features;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.geoserver.config.GeoServer;
import org.geoserver.configuration.extension.ogcapi.core.OgcApiCoreConfiguration;
import org.geoserver.ogcapi.APIFilterParser;
import org.geoserver.ogcapi.APIService;
import org.geoserver.ogcapi.FreemarkerTemplateSupport;
import org.geoserver.ogcapi.v1.features.FeatureResponseMessageConverter;
import org.geoserver.ogcapi.v1.features.FeatureService;
import org.geoserver.ogcapi.v1.features.FeaturesExceptionHandler;
import org.geoserver.ogcapi.v1.features.FeaturesSampleDataProvider;
import org.geoserver.ogcapi.v1.features.GetFeatureHTMLMessageConverter;
import org.geoserver.ogcapi.v1.features.JSONFGFeaturesResponse;
import org.geoserver.ogcapi.v1.features.RFCGeoJSONFeaturesResponse;
import org.geoserver.spring.config.annotations.TranspileXmlConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration class for OGC API Features service.
 *
 * <p>Uses {@code ignoreComponentScan = true} to avoid the XML's component scan on
 * {@code org.geoserver.ogcapi.v1.features}. The stereotyped components are registered explicitly below.
 *
 * @see OgcApiCoreConfiguration
 */
@Configuration(proxyBeanMethods = false)
@TranspileXmlConfig(locations = "jar:gs-ogcapi-features-.*!/applicationContext.xml", ignoreComponentScan = true)
@Import({OgcApiCoreConfiguration.class, OgcApiFeaturesConfiguration_Generated.class})
@SuppressWarnings("java:S6830")
public class OgcApiFeaturesConfiguration {

    // Stereotyped components from ogcapi-features that would normally be picked up
    // by component scanning. Registered here explicitly to avoid the too-broad
    // scan on org.geoserver.ogcapi.

    /** Stereotyped as {@link APIService @APIService} which extends {@code @Component} */
    @Bean
    FeatureService featureService(GeoServer geoServer, APIFilterParser filterParser) {
        return new FeatureService(geoServer, filterParser);
    }

    @Bean
    FeatureResponseMessageConverter featureResponseMessageConverter() {
        return new FeatureResponseMessageConverter();
    }

    @Bean
    @SuppressWarnings("java:S3011") // setAccessible
    FeaturesSampleDataProvider featuresSampleDataProvider(FeatureService featureService)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException,
                    InvocationTargetException {
        Constructor<FeaturesSampleDataProvider> ctor =
                FeaturesSampleDataProvider.class.getDeclaredConstructor(FeatureService.class);
        ctor.setAccessible(true);
        return ctor.newInstance(featureService);
    }

    @Bean("RFCGeoJSONFeaturesResponse")
    RFCGeoJSONFeaturesResponse rfcGeoJSONFeaturesResponse(GeoServer gs) {
        return new RFCGeoJSONFeaturesResponse(gs);
    }

    @Bean("JSONFGFeaturesResponse")
    JSONFGFeaturesResponse jsonFGFeaturesResponse(GeoServer gs) {
        return new JSONFGFeaturesResponse(gs);
    }

    @Bean
    FeaturesExceptionHandler featuresExceptionHandler(GeoServer geoServer) {
        return new FeaturesExceptionHandler(geoServer);
    }

    @Bean
    GetFeatureHTMLMessageConverter getFeatureHTMLMessageConverter(
            FreemarkerTemplateSupport templateSupport, GeoServer geoServer) {
        return new GetFeatureHTMLMessageConverter(templateSupport, geoServer);
    }
}
