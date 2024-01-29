/*
 * (c) 2023 Open Source Geospatial Foundation - all rights reserved This code is licensed under the
 * GPL 2.0 license, available at the root application directory.
 */
package org.geoserver.cloud.config.catalog.backend.pgsql;

import lombok.extern.slf4j.Slf4j;

import org.geoserver.GeoServerConfigurationLock;
import org.geoserver.catalog.plugin.CatalogPlugin;
import org.geoserver.catalog.plugin.ExtendedCatalogFacade;
import org.geoserver.catalog.plugin.locking.LockProviderGeoServerConfigurationLock;
import org.geoserver.cloud.backend.pgsql.PgsqlBackendBuilder;
import org.geoserver.cloud.backend.pgsql.config.PgsqlConfigRepository;
import org.geoserver.cloud.backend.pgsql.config.PgsqlGeoServerFacade;
import org.geoserver.cloud.backend.pgsql.config.PgsqlUpdateSequence;
import org.geoserver.cloud.backend.pgsql.resource.FileSystemResourceStoreCache;
import org.geoserver.cloud.backend.pgsql.resource.PgsqlLockProvider;
import org.geoserver.cloud.backend.pgsql.resource.PgsqlResourceStore;
import org.geoserver.cloud.config.catalog.backend.core.CatalogProperties;
import org.geoserver.cloud.config.catalog.backend.core.GeoServerBackendConfigurer;
import org.geoserver.cloud.config.catalog.backend.pgsql.DatabaseMigrationConfiguration.Migrations;
import org.geoserver.config.GeoServerLoader;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.LockProvider;
import org.geoserver.platform.resource.ResourceStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.jdbc.lock.LockRepository;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * @since 1.4
 */
@Configuration(proxyBeanMethods = true)
@Slf4j(topic = "org.geoserver.cloud.config.catalog.backend.pgsql")
public class PgsqlBackendConfiguration extends GeoServerBackendConfigurer {

    private String instanceId;
    private DataSource dataSource;
    private CatalogProperties catalogProperties;

    /**
     * @param instanceId used as client-id for the {@link #pgsqlLockRepository() LockRepository}
     * @param dataSource DataSource for {@link #template()}, {@link #pgsqlLockRepository()}, and
     *     {@link #updateSequence()}
     * @param catalogProperties properties for {@link #rawCatalog()}
     * @param migrations required to make sure the migrations ran before this configuration takes
     *     place
     */
    PgsqlBackendConfiguration(
            @Value("${info.instance-id:}") String instanceId,
            @Qualifier("pgsqlConfigDatasource") DataSource dataSource,
            CatalogProperties catalogProperties,
            Migrations migrations) {
        this.instanceId = instanceId;
        this.dataSource = dataSource;
        this.catalogProperties = catalogProperties;
        log.info(
                "Loading geoserver config backend with {}. {}",
                PgsqlBackendConfiguration.class.getSimpleName(),
                migrations);
    }

    @Bean
    CatalogPlugin rawCatalog() {
        boolean isolated = catalogProperties.isIsolated();
        CatalogPlugin rawCatalog = new CatalogPlugin(isolated);
        GeoServerResourceLoader resourceLoader = resourceLoader();
        rawCatalog.setResourceLoader(resourceLoader);
        return rawCatalog;
    }

    @Bean
    @Override
    protected ExtendedCatalogFacade catalogFacade() {
        CatalogPlugin rawCatalog = rawCatalog();
        ExtendedCatalogFacade facade =
                new PgsqlBackendBuilder(dataSource).createCatalogFacade(rawCatalog);
        rawCatalog.setFacade(facade);
        return facade;
    }

    @Bean(name = "pgsqlCongigJdbcTemplate")
    JdbcTemplate template() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Override
    protected GeoServerConfigurationLock configurationLock() {
        LockProvider lockProvider = pgsqlLockProvider();
        return new LockProviderGeoServerConfigurationLock(lockProvider);
    }

    @Bean
    @Override
    protected PgsqlUpdateSequence updateSequence() {
        return new PgsqlUpdateSequence(dataSource, geoserverFacade());
    }

    @Bean
    @Override
    protected GeoServerLoader geoServerLoaderImpl() {
        log.debug("Creating GeoServerLoader {}", PgsqlGeoServerLoader.class.getSimpleName());
        return new PgsqlGeoServerLoader(resourceLoader(), configurationLock());
    }

    @Bean
    PgsqlConfigRepository configRepository() {
        return new PgsqlConfigRepository(template());
    }

    @Bean
    @Override
    protected PgsqlGeoServerFacade geoserverFacade() {
        return new PgsqlGeoServerFacade(configRepository());
    }

    @Bean
    @Override
    protected PgsqlResourceStore resourceStoreImpl() {
        log.debug("Creating ResourceStore {}", PgsqlResourceStore.class.getSimpleName());
        FileSystemResourceStoreCache resourceStoreCache = pgsqlFileSystemResourceStoreCache();
        JdbcTemplate template = template();
        PgsqlLockProvider lockProvider = pgsqlLockProvider();
        return new PgsqlResourceStore(resourceStoreCache, template, lockProvider);
    }

    @Bean
    FileSystemResourceStoreCache pgsqlFileSystemResourceStoreCache() {
        return FileSystemResourceStoreCache.newTempDirInstance();
    }

    @Bean
    @Override
    protected GeoServerResourceLoader resourceLoader() {
        log.debug(
                "Creating GeoServerResourceLoader {}",
                PgsqlGeoServerResourceLoader.class.getSimpleName());
        ResourceStore resourceStore = resourceStoreImpl();
        return new PgsqlGeoServerResourceLoader(resourceStore);
    }

    @Bean
    PgsqlLockProvider pgsqlLockProvider() {
        log.debug("Creating {}", PgsqlLockProvider.class.getSimpleName());
        return new PgsqlLockProvider(pgsqlLockRegistry());
    }

    /**
     * @return
     */
    private LockRegistry pgsqlLockRegistry() {
        log.debug("Creating {}", LockRegistry.class.getSimpleName());
        return new JdbcLockRegistry(pgsqlLockRepository());
    }

    @Bean
    LockRepository pgsqlLockRepository() {
        log.debug(
                "Creating {} for instance {}",
                LockRepository.class.getSimpleName(),
                this.instanceId);
        String id = this.instanceId;
        DefaultLockRepository lockRepository;
        if (StringUtils.hasLength(id)) {
            lockRepository = new DefaultLockRepository(dataSource, id);
        } else {
            lockRepository = new DefaultLockRepository(dataSource);
        }
        // override default table prefix "INT" by "RESOURCE_" (matching table definition
        // RESOURCE_LOCK in init.XXX.sql
        lockRepository.setPrefix("RESOURCE_");
        // time in ms to expire dead locks (10k is the default)
        lockRepository.setTimeToLive(300_000);
        return lockRepository;
    }
}
