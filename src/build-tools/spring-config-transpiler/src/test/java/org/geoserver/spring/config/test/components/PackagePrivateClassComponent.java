/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.spring.config.test.components;

/**
 * Package-private class used to verify that the transpiler emits a {@code @Bean} method whose return type is not the
 * bean class itself (which would be inaccessible from the generated configuration class's package) but its nearest
 * public ancestor.
 */
@SuppressWarnings("java:S2094") // empty class is intentional
class PackagePrivateClassComponent extends PublicAncestor {
    PackagePrivateClassComponent() {}
}
