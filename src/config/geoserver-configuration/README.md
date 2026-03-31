# gs-spring-configuration

Centralized transpiled Spring XML configurations for GeoServer modules and plugins.

This module uses `@TranspileXmlConfig` to convert GeoServer's `applicationContext.xml` files into
Java `@Configuration` classes at compile time, eliminating runtime XML parsing entirely.

## How it works

Each `@TranspileXmlConfig` annotation tells the compile-time annotation processor to:
1. Find the XML file(s) matching the `locations` pattern
2. Parse all `<bean>` definitions
3. Apply include/exclude filters
4. Generate a `@Configuration` class with equivalent `@Bean` methods

The generated class is then brought into the application context via `@Import`.

## `@TranspileXmlConfig` by example

### Simplest case

Transpile all beans from a JAR's `applicationContext.xml`:

```java
@Configuration(proxyBeanMethods = false)
@TranspileXmlConfig(locations = "jar:gs-geoparquet-.*!/applicationContext.xml")
@Import(GeoParquetWebUIConfiguration_Generated.class)
public class GeoParquetWebUIConfiguration {}
```

The `jar:` prefix with a regex pattern matches the JAR filename on the annotation processor
classpath. The generated class is named `<AnnotatedClass>_Generated` by default.

### Excluding beans

When some beans are provided by other means (e.g., a Spring Boot backend configurer),
exclude them by name:

```java
@Configuration(proxyBeanMethods = false)
@TranspileXmlConfig(
        locations = "jar:gs-main-.*!/applicationContext.xml",
        excludes = {
            "catalog",       // overridden by cloud-specific catalog implementation
            "rawCatalog",    // provided by GeoServerBackendConfigurer
            "geoServer",     // provided by GeoServerBackendConfigurer
            "resourceLoader" // provided by GeoServerBackendConfigurer
        })
@Import(GeoServerMainConfiguration_Generated.class)
public class GeoServerMainConfiguration {}
```

Exclude patterns are regex — `"proxy.*"` would exclude all beans whose name starts with `proxy`.

### Cross-package imports with `publicAccess`

When the importing class is in a different package than the generated class, the generated
class and its `@Bean` methods must be `public`:

```java
@Configuration(proxyBeanMethods = false)
@TranspileXmlConfig(locations = "jar:gs-wfs-.*!/applicationContext.xml", publicAccess = true)
@Import(WFSCoreConfiguration_Generated.class)
public class WFSCoreConfiguration {}
```

### Custom generated class name with `targetClass`

Override the default `_Generated` suffix when you need a specific name:

```java
@TranspileXmlConfig(
        locations = "jar:gs-gwc-[0-9]+.*!/geowebcache-geoserver-context.xml",
        targetClass = "GwcGeoServerContextConfiguration",
        publicAccess = true,
        excludes = {"GeoSeverTileLayerCatalog", "gwcGeoServervConfigPersister"})
```

### Suppressing component scanning with `ignoreComponentScan`

Some XML files contain `<context:component-scan>` that would be too broad in a cloud
deployment. Use `ignoreComponentScan` and register the needed components explicitly:

```java
@Configuration(proxyBeanMethods = false)
@TranspileXmlConfig(locations = "jar:gs-ogcapi-features-.*!/applicationContext.xml", ignoreComponentScan = true)
@Import({OgcApiCoreConfiguration.class, OgcApiFeaturesConfiguration_Generated.class})
public class OgcApiFeaturesConfiguration {

    // Stereotyped components that would have been picked up by the XML's component scan,
    // registered explicitly instead
    @Bean
    FeatureService featureService(GeoServer geoServer, APIFilterParser filterParser) {
        return new FeatureService(geoServer, filterParser);
    }
}
```

### Multiple annotations on a single class (`@Repeatable`)

`@TranspileXmlConfig` is repeatable. Use this to generate multiple configuration classes
from a single aggregator, each targeting a different XML file:

```java
@TranspileXmlConfig(
        locations = "jar:gs-gwc-[0-9]+.*!/geowebcache-geoserver-context.xml",
        targetClass = "GwcGeoServerContextConfiguration",
        publicAccess = true)
@TranspileXmlConfig(
        locations = "jar:gs-gwc-[0-9]+.*!/geowebcache-core-context.xml",
        targetClass = "GwcCoreContextConfiguration",
        publicAccess = true,
        excludes = {"gwcAppCtx", "gwcXmlConfig"})
@TranspileXmlConfig(
        locations = "jar:gs-gwc-[0-9]+.*!/geowebcache-wmsservice-context.xml",
        targetClass = "GwcWMSServiceConfiguration",
        publicAccess = true)
@Import({
    GwcGeoServerContextConfiguration.class,
    GwcCoreContextConfiguration.class,
    GwcWMSServiceConfiguration.class
})
public class GwcConfigurationTranspilerAggregator {}
```

## `@TranspileXmlConfig` attribute reference

| Attribute            | Type       | Default                   | Description |
|----------------------|------------|---------------------------|-------------|
| `value` / `locations`| `String[]` | --                        | XML resource locations (`classpath:`, `jar:artifact-regex-.*!/path.xml`, `file:`) |
| `targetPackage`      | `String`   | annotated class's package | Package for the generated class |
| `targetClass`        | `String`   | `<ClassName>_Generated`   | Name of the generated class |
| `includes`           | `String[]` | `{".*"}`                  | Regex patterns -- only matching bean names are transpiled |
| `excludes`           | `String[]` | `{}`                      | Regex patterns -- matching beans are skipped (takes precedence over includes) |
| `publicAccess`       | `boolean`  | `false`                   | Generate `public` class and methods (needed for cross-package `@Import`) |
| `proxyBeanMethods`   | `boolean`  | `false`                   | Controls `@Configuration(proxyBeanMethods=...)` on the generated class |
| `ignoreComponentScan`| `boolean`  | `false`                   | When `true`, `<context:component-scan>` elements in the XML are not transpiled |

## Usage from Spring Boot auto-configurations

This module contains pure Spring `@Configuration` classes with no conditional logic.
Spring Boot `@AutoConfiguration` classes in specific modules then import them with the
appropriate conditionals:

```java
@AutoConfiguration
@ConditionalOnOgcApiFeatures
@ConditionalOnGeoServerWebUI
@EnableConfigurationProperties(OgcApiFeatureConfigProperties.class)
@Import(OgcApiFeaturesWebUIConfiguration.class)
class OgcApiFeaturesWebUIAutoConfiguration {}
```

This separates **what** beans exist (this module) from **when** they're activated
(auto-configuration modules).
