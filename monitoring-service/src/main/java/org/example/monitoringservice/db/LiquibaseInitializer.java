package org.example.monitoringservice.db;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * The LiquibaseInitializer class is responsible for initializing Liquibase and applying
 * database migrations using a provided changelog file.
 */
@RequiredArgsConstructor
public class LiquibaseInitializer {

    private final String url;
    private final String userName;
    private final String userPassword;
    private final String changelogPath;

    /**
     * Initializes Liquibase and applies database migrations using the specified
     * changelog file.
     */
    public void initLiquibase() {
        try(Connection connection = DriverManager
                .getConnection(url, userName, userPassword)) {
            Database db = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            db.setDefaultSchemaName("monitoring_service_schema");
            db.setLiquibaseSchemaName("data_changelog_schema");
            Liquibase liquibase = new Liquibase(changelogPath,
                    new ClassLoaderResourceAccessor(), db);
            liquibase.update();

        } catch (SQLException | LiquibaseException ex) {
            ex.printStackTrace();
        }
    }
}
