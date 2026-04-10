package com.ruoyi.jst.common.support;

import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight schema inspector used by compatibility endpoints.
 * <p>
 * Some FIX tasks require "return empty instead of failing" when optional tables
 * or columns are not deployed yet. This component centralizes metadata checks
 * and caches the results to avoid repeated database metadata scans.
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class SchemaInspector {

    private static final Logger log = LoggerFactory.getLogger(SchemaInspector.class);

    private final DataSource dataSource;
    private final Map<String, Boolean> tableCache = new ConcurrentHashMap<>();
    private final Map<String, Boolean> columnCache = new ConcurrentHashMap<>();

    public SchemaInspector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns true when the target table exists in the current database.
     *
     * @param tableName table name
     * @return true if present
     */
    public boolean tableExists(String tableName) {
        if (!StringUtils.hasText(tableName)) {
            return false;
        }
        String cacheKey = normalize(tableName);
        return tableCache.computeIfAbsent(cacheKey, key -> lookupTable(tableName));
    }

    /**
     * Returns true only if all requested columns exist.
     *
     * @param tableName table name
     * @param columns required columns
     * @return true when all columns are present
     */
    public boolean hasColumns(String tableName, String... columns) {
        if (!tableExists(tableName) || columns == null || columns.length == 0) {
            return false;
        }
        for (String column : columns) {
            if (!columnExists(tableName, column)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true when the target column exists on the given table.
     *
     * @param tableName table name
     * @param columnName column name
     * @return true if present
     */
    public boolean columnExists(String tableName, String columnName) {
        if (!StringUtils.hasText(tableName) || !StringUtils.hasText(columnName)) {
            return false;
        }
        String cacheKey = normalize(tableName) + "." + normalize(columnName);
        return columnCache.computeIfAbsent(cacheKey, key -> lookupColumn(tableName, columnName));
    }

    private boolean lookupTable(String tableName) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = safeSchema(connection);
            return existsTable(metaData, catalog, schema, tableName);
        } catch (SQLException e) {
            log.warn("[SchemaInspector] failed to inspect table {}", tableName, e);
            return false;
        }
    }

    private boolean lookupColumn(String tableName, String columnName) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = safeSchema(connection);
            return existsColumn(metaData, catalog, schema, tableName, columnName);
        } catch (SQLException e) {
            log.warn("[SchemaInspector] failed to inspect column {}.{}", tableName, columnName, e);
            return false;
        }
    }

    private boolean existsTable(DatabaseMetaData metaData, String catalog, String schema, String tableName)
            throws SQLException {
        String[] candidates = new String[]{tableName, tableName.toLowerCase(), tableName.toUpperCase()};
        for (String candidate : candidates) {
            try (ResultSet rs = metaData.getTables(catalog, schema, candidate, new String[]{"TABLE"})) {
                if (rs.next()) {
                    return true;
                }
            }
            try (ResultSet rs = metaData.getTables(catalog, null, candidate, new String[]{"TABLE"})) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existsColumn(DatabaseMetaData metaData, String catalog, String schema, String tableName,
                                 String columnName) throws SQLException {
        String[] tableCandidates = new String[]{tableName, tableName.toLowerCase(), tableName.toUpperCase()};
        String[] columnCandidates = new String[]{columnName, columnName.toLowerCase(), columnName.toUpperCase()};
        for (String tableCandidate : tableCandidates) {
            for (String columnCandidate : columnCandidates) {
                try (ResultSet rs = metaData.getColumns(catalog, schema, tableCandidate, columnCandidate)) {
                    if (rs.next()) {
                        return true;
                    }
                }
                try (ResultSet rs = metaData.getColumns(catalog, null, tableCandidate, columnCandidate)) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String safeSchema(Connection connection) {
        try {
            return connection.getSchema();
        } catch (SQLException e) {
            return null;
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
