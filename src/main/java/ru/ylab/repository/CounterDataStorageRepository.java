package ru.ylab.repository;

import ru.ylab.model.CounterDataStorage;

import java.util.Map;

/**
 * The `CounterDataStorageRepository` class serves as a repository for managing counter data storage operations.
 * It delegates operations to the `CounterDataStorage` class and provides methods for registering water counters,
 * adding values, checking existence, retrieving values, and deleting counters.
 */

public class CounterDataStorageRepository {

    private final CounterDataStorage dataStorage;

    /**
     * Constructs a new `CounterDataStorageRepository` with the specified `CounterDataStorage`.
     *
     * @param counterDataStorage The `CounterDataStorage` instance to be used by the repository.
     */
    public CounterDataStorageRepository(CounterDataStorage counterDataStorage) {
        this.dataStorage = counterDataStorage;
    }

    /**
     * Registers a water counter with the given serial number.
     *
     * @param serialNumber The serial number of the water counter to register.
     */
    public void registrationWaterCounter(String serialNumber) {
        dataStorage.registerWaterCounter(serialNumber);
    }

    /**
     * Adds a value for a specific date to the data storage associated with a water counter.
     *
     * @param serialNumber The serial number of the water counter.
     * @param date         The date for which the value is added.
     * @param value        The value to be added.
     */
    public void addValue(String serialNumber, String date, Float value) {
        dataStorage.addValue(serialNumber, date, value);
    }

    /**
     * Checks if a water counter with the specified serial number exists in the data storage.
     *
     * @param serialNumber The serial number to check for existence.
     * @return True if the water counter exists, false otherwise.
     */
    public boolean isExist(String serialNumber) {
        return dataStorage.isRegistry(serialNumber);
    }

    /**
     * Retrieves the value associated with a water counter for a specific date.
     *
     * @param serialNumber The serial number of the water counter.
     * @param dateKey      The date for which the value is requested.
     * @return The value associated with the specified serial number and date.
     */
    public Float getValue(String serialNumber, String dateKey) {
        return dataStorage.getValueByDate(serialNumber, dateKey);
    }

    /**
     * Retrieves all values associated with a water counter.
     *
     * @param serialNumber The serial number of the water counter.
     * @return A map of all values associated with the specified water counter.
     */
    public Map<String, Float> getValues(String serialNumber) {
        return dataStorage.getValuesWithDate(serialNumber);
    }

    /**
     * Deletes a water counter along with its associated data from the data storage.
     *
     * @param serialNumber The serial number of the water counter to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean delete(String serialNumber) {
        return dataStorage.deleteWaterCounterAndData(serialNumber);
    }
}
