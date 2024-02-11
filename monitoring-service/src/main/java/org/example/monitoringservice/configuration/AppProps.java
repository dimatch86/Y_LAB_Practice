package org.example.monitoringservice.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * This class represents the properties of the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppProps {

    /**
     * The URL in the configuration file.
     */
    private String url;
    /**
     * The user name used in the configuration file.
     */
    private String userName;
    /**
     * The password used in the configuration file.
     */
    private String password;

    /**
     * The path to Liquibase configuration.
     */
    private String liquibasePath;
}
