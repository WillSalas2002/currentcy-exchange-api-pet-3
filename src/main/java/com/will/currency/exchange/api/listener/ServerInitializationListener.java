package com.will.currency.exchange.api.listener;

import com.will.currency.exchange.api.exception.DatabaseOperationException;
import com.will.currency.exchange.api.util.ConnectionManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.String.format;

@Slf4j
@WebListener
public class ServerInitializationListener implements ServletContextListener {

    private static final String FILE_CREATION_SQL = "creation.sql";
    private static final String FILE_INITIALIZATION_SQL = "initialization.sql";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String creationSqlAbsolutePath = getFileAbsolutePath(FILE_CREATION_SQL);
        String initializationSqlAbsolutePath = getFileAbsolutePath(FILE_INITIALIZATION_SQL);

        String tableCreationSql = readFile(creationSqlAbsolutePath);
        String tablePopulationSql = readFile(initializationSqlAbsolutePath);

        try (Connection connection = ConnectionManager.get();
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");

            statement.executeUpdate("DROP TABLE IF EXISTS currency");
            statement.executeUpdate("DROP TABLE IF EXISTS exchange_rate");

            statement.executeUpdate(tableCreationSql);
            statement.executeUpdate(tablePopulationSql);
        } catch (SQLException e) {
            log.error("Error during database table create and population");
            throw new DatabaseOperationException(format("Failed to initialize database: %s", e.getMessage()));
        }
    }

    private static String getFileAbsolutePath(String sqlFile) {
        try {
            URL resource = ServerInitializationListener.class.getClassLoader().getResource(sqlFile);
            if (resource == null) {
                throw new RuntimeException("SQL file not found in resources!");
            }
            return new File(resource.toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            log.error("Error happened while reading sql script files");
            throw new RuntimeException(e);
        }
    }
}
