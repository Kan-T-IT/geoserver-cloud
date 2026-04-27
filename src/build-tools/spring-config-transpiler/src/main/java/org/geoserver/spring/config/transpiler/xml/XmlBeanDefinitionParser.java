/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.spring.config.transpiler.xml;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.geoserver.spring.config.transpiler.context.ComponentScanInfo;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.io.ByteArrayResource;

/**
 * Utility class for parsing XML bean definitions with proper alias resolution.
 *
 * <p>This utility extracts the XML parsing logic from ConfigurationClassGenerator to make it reusable for both
 * transpiler and testing scenarios.
 *
 * @since 3.0.0
 */
@UtilityClass
public class XmlBeanDefinitionParser {

    /** Result of parsing XML content containing bean definitions, enhanced bean info, and component scans. */
    public static class ParsedXmlResult {
        private final Map<String, BeanDefinition> beanDefinitions;
        private final Map<String, EnhancedBeanDefinition> enhancedBeanInfos;
        private final List<ComponentScanInfo> componentScans;

        public ParsedXmlResult(
                Map<String, BeanDefinition> beanDefinitions,
                Map<String, EnhancedBeanDefinition> enhancedBeanInfos,
                List<ComponentScanInfo> componentScans) {
            this.beanDefinitions = beanDefinitions;
            this.enhancedBeanInfos = enhancedBeanInfos;
            this.componentScans = componentScans;
        }

        public Map<String, BeanDefinition> getBeanDefinitions() {
            return beanDefinitions;
        }

        public Map<String, EnhancedBeanDefinition> getEnhancedBeanInfos() {
            return enhancedBeanInfos;
        }

        public List<ComponentScanInfo> getComponentScans() {
            return componentScans;
        }
    }

    /**
     * Parse XML content string into bean definitions with proper alias resolution.
     *
     * @param xmlContent the XML content as string
     * @return parsed result containing bean definitions and name resolution info
     */
    public static ParsedXmlResult parseXmlContent(final String xmlContent) {
        String fullXml = ensureXmHasPrologAndBeanElement(xmlContent);

        // Use the enhanced XML reader that already handles everything
        EnhancedXmlBeanDefinitionReader reader = new EnhancedXmlBeanDefinitionReader();

        reader.loadBeanDefinitions(new ByteArrayResource(fullXml.getBytes(StandardCharsets.UTF_8)));

        // Extract bean definitions from the factory - only primary bean names, not aliases
        // Aliases will be handled through BeanNameInfo and included in @Bean(name={...}) arrays
        Map<String, BeanDefinition> beanDefinitions = reader.getBeanDefinitions();

        // Get enhanced bean infos and component scans from the enhanced reader
        // The enhanced bean infos contain all the name resolution information
        Map<String, EnhancedBeanDefinition> enhancedBeanInfos = reader.getEnhancedBeanInfos();
        List<ComponentScanInfo> componentScans = reader.getComponentScans();

        return new ParsedXmlResult(beanDefinitions, enhancedBeanInfos, componentScans);
    }

    // Add XML prolog if missing and wrap in beans element if needed
    private static String ensureXmHasPrologAndBeanElement(final String xmlContent) {
        StringBuilder fullXml = new StringBuilder(xmlContent.trim());
        final boolean needsXmlProlog = !xmlContent.contains("<?xml");
        final boolean needsBeansWrapper = !xmlContent.contains("<beans");

        if (needsBeansWrapper) {
            // Missing both prolog and beans wrapper - add both
            fullXml.insert(
                    0,
                    """
                    <beans xmlns="http://www.springframework.org/schema/beans"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd">
                    """);
            fullXml.append("</beans>");
        }

        if (needsXmlProlog) {
            // Missing only the XML prolog - add it without wrapping in beans
            fullXml.insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        }
        return fullXml.toString();
    }
}
