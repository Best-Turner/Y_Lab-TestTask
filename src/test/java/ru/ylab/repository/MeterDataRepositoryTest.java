package ru.ylab.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.ylab.model.MeterData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Testcontainers
public class MeterDataRepositoryTest {
    private final static long ID = 1L;
    private final static Float VALUE = 123f;
    private final static String DATE = "2024-01";
    private static final String DB_NAME = "testDB";
    private static final String USERNAME = "testUser";
    private static final String PASSWORD_DB = "testPass";
    private MeterDataRepository repository;
    private Connection connection;
    private final MeterData meterData = new MeterData(ID, DATE, VALUE);

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
                            postgresqlContainer.getUsername(),
                            postgresqlContainer.getPassword());
            connection.setAutoCommit(true);
            repository = new MeterDataRepository(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String createSchemaSql = "CREATE SCHEMA IF NOT EXISTS model;";
        String createTableSql = "CREATE TABLE IF NOT EXISTS model.meter_data" +
                "(id SERIAL PRIMARY KEY," +
                "water_id BIGINT," +
                "date VARCHAR(50)," +
                "value NUMERIC);";
        try (PreparedStatement creteSchemaStatement = connection.prepareStatement(createSchemaSql);
             PreparedStatement createTableStatement = connection.prepareStatement(createTableSql)) {
            creteSchemaStatement.executeUpdate();
            createTableStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @After
    public void tearDown() throws SQLException {
        String dropSchemaSql = "DROP SCHEMA model;";
        String dropTableSql = "DROP TABLE model.meter_data;";
        try (PreparedStatement dropTableStatement = connection.prepareStatement(dropTableSql);
             PreparedStatement dropSchemaStatement = connection.prepareStatement(dropSchemaSql)) {
            dropTableStatement.executeUpdate();
            dropSchemaStatement.executeUpdate();
        } finally {
            connection.close();
        }
    }

    @Test
    public void whenMeterDataExistsThenReturnTrue() {
        repository.addValue(meterData);
        assertTrue(repository.isExist(ID));
    }

    @Test
    public void whenMeterDataNotExistsThenReturnFalse() {
        assertFalse(repository.isExist(ID));
    }

    @Test
    public void methodShouldBeSaveElement() {
        assertFalse(repository.isExist(ID));
        repository.addValue(meterData);
        assertTrue(repository.isExist(ID));
    }


    @Test
    public void methodDeleteExistMeterDataThenReturnTrue() {
        repository.addValue(meterData);
        assertTrue(repository.isExist(ID));
        boolean expected = repository.delete(ID);
        assertTrue(expected);
    }

    @Test
    public void methodDeleteNotExistMeterDataThenReturnFalse() {
        assertFalse(repository.isExist(ID));
        boolean expected = repository.delete(ID);
        assertFalse(expected);
    }


//        Float actual = repository.getValue(ID, DATE);
//        assertNull(actual);
//        repository.addValue(new MeterData(ID, DATE, VALUE));
//        actual = repository.getValue(ID, DATE);
//        assertEquals(VALUE, actual);
//
//    @Test
//    public void methodShouldBeReturnAllValuesBySerialNumber() {
//        MeterData data1 = new MeterData(ID, "2024-01", 123f);
//        MeterData data2 = new MeterData(ID, "2024-02", 555f);
//        MeterData data3 = new MeterData(ID, "2024-05", 888f);
//        MeterData data4 = new MeterData(ID, "2024-12", 12f);
//        List<MeterData> waterMeterList = List.of(data1, data2, data3, data4);
//        waterMeterList.forEach(repository::addValue);
//        List<MeterData> dataFromDb = repository.getValuesByWaterMeterId(ID);
//        boolean actual = waterMeterList.containsAll(dataFromDb);
//        assertTrue(actual);
//    }

}