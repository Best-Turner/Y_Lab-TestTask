package ru.ylab.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The `CounterDataStorage` class is a singleton class responsible for managing and storing water counter data.
 * It provides methods to register water counters, retrieve values by date, check registry status, add new values,
 * and delete water counters along with their associated data.
 */

public class CounterDataStorage {

    private static CounterDataStorage counterDataStorage;
    private static Map<String, Map<String, Float>> dataStorage;
    private Map<String, Float> values;


    /**
     * Private constructor to enforce singleton pattern.
     */
    private CounterDataStorage() {
    }

    /**
     * Retrieves the instance of the `CounterDataStorage` class following the singleton pattern.
     *
     * @return The instance of the `CounterDataStorage` class.
     */
    public static CounterDataStorage getInstance() {
        if (counterDataStorage == null) {
            counterDataStorage = new CounterDataStorage();
            dataStorage = new HashMap<>();
        }
        return counterDataStorage;
    }

    /**
     * Retrieves the entire data storage map containing serial numbers and their associated values.
     *
     * @return The data storage map.
     */
    public Map<String, Map<String, Float>> getDataStorage() {
        return dataStorage;
    }

    /**
     * Registers a new water counter with the specified serial number.
     *
     * @param serialNumber The serial number of the water counter to be registered.
     */
    public void registerWaterCounter(String serialNumber) {
        values = new HashMap<>();
        dataStorage.put(serialNumber, values);
    }

    /**
     * Retrieves the values associated with a water counter for a specific date.
     *
     * @param serialNumber The serial number of the water counter.
     * @return The map of values associated with the specified serial number.
     */
    public Map<String, Float> getValuesWithDate(String serialNumber) {
        return dataStorage.get(serialNumber);
    }

    /**
     * Retrieves the value associated with a water counter for a specific date.
     *
     * @param serialNumber The serial number of the water counter.
     * @param date         The date for which the value is requested.
     * @return The value associated with the specified serial number and date.
     */
    public Float getValueByDate(String serialNumber, String date) {
        Float value = dataStorage.get(serialNumber).get(date);
        return value;
    }

    /**
     * Checks if a water counter with the specified serial number is registered.
     *
     * @param serialNumber The serial number to check for registry status.
     * @return True if the water counter is registered, false otherwise.
     */
    public boolean isRegistry(String serialNumber) {
        return dataStorage.containsKey(serialNumber);
    }

    /**
     * Adds a new value for a specific date to the data storage associated with a water counter.
     *
     * @param serialNumber The serial number of the water counter.
     * @param date         The date for which the new value is added.
     * @param newValue     The new value to be added.
     */
    public void addValue(String serialNumber, String date, Float newValue) {
        dataStorage.get(serialNumber).put(date, newValue);
    }

    /**
     * Deletes a water counter along with its associated data from the data storage.
     *
     * @param serialNumber The serial number of the water counter to be deleted.
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean deleteWaterCounterAndData(String serialNumber) {
        return dataStorage.remove(serialNumber).isEmpty();
    }

}
