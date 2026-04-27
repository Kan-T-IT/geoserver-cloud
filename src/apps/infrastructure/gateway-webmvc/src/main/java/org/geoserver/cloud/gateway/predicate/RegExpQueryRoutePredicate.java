/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.gateway.predicate;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.ServerRequest;

/**
 * {@link RequestPredicate} that matches HTTP request query parameters using Java regular expressions for both parameter
 * name and value.
 *
 * <p>Similar to the built-in {@code Query} route predicate, but supports regular expressions on the parameter name in
 * addition to the value. This is useful for case-insensitive matching or matching multiple parameter name variants.
 *
 * <p>If {@code valueRegexp} is blank, only the presence of a matching parameter name is required. Otherwise, at least
 * one value of the matched parameter must also match {@code valueRegexp}.
 *
 * <p>Instances are created via {@link GeoServerGatewayRequestPredicates#regExpQuery(String, String)} for YAML route
 * configuration:
 *
 * <p>{@snippet lang = "yaml": - id: wms_ows uri: http://wms:8080 predicates: # match service=wms case insensitively -
 * RegExpQuery=(?i:service),(?i:wms) }
 *
 * @see GeoServerGatewayRequestPredicates#regExpQuery(String, String)
 * @since 3.0.0
 */
class RegExpQueryRoutePredicate implements RequestPredicate {

    private final String paramRegexp;
    private final String valueRegexp;

    /**
     * @param paramRegexp regex to match query parameter names (matched against the full name via
     *     {@link String#matches(String)})
     * @param valueRegexp optional regex to match parameter values; if blank, only the parameter name is checked
     */
    public RegExpQueryRoutePredicate(String paramRegexp, String valueRegexp) {
        this.paramRegexp = paramRegexp;
        this.valueRegexp = valueRegexp;
    }

    @Override
    public boolean test(ServerRequest request) {
        boolean matchNameOnly = !StringUtils.hasText(valueRegexp);
        Map<String, String[]> parameterMap = request.servletRequest().getParameterMap();
        Set<String> parameterNames = parameterMap.keySet();
        Optional<String> paramName = parameterNames.stream()
                .filter(name -> name.matches(paramRegexp))
                .findFirst();
        boolean paramNameMatches = paramName.isPresent();
        if (matchNameOnly) {
            return paramNameMatches;
        }
        if (!paramNameMatches) {
            return false;
        }
        String[] values = parameterMap.get(paramName.get());
        return values != null && Arrays.stream(values).anyMatch(v -> v != null && v.matches(valueRegexp));
    }
}
