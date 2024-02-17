package ru.ylab.repository;

import ru.ylab.model.MeterData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The `CounterDataStorageRepository` class serves as a repository for managing counter data storage operations.
 * It delegates operations to the `CounterDataStorage` class and provides methods for registering water counters,
 * adding values, checking existence, retrieving values, and deleting counters.
 */

public class MeterDataRepository {
    private final Map<Long, Map<String, Float>> storage;
    private Map<String, Float> values;

    /**
     * Constructs a new 'MeterDataRepository'
     */
    public MeterDataRepository() {
        storage = new HashMap<>();
    }

    /**
     * Registers a water counter with the given serial number.
     *
     * @param waterMeterId The id of the water meter to register.
     */
    public void registrationWaterMeter(long waterMeterId) {
        values = new HashMap<>();
        storage.put(waterMeterId, values);
    }

    /**
     * Adds a new MeterData for a specific date to the storage associated with a water counter.
     *
     * @param meterData The data about water meter.
     */
    public void addValue(MeterData meterData) {
        long waterMeterId = meterData.getWaterMeterId();
        String date = meterData.getDate();
        float value = meterData.getValue();
        storage.get(waterMeterId).put(date, value);
    }

    /**
     * Checks if a water counter with the specified serial number exists in the data storage.
     *
     * @param waterMeterId The id to check for existence.
     * @return True if the water counter exists, false otherwise.
     */
    public boolean isExist(long waterMeterId) {
        return storage.containsKey(waterMeterId);
    }

    /**
     * Retrieves the value associated with a water counter for a specific date.
     *
     * @param waterMeterId The id of the water counter.
     * @param dateKey      The date for which the value is requested.
     * @return The value associated with the specified serial number and date.
     */
    public Float getValue(long waterMeterId, String dateKey) {
        return storage.get(waterMeterId).get(dateKey);
    }

    /**
     * Retrieves all values associated with a water counter.
     *
     * @param waterMeterId The id of the water counter.
     * @return A map of all values associated with the specified water counter.
     */
    public List<MeterData> getValues(long waterMeterId) {
        List<MeterData> meterData = new ArrayList<>();

        for (Map.Entry<String, Float> data : storage.get(waterMeterId).entrySet()) {
            meterData.add(new MeterData(waterMeterId, data.getKey(), data.getValue()));
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
        return storage.remove(waterMeterId).isEmpty();
    }
}
