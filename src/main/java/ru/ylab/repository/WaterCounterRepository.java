package ru.ylab.repository;

import ru.ylab.model.CounterType;
import ru.ylab.model.WaterMeter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The `WaterCounterRepository` class represents a repository for managing water counter data and associated operations.
 * It provides methods for retrieving, adding, updating, and deleting water counter information, as well as checking
 * for water counter existence and retrieving the list of all water counters.
 */

public class WaterCounterRepository {

    private final Connection connection;
    private PreparedStatement preparedStatement;
    private String sql;

    /**
     * Constructs a new `WaterCounterRepository` with the given map of water counters.
     *
     * @param connection
     */
    public WaterCounterRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Retrieves a water counter with the specified serial number.
     *
     * @param serialNumber The serial number of the water counter to retrieve.
     * @return An Optional containing the water counter with the given serial number, or empty if not found.
     */
    public Optional<WaterMeter> getWaterCounter(String serialNumber) {
        sql = "SELECT * FROM model.water_meters WHERE serial_number = ?";
        WaterMeter waterMeterFromDb = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, serialNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                waterMeterFromDb = waterMeterFromIncomingDatabaseData(resultSet);
            }
            return Optional.ofNullable(waterMeterFromDb);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a new water counter to the repository.
     *
     * @param waterCounter The water counter to be added.
     */
    public void addWaterCounter(WaterMeter waterCounter) {
        sql = "INSERT INTO model.water_meters(serial_number, type, current_value, owner) VALUES(?,?,?,?)";
        try {
            connection.setAutoCommit(true);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, waterCounter.getSerialNumber());
            preparedStatement.setObject(2, waterCounter.getType(), Types.OTHER);
            preparedStatement.setFloat(3, waterCounter.getCurrentValue());
            preparedStatement.setLong(4, waterCounter.getOwner().getId());
            int i = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Checks if a water counter with the specified serial number exists in the repository.
     *
     * @param serialNumber The serial number to check for existence.
     * @return True if the water counter exists, false otherwise.
     */
    public boolean isExist(String serialNumber) {
        boolean executeResult = false;
        sql = "SELECT EXISTS(SELECT 1 FROM model.water_meters w WHERE w.serial_number = ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, serialNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                executeResult = resultSet.getBoolean(1);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return executeResult;
    }

    /**
     * Retrieves the entire list of water counters in the repository.
     *
     * @return The map of water counters in the repository.
     */
    public List<WaterMeter> getAllWaterCounters() {
        List<WaterMeter> waterMeters = new ArrayList<>();
        sql = "SELECT * FROM model.water_meters";
        try {
            preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                waterMeters.add(waterMeterFromIncomingDatabaseData(resultSet));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return waterMeters;
    }

    public WaterMeter getWaterCounter(long waterCounterId) {
        sql = "SELECT * FROM model.water_meters wm WHERE wm.id = ?";
        WaterMeter waterMeterFromDb = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, waterCounterId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                waterMeterFromDb = waterMeterFromIncomingDatabaseData(resultSet);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return waterMeterFromDb;
    }

    private WaterMeter waterMeterFromIncomingDatabaseData(ResultSet resultSet) {
        try {
            long id = resultSet.getLong("id");
            String serialNumber = resultSet.getString("serial_number");
            CounterType type = CounterType.valueOf(resultSet.getString("type"));
            float value = resultSet.getFloat("current_value");

            WaterMeter waterMeter = new WaterMeter(serialNumber, type, value, null);
            waterMeter.setId(id);
            return waterMeter;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
