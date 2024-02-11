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
    private final Map<String, Map<String, Float>> storage;
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
     * @param serialNumber The serial number of the water counter to register.
     */
    public void registrationWaterMeter(String serialNumber) {
        values = new HashMap<>();
        storage.put(serialNumber, values);
    }

    /**
     * Adds a new MeterData for a specific date to the storage associated with a water counter.
     *
     * @param meterData The data about water meter.
     */
    public void addValue(MeterData meterData) {
        String serialNumberMeterWater = meterData.getSerialNumberMeterWater();
        String date = meterData.getDate();
        float value = meterData.getValue();
        storage.get(serialNumberMeterWater).put(date, value);
    }

    /**
     * Checks if a water counter with the specified serial number exists in the data storage.
     *
     * @param serialNumber The serial number to check for existence.
     * @return True if the water counter exists, false otherwise.
     */
    public boolean isExist(String serialNumber) {
        return storage.containsKey(serialNumber);
    }

    /**
     * Retrieves the value associated with a water counter for a specific date.
     *
     * @param serialNumber The serial number of the water counter.
     * @param dateKey      The date for which the value is requested.
     * @return The value associated with the specified serial number and date.
     */
    public Float getValue(String serialNumber, String dateKey) {
        return storage.get(serialNumber).get(dateKey);
    }

    /**
     * Retrieves all values associated with a water counter.
     *
     * @param serialNumber The serial number of the water counter.
     * @return A map of all values associated with the specified water counter.
     */
    public List<MeterData> getValues(String serialNumber) {
        List<MeterData> meterData = new ArrayList<>();
        ;
        for (Map.Entry<String, Float> data : storage.get(serialNumber).entrySet()) {
            meterData.add(new MeterData(serialNumber, data.getKey(), data.getValue()));
        }
        return meterData;
    }

    /**
     * Deletes a water counter along with its associated data from the data storage.
     *
     * @param serialNumber The serial number of the water counter to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean delete(String serialNumber) {
        return storage.remove(serialNumber).isEmpty();
    }
}
