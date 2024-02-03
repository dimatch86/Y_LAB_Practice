package org.example.monitoringservice.repository;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
    protected DbReadingRepository readingRepository;
    protected DbUserRepository userRepository;

    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName(DB_NAME)
            .withUsername(DB_USER)
            .withPassword(DB_PASSWORD);

    @BeforeAll
    static void init() {
        postgreSQLContainer.start();
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        String username = postgreSQLContainer.getUsername();
        String password = postgreSQLContainer.getPassword();
        createTestData(jdbcUrl, username, password);
    }


    @BeforeEach
    public void setUp() {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        String username = postgreSQLContainer.getUsername();
        String password = postgreSQLContainer.getPassword();
        userRepository = new DbUserRepository(jdbcUrl, username, password);
        readingRepository = new DbReadingRepository(jdbcUrl, username, password);
    }



    private static void createTestData(String jdbcUrl, String username, String password) {
        try(Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE SEQUENCE jdbc_sequence START 101");
            Database db = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/changelog/changelog.xml",
                    new ClassLoaderResourceAccessor(), db);
            liquibase.update();

        } catch (SQLException | LiquibaseException ex) {
            ex.printStackTrace();
        }
    }
}
