package ru.ylab.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.ylab.model.Role;
import ru.ylab.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Testcontainers
public class UserRepositoryTest {
    private final static String NAME = "admin";
    private final static String EMAIL = "admin@mail.ru";
    private final static Role ADMIN = Role.ADMIN;
    private final static String PASSWORD = "1234";
    private final static Long USER_ID = 1L;
    private static final String DB_NAME = "testDB";
    private static final String USERNAME = "testUser";
    private static final String PASSWORD_DB = "testPass";

    private Connection connection;
    private User user;
    private UserRepository repository;
    @Container
    public static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:latest"))
            .withDatabaseName(DB_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD_DB)
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 2));


    @Before
    public void setUp() {
        postgresqlContainer.start();
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            repository = new UserRepository(connection);
            String createSchema = "CREATE SCHEMA IF NOT EXISTS model";
            String createRole = "CREATE TYPE role as ENUM('USER','ADMIN');";
            String createTableSQL = "CREATE TABLE IF NOT EXISTS model.users" +
                    "(id SERIAL PRIMARY KEY," +
                    "name VARCHAR(100)," +
                    "email VARCHAR(100)," +
                    " password VARCHAR(255)," +
                    " role role);";
            try (PreparedStatement createSchemaStatement = connection.prepareStatement(createSchema);
                 PreparedStatement createRoleStatement = connection.prepareStatement(createRole);
                 PreparedStatement createTable = connection.prepareStatement(createTableSQL)) {
                createSchemaStatement.executeUpdate();
                createRoleStatement.executeUpdate();
                createTable.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        user = new User(NAME, EMAIL, PASSWORD, ADMIN);
        user.setId(USER_ID);
    }

    @After
    public void tearDown() throws SQLException {
        String dropTableSQL = "DROP table model.users";
        String dropTypeSql = "DROP TYPE role";
        try (PreparedStatement dropTableStatement = connection.prepareStatement(dropTableSQL);
             PreparedStatement dropTypeStatement = connection.prepareStatement(dropTypeSql)) {
            dropTableStatement.execute();
            dropTypeStatement.execute();
        } finally {
            connection.close();
        }
    }

    @Test
    public void saveUserTest() {
        List<User> userList = repository.usersGroup();
        assertEquals(Collections.emptyList(), userList);
        repository.save(user);
        assertEquals(Collections.singletonList(user), repository.usersGroup());
    }

    @Test
    public void returnTrueIfUserExists() {
        repository.save(user);
        boolean expected = repository.isExist(user.getEmail());
        assertTrue(expected);
    }

    @Test
    public void returnFalseIfUserNotExists() {
        boolean expected = repository.isExist(user.getEmail());
        assertFalse(expected);
    }

    @Test
    public void whenGetExistsUserByIdThenReturnUser() {
        assertFalse(repository.isExist(user.getEmail()));
        repository.save(user);
        User expected = repository.getUser(USER_ID);
        assertEquals(user, expected);
    }

    @Test
    public void whenGetExistsUserByEmailThenReturnUser() {
        assertFalse(repository.isExist(user.getEmail()));
        repository.save(user);
        Optional<User> expected = repository.getUser(user.getEmail());
        assertEquals(user, expected.get());
    }

    @Test
    public void whenDeleteExistsUserThenReturnTrue() {
        repository.save(user);
        boolean expected = repository.delete(user);
        assertTrue(expected);
    }
    @Test
    public void whenDeleteNotExistsUserThenReturnFalse() {
        boolean expected = repository.delete(user);
        assertFalse(expected);
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(postgresqlContainer.getJdbcUrl(), USERNAME, PASSWORD_DB);
    }
}
