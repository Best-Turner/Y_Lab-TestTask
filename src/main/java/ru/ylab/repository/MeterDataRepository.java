package ru.ylab.repository;

import ru.ylab.model.MeterData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The `CounterDataStorageRepository` class serves as a repository for managing counter data storage operations.
 * It delegates operations to the `CounterDataStorage` class and provides methods for registering water counters,
 * adding values, checking existence, retrieving values, and deleting counters.
 */

public class MeterDataRepository {

    private final Connection connection;
    private String sql;

    /**
     * Constructs a new 'MeterDataRepository'
     */
    public MeterDataRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a new MeterData for a specific date to the storage associated with a water counter.
     *
     * @param meterData The data about water meter.
     */
    public void addValue(MeterData meterData) {
        sql = "INSERT INTO model.meter_data(water_id, date, value) VALUES(?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            long waterMeterId = meterData.getWaterMeterId();
            String date = meterData.getDate();
            float value = meterData.getValue();
            preparedStatement.setLong(1, waterMeterId);
            preparedStatement.setString(2, date);
            preparedStatement.setFloat(3, value);
            preparedStatement.executeUpdate();
            connection.commit();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a water counter with the specified serial number exists in the data storage.
     *
     * @param waterMeterId The id to check for existence.
     * @return True if the water counter exists, false otherwise.
     */
    public boolean isExist(long waterMeterId) {
        boolean executeResult = false;
        sql = "SELECT EXISTS(SELECT 1 FROM model.meter_data md WHERE md.water_id = ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, waterMeterId);
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
     * Retrieves the value associated with a water counter for a specific date.
     *
     * @param waterMeterId The id of the water counter.
     * @param dateKey      The date for which the value is requested.
     * @return The value associated with the specified serial number and date.
     */
    public Float getValue(long waterMeterId, String dateKey) {
        float valueFromDb = -1;
        sql = "SELECT * FROM model.meter_data md WHERE md.water_id = ? and md.date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, waterMeterId);
            preparedStatement.setString(2, dateKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                valueFromDb = resultSet.getFloat("value");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return valueFromDb;
    }

    /**
     * Retrieves all values associated with a water counter.
     *
     * @param waterMeterId The id of the water counter.
     * @return A map of all values associated with the specified water counter.
     */
    public List<MeterData> getValuesByWaterMeterId(long waterMeterId) {
        List<MeterData> meterData = new ArrayList<>();
        sql = "SELECT * FROM model.meter_data md WHERE md.water_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, waterMeterId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getLong("water_id");
                String date = resultSet.getString("date");
                float value = resultSet.getFloat("value");
                meterData.add(new MeterData(id, date, value));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return meterData;
    }

    /**
     * Deletes a water counter along with its associated data from the data storage.
     *
     * @param waterMeterId The water metre id of the water counter to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean delete(long waterMeterId) {
        sql = "DELETE FROM model.meter_date md WHERE md.water_id = ?";
        int result;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, waterMeterId);
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result > 0;
    }
}
