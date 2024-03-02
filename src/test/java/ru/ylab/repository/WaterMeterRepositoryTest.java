package ru.ylab.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.ylab.model.CounterType;
import ru.ylab.model.Role;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;

@Testcontainers
public class WaterMeterRepositoryTest {

    private final static Long ID = 1l;
    private final static String SERIAL_NUMBER = "H-1234";
    private final static Float VALUE = 123f;
    private final static CounterType TYPE = CounterType.HOT;

    private static final String DB_NAME = "testDB";
    private static final String USERNAME = "testUser";
    private static final String PASSWORD_DB = "testPass";
    private Connection connection;
    private WaterMeter waterCounter;
    private User owner;
    private WaterCounterRepository repository;

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
            connection = DriverManager
                    .getConnection(postgresqlContainer.getJdbcUrl(),
                            USERNAME,
                            PASSWORD_DB);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String createSchema = "CREATE SCHEMA IF NOT EXISTS model;";
        String createType = "CREATE TYPE type as ENUM('HOT', 'COLD', 'HEATING');";
        String createTableSQL = "CREATE TABLE IF NOT EXISTS model.water_meters" +
                "(id SERIAL PRIMARY KEY," +
                "serial_number VARCHAR(100)," +
                "type type," +
                "current_value numeric," +
                "owner bigint);";

        try (PreparedStatement createSchemaStatement = connection.prepareStatement(createSchema);
             PreparedStatement createTypeStatement = connection.prepareStatement(createType);
             PreparedStatement createTableStatement = connection.prepareStatement(createTableSQL)) {
            createSchemaStatement.executeUpdate();
            createTypeStatement.executeUpdate();
            createTableStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        owner = new User("user", "user@mai.ru", "1234", Role.USER);
        owner.setId(ID);
        waterCounter = new WaterMeter(SERIAL_NUMBER, TYPE, VALUE, owner);
        waterCounter.setId(ID);
        repository = new WaterCounterRepository(connection);
    }

    @After
    public void tearDown() throws SQLException {
        String dropTableSQL = "DROP table model.water_meters";
        String dropTypeSql = "DROP TYPE type";
        try (PreparedStatement dropTableStatement = connection.prepareStatement(dropTableSQL);
             PreparedStatement dropTypeStatement = connection.prepareStatement(dropTypeSql)) {
            dropTableStatement.execute();
            dropTypeStatement.execute();
        } finally {
            connection.close();
        }
    }

    @Test
    public void whenSaveUniqueWaterCounterThenSavingOccurs() {
        assertEquals(Collections.emptyList(), repository.getAllWaterCounters());
        repository.addWaterCounter(waterCounter);
        assertEquals(Collections.singletonList(waterCounter), repository.getAllWaterCounters());
    }

    @Test
    public void whenExistsWaterMeterThenReturnTrue() {
        repository.addWaterCounter(waterCounter);
        boolean expected = repository.isExist(SERIAL_NUMBER);
        assertTrue(expected);
    }

    @Test
    public void whenNotExistsWaterMeterThenReturnFalse() {
        boolean expected = repository.isExist(SERIAL_NUMBER);
        assertFalse(expected);
    }

    @Test
    public void whenChangeCurrentValueThenValueMustByChanged() {
        repository.addWaterCounter(waterCounter);
        Optional<WaterMeter> fromDb = repository.getWaterCounter(SERIAL_NUMBER);
        assertEquals(VALUE, fromDb.get().getCurrentValue());
        repository.updateCurrentValue(ID, 777f);
        float expected = repository.getWaterCounter(SERIAL_NUMBER).get().getCurrentValue();
        assertEquals(777, expected, 0.1);
    }

    @Test
    public void whenGetExistsWaterMeterBySerialNumberThenReturnWaterMeter() {
        repository.addWaterCounter(waterCounter);
        WaterMeter expected = repository.getWaterCounter(SERIAL_NUMBER).get();
        assertEquals(waterCounter, expected);
    }

    @Test
    public void whenGetNotExistsWaterMeterBySerialNumberThenEmptyOptional() {
        Optional<WaterMeter> expected = repository.getWaterCounter(SERIAL_NUMBER);
        assertEquals(Optional.empty(), expected);
    }

    @Test
    public void whenGetExistsWaterMeterByIdThenReturnWaterMeter() {
        repository.addWaterCounter(waterCounter);
        WaterMeter expected = repository.getWaterCounter(ID);
        assertEquals(waterCounter, expected);
    }

    @Test
    public void whenGetNutExistsWaterMeterByIdThenReturnNull() {
        WaterMeter expected = repository.getWaterCounter(ID);
        assertNull(expected);
    }


}