package org.example.monitoringservice.configuration;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DbConfigurer {
    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    @Value("${spring.datasource.driver-class-name}")
    private String dbDriverName;
    @Value("${spring.liquibase.change-log}")
    private String liquibaseChangelogPath;
    @Value("${spring.liquibase.default-schema}")
    private String liquibaseDefaultSchema;
    @Value("${spring.liquibase.service-schema}")
    private String liquibaseServiceSchema;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName(dbDriverName);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public String preLiquibaseBean() {
        try(Connection connection = dataSource().getConnection();
            Statement stmt = connection.createStatement()) {

            stmt.execute("CREATE SCHEMA IF NOT EXISTS data_changelog_schema;");
            stmt.execute("CREATE SCHEMA IF NOT EXISTS monitoring_service_schema;");
            stmt.execute("CREATE SEQUENCE IF NOT EXISTS jdbc_sequence START 1");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(liquibaseChangelogPath);
        liquibase.setLiquibaseSchema(liquibaseServiceSchema);
        liquibase.setDefaultSchema(liquibaseDefaultSchema);
        liquibase.setDataSource(dataSource());
        return liquibase;
    }
}
