package org.example.monitoringservice.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {

    private final Properties properties;

    public AppConfig() {
        properties = new Properties();
        try(FileInputStream fileInputStream =
                    new FileInputStream("src/main/resources/config.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return properties.getProperty("database.url");
    }

    public String getUserName() {
        return properties.getProperty("database.username");
    }

    public String getPassword() {
        return properties.getProperty("database.password");
    }

    public String getLiquibasePath() {
        return properties.getProperty("liquibase.path");
    }
}
