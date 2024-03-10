package com.exemple.socialapp.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/socialnetwork");
        config.setUsername("postgres");
        config.setPassword("postgres");

        // Set other configuration options as needed

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Close the connection pool when the application exits
    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}

