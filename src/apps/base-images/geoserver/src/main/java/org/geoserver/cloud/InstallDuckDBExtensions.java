/* (c) 2025 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cloud;

/**
 * DuckDB extension installer utility.
 * <p>
 * This utility class is designed specifically for build-time use in Docker images.
 * It pre-installs DuckDB extensions (parquet, httpfs, spatial) required for GeoParquet
 * functionality in GeoServer cloud microservices.
 * <p>
 * Running this during image build ensures that:
 * 1. All extensions are pre-installed in a shared base layer
 * 2. Extensions are available when containers run as arbitrary non-root users
 * 3. No runtime installation is required, avoiding permission issues
 * <p>
 * The extensions are installed to $HOME/.duckdb where HOME is expected to be
 * set to a directory with appropriate permissions.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InstallDuckDBExtensions {
    /**
     * Main method to install DuckDB extensions.
     * <p>
     * Loads the DuckDB JDBC driver, connects to an in-memory database, and executes
     * SQL commands to install the required extensions.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            Class.forName("org.duckdb.DuckDBDriver");
            System.out.println("Installing DuckDB extensions...");

            try (Connection conn = DriverManager.getConnection("jdbc:duckdb:");
                    Statement stmt = conn.createStatement()) {

                stmt.execute("INSTALL parquet;");
                System.out.println("Parquet extension installed");

                stmt.execute("INSTALL httpfs;");
                System.out.println("HTTP FS extension installed");

                stmt.execute("INSTALL spatial;");
                System.out.println("Spatial extension installed");
            }

            System.out.println("All extensions successfully installed to " + System.getenv("HOME") + "/.duckdb");
        } catch (Exception e) {
            System.err.println("Error installing extensions: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
