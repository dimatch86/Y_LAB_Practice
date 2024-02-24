package org.example.monitoringservice.in.controller;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.monitoringservice.repository.ReadingRepositoryImpl;
import org.example.monitoringservice.repository.ReadingTypeRepositoryImpl;
import org.example.monitoringservice.repository.UserRepositoryImpl;
import org.example.monitoringservice.service.AuthenticationService;
import org.example.monitoringservice.service.ReadingServiceImpl;
import org.example.monitoringservice.service.ReadingTypeServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Transactional
public abstract class AbstractTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected AuthenticationService authenticationService;
    @Autowired
    protected ReadingServiceImpl readingService;
    @Autowired
    protected ReadingTypeServiceImpl readingTypeService;
    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    protected ReadingRepositoryImpl readingRepository;
    @Autowired
    protected UserRepositoryImpl userRepository;
    @Autowired
    protected ReadingTypeRepositoryImpl readingTypeRepository;
    @Autowired
    protected MockMvc mockMvc;

    protected static final PostgreSQLContainer<?> postgreSQLContainer;
    static {
        DockerImageName postgres = DockerImageName.parse("postgres:12.3");
        postgreSQLContainer = (PostgreSQLContainer<?>) new PostgreSQLContainer<>(postgres)
                .withReuse(true);
        postgreSQLContainer.start();
    }


    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.url", () -> jdbcUrl);
    }

    @BeforeAll
    static void init() {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        String username = postgreSQLContainer.getUsername();
        String password = postgreSQLContainer.getPassword();
        createTestData(jdbcUrl, username, password);
    }

    private static void createTestData(String jdbcUrl, String username, String password) {
        try(Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE SEQUENCE jdbc_sequence START 101");
            stmt.execute("CREATE SCHEMA IF NOT EXISTS data_changelog_schema;");
            stmt.execute("CREATE SCHEMA IF NOT EXISTS monitoring_service_schema;");
            Database db = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            db.setDefaultSchemaName("monitoring_service_schema");
            db.setLiquibaseSchemaName("data_changelog_schema");
            Liquibase liquibase = new Liquibase("db/changelog/changelog-master.xml",
                    new ClassLoaderResourceAccessor(), db);
            liquibase.update();

        } catch (SQLException | LiquibaseException ex) {
            ex.printStackTrace();
        }
    }
}
