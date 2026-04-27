/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.gateway.predicate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.ServerRequest;

class RegExpQueryRoutePredicateTest {

    @Test
    void nameOnly_present() {
        assertThat(matches("service", null, "service", "wfs")).isTrue();
    }

    @Test
    void nameOnly_absent() {
        assertThat(matchesNoParams("service", null)).isFalse();
    }

    @Test
    void nameAndValue_match() {
        assertThat(matches("service", "wfs", "service", "wfs")).isTrue();
    }

    @Test
    void nameMatch_valueMismatch() {
        assertThat(matches("service", "wms", "service", "wfs")).isFalse();
    }

    @Test
    void nameNoMatch() {
        assertThat(matches("request", "wfs", "service", "wfs")).isFalse();
    }

    @Test
    void caseInsensitiveName() {
        assertThat(matches("(?i:service)", null, "SERVICE", "wfs")).isTrue();
    }

    @Test
    void caseInsensitiveValue() {
        assertThat(matches("(?i:service)", "(?i:wfs)", "Service", "WFS")).isTrue();
    }

    @Test
    void exactMatch_noPartial() {
        assertThat(matches("service", null, "myservice", "wfs")).isFalse();
    }

    @Test
    void multipleParams() {
        RequestPredicate predicate = GeoServerGatewayRequestPredicates.regExpQuery("(?i:service)", "(?i:wfs)");
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/test");
        mockRequest.addParameter("request", "GetCapabilities");
        mockRequest.addParameter("service", "wfs");
        ServerRequest request = ServerRequest.create(mockRequest, List.of());
        assertThat(predicate.test(request)).isTrue();
    }

    @Test
    void emptyValueRegexp_nameOnly() {
        assertThat(matches("service", "", "service", "wfs")).isTrue();
    }

    private boolean matches(String paramRegexp, String valueRegexp, String paramName, String paramValue) {
        RequestPredicate predicate = GeoServerGatewayRequestPredicates.regExpQuery(paramRegexp, valueRegexp);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/test");
        mockRequest.addParameter(paramName, paramValue);
        ServerRequest request = ServerRequest.create(mockRequest, List.of());
        return predicate.test(request);
    }

    private boolean matchesNoParams(String paramRegexp, String valueRegexp) {
        RequestPredicate predicate = GeoServerGatewayRequestPredicates.regExpQuery(paramRegexp, valueRegexp);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/test");
        ServerRequest request = ServerRequest.create(mockRequest, List.of());
        return predicate.test(request);
    }
}
