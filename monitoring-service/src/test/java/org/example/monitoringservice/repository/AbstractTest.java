package org.example.monitoringservice.repository;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Testcontainers
abstract class AbstractTest {

    private static final String POSTGRES_IMAGE = "postgres:12.3";
    private static final String DB_NAME = "test";
    private static final String DB_USER = "test";
    private static final String DB_PASSWORD = "test";
    private static DriverManagerDataSource dataSource;
    private static JdbcTemplate jdbcTemplate;
    protected ReadingRepository readingRepository;
    protected ReadingTypeRepository readingTypeRepository;
    protected UserRepository userRepository;
    public static String jdbcUrl;
    public static String username;
    public static String password;

    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName(DB_NAME)
            .withUsername(DB_USER)
            .withPassword(DB_PASSWORD);

    @BeforeAll
    static void init() {
        postgreSQLContainer.start();
        jdbcUrl = postgreSQLContainer.getJdbcUrl();
        username = postgreSQLContainer.getUsername();
        password = postgreSQLContainer.getPassword();

        dataSource = new DriverManagerDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        jdbcTemplate = new JdbcTemplate(dataSource);

        createTestData(jdbcUrl, username, password);
    }


    @BeforeEach
    public void setUp() {
        userRepository = new UserRepositoryImpl(jdbcTemplate);
        readingRepository = new ReadingRepositoryImpl(jdbcTemplate);
        readingTypeRepository = new ReadingTypeRepositoryImpl(jdbcTemplate);
    }

    @AfterEach
    public void cleanDatabase() {
        UserContext.setCurrentUser(null);
        jdbcTemplate.update("DELETE FROM monitoring_service_schema.user");
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
