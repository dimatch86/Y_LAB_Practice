package org.example.monitoringservice.in.servlet.liquibase;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import org.example.monitoringservice.configuration.AppPropertiesProvider;
import org.example.monitoringservice.configuration.AppProps;
import org.example.monitoringservice.db.LiquibaseInitializer;
import org.example.monitoringservice.exception.custom.DbException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

/**
 * A servlet for initializing Liquibase on application startup.
 * This servlet is loaded on startup and initializes Liquibase using the application properties.
 */
@WebServlet(loadOnStartup = 1, urlPatterns = { "/start" })
public class LiquibaseServlet extends HttpServlet {
    private LiquibaseInitializer liquibaseInitializer;

    /**
     * Initializes Liquibase by creating an instance of LiquibaseInitializer
     * with the database URL, username, password, and Liquibase path from the application properties.
     */
    @Override
    public void init() throws ServletException {
        AppProps appProps = AppPropertiesProvider.getProperties();
        String dbUrl = appProps.getUrl();
        String dbUserName = appProps.getUserName();
        String dbPassword = appProps.getPassword();
        try(Connection connection = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
            Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS data_changelog_schema;");
            stmt.execute("CREATE SCHEMA IF NOT EXISTS monitoring_service_schema;");
            stmt.execute("CREATE SEQUENCE jdbc_sequence START 1");

        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
        liquibaseInitializer =
                new LiquibaseInitializer(dbUrl, dbUserName, dbPassword, appProps.getLiquibasePath());
        liquibaseInitializer.initLiquibase();
    }
}
