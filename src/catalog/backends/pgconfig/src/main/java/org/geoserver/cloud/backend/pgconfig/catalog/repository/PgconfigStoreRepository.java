/* (c) 2023 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.cloud.backend.pgconfig.catalog.repository;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.NonNull;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.plugin.CatalogInfoRepository.StoreRepository;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @since 1.4
 */
public class PgconfigStoreRepository extends PgconfigCatalogInfoRepository<StoreInfo> implements StoreRepository {

    /**
     * @param template
     */
    public PgconfigStoreRepository(@NonNull JdbcTemplate template) {
        super(StoreInfo.class, template);
    }

    @Override
    protected String getQueryTable() {
        return "storeinfos";
    }

    @Override
    protected String getReturnColumns() {
        return CatalogInfoRowMapper.STOREINFO_BUILD_COLUMNS;
    }

    @Override
    public <U extends StoreInfo> Optional<U> findById(@NonNull String id, Class<U> clazz) {
        return findOne(select("WHERE id = ?"), clazz, id);
    }

    @Override
    public void setDefaultDataStore(@NonNull WorkspaceInfo workspace, @NonNull DataStoreInfo dataStore) {
        String sql = "UPDATE workspaceinfo SET default_store = ? WHERE id = ?";
        template.update(sql, dataStore.getId(), workspace.getId());
    }

    @Override
    public void unsetDefaultDataStore(@NonNull WorkspaceInfo workspace) {
        String sql = "UPDATE workspaceinfo SET default_store = NULL WHERE id = ?";
        template.update(sql, workspace.getId());
    }

    @Override
    public Optional<DataStoreInfo> getDefaultDataStore(@NonNull WorkspaceInfo workspace) {
        String sql = select(
                """
                WHERE id = (SELECT default_store FROM workspaceinfo WHERE id = ?)
                """);
        return findOne(sql, DataStoreInfo.class, workspace.getId());
    }

    @Override
    public Stream<DataStoreInfo> getDefaultDataStores() {
        String sql =
                """
                SELECT s.store, s.workspace \
                FROM storeinfos s \
                INNER JOIN workspaceinfo w ON s."workspace.id" = w.id AND s.id = w.default_store;
                """;
        return super.queryForStream(DataStoreInfo.class, sql);
    }

    @Override
    public <U extends StoreInfo> Stream<U> findAllByWorkspace(
            @NonNull WorkspaceInfo workspace, @NonNull Class<U> clazz) {

        String sql = select("""
                WHERE "workspace.id" = ?
                """);

        final String workspaceId = workspace.getId();
        if (StoreInfo.class.equals(clazz)) {
            return super.queryForStream(clazz, sql, workspaceId);
        }

        String infotype = infoType(clazz);
        sql += " AND \"@type\" = ?::infotype";
        return super.queryForStream(clazz, sql, workspaceId, infotype);
    }

    @Override
    public <T extends StoreInfo> Stream<T> findAllByType(@NonNull Class<T> clazz) {

        if (StoreInfo.class.equals(clazz)) {
            return super.queryForStream(clazz, select(null));
        }

        String infotype = infoType(clazz);
        return super.queryForStream(clazz, select("WHERE \"@type\" = ?::infotype"), infotype);
    }

    @Override
    public <T extends StoreInfo> Optional<T> findByNameAndWorkspace(
            @NonNull String name, @NonNull WorkspaceInfo workspace, @NonNull Class<T> clazz) {

        return findOne(
                select("""
                WHERE "workspace.id" = ? AND name = ?
                """),
                clazz,
                workspace.getId(),
                name);
    }
}
