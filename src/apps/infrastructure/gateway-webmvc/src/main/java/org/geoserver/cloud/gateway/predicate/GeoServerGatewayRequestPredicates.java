/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cloud.gateway.predicate;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.cloud.gateway.server.mvc.common.Shortcut;
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates;
import org.springframework.cloud.gateway.server.mvc.predicate.PredicateSupplier;
import org.springframework.web.servlet.function.RequestPredicate;

/**
 * Single entry point for all custom GeoServer Cloud gateway request predicates.
 *
 * <p>Follows the Spring Cloud Gateway Server MVC convention established by {@link GatewayRequestPredicates}: an
 * interface with {@code static} methods annotated with {@link Shortcut @Shortcut}, and a single
 * {@link GeoServerGatewayPredicateSupplier} that exposes all predicates for YAML route configuration.
 *
 * @see GatewayRequestPredicates
 * @since 3.0.0
 */
public interface GeoServerGatewayRequestPredicates {

    /**
     * Creates a predicate that matches requests by regular expressions on both the name and value of query parameters.
     *
     * <p>YAML: {@code predicates: - RegExpQuery=(?i:service),(?i:wms)}
     *
     * @param paramRegexp regex to match query parameter names
     * @param valueRegexp optional regex to match parameter values; if blank, only the parameter name is matched
     * @see RegExpQueryRoutePredicate
     */
    @Shortcut
    static RequestPredicate regExpQuery(String paramRegexp, String valueRegexp) {
        return new RegExpQueryRoutePredicate(paramRegexp, valueRegexp);
    }

    /** Single {@link GeoServerGatewayPredicateSupplier} that exposes all custom gateway predicates for YAML config. */
    class GeoServerGatewayPredicateSupplier implements PredicateSupplier {
        @Override
        public Collection<Method> get() {
            return Arrays.asList(GeoServerGatewayRequestPredicates.class.getMethods());
        }
    }
}
