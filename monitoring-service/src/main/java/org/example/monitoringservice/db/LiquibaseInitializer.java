package org.example.monitoringservice.db;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.monitoringservice.configuration.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LiquibaseInitializer extends AppConfig {

    public void initLiquibase() {
        try(Connection connection = DriverManager.getConnection(getUrl(), getUserName(), getPassword())) {
            Database db = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(getLiquibasePath(),
                    new ClassLoaderResourceAccessor(), db);
            liquibase.update();

        } catch (SQLException | LiquibaseException ex) {
            ex.printStackTrace();
        }
    }
}
