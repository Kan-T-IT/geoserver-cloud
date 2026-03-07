/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.spring.config.transpiler.context;

import lombok.Value;
import org.w3c.dom.Element;

/**
 * Information about a component-scan configuration from XML. Based on the ComponentScanInfo from the old
 * spring-factory-processor.
 *
 * @since 3.0.0
 */
@Value
public class ComponentScanInfo {

    private final String basePackage;
    private final String scopeResolver;
    private final boolean useDefaultFilters;
    private final String resourcePattern;
    private final Element originalElement;

    public ComponentScanInfo(Element element) {
        this.originalElement = element;
        this.basePackage = element.getAttribute("base-package");
        this.scopeResolver = element.getAttribute("scope-resolver");
        this.useDefaultFilters = !"false".equals(element.getAttribute("use-default-filters"));
        this.resourcePattern = element.getAttribute("resource-pattern");
    }

    /** Split base-package attribute into individual packages. */
    public String[] getBasePackages() {
        if (basePackage == null || basePackage.trim().isEmpty()) {
            return new String[0];
        }
        return basePackage.split("[,;\\s]+");
    }
}
