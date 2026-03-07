/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.spring.config.transpiler.generator;

import com.palantir.javapoet.MethodSpec;
import org.geoserver.spring.config.transpiler.context.BeanGenerationContext;
import org.geoserver.spring.config.transpiler.context.TranspilationContext;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * Core strategy interface for processing Spring bean definitions and generating Java code.
 *
 * <p>This interface defines the contract for generators that can transform Spring {@link BeanDefinition} objects into
 * JavaPoet {@link MethodSpec} objects representing {@code @Bean} methods in a {@code @Configuration} class.
 *
 * <p>The strategy pattern allows for different implementations to handle various types of bean definitions
 * (constructor-based, factory-method-based, etc.) while maintaining a consistent interface.
 *
 * <p>Implementations should be stateless and thread-safe to allow for concurrent processing of multiple bean
 * definitions.
 *
 * @since 3.0.0
 * @see BeanGenerationContext
 * @see TranspilationContext
 */
public interface BeanMethodGenerator {

    /**
     * Check if this generator can handle the given bean definition.
     *
     * <p>This method allows for generator selection based on bean definition characteristics such as:
     *
     * <ul>
     *   <li>Bean class type
     *   <li>Constructor arguments presence
     *   <li>Factory method configuration
     *   <li>Property values
     * </ul>
     *
     * @param beanDefinition the bean definition to check
     * @return true if this generator can process the bean definition
     */
    boolean canHandle(BeanDefinition beanDefinition);

    /**
     * Generate a {@code @Bean} method for the given bean definition.
     *
     * <p>This method creates a complete {@link MethodSpec} representing a Spring {@code @Bean} method that will
     * instantiate and configure the bean as specified in the original XML configuration.
     *
     * <p>The generated method should:
     *
     * <ul>
     *   <li>Be annotated with {@code @Bean}
     *   <li>Have the correct return type
     *   <li>Include proper parameter injection for dependencies
     *   <li>Handle constructor arguments and property values
     *   <li>Respect visibility modifiers from the transpilation context
     * </ul>
     *
     * @param beanContext the bean-specific generation context containing both bean-specific data and a reference to the
     *     TranspilationContext
     * @return the generated {@code @Bean} method specification
     * @throws IllegalArgumentException if the bean definition cannot be processed
     */
    MethodSpec generateBeanMethod(BeanGenerationContext beanContext);

    /**
     * Get the priority order for this generator.
     *
     * <p>When multiple generators can handle the same bean definition, the one with the highest priority (lowest
     * number) will be selected.
     *
     * <p>Common priority ranges:
     *
     * <ul>
     *   <li>0-99: High priority (specialized generators)
     *   <li>100-199: Medium priority (common cases)
     *   <li>200+: Low priority (fallback generators)
     * </ul>
     *
     * @return the generator priority (lower numbers = higher priority)
     */
    default int getPriority() {
        return 100; // Default medium priority
    }
}
