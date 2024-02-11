package org.example.monitoringservice.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * This class provides access to the application properties.
 */

public class AppPropertiesProvider {

    /**
     * Retrieves the application properties.
     *
     * @return An instance of AppProps containing the application properties.
     */
    public static AppProps getProperties(){
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("/config.properties");

        Properties prop = new Properties();
        try {
            prop.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String dbUrl = prop.getProperty("database.url");
        String dbUsername = prop.getProperty("database.username");
        String dbPassword = prop.getProperty("database.password");
        String changelogPath = prop.getProperty("liquibase.path");
        return AppProps.builder()
                .url(dbUrl)
                .userName(dbUsername)
                .password(dbPassword)
                .liquibasePath(changelogPath)
                .build();
    }
}
