package ru.ylab.repository;

import ru.ylab.model.WaterCounter;

import java.util.Map;
import java.util.Optional;

/**
 * The `WaterCounterRepository` class represents a repository for managing water counter data and associated operations.
 * It provides methods for retrieving, adding, updating, and deleting water counter information, as well as checking
 * for water counter existence and retrieving the list of all water counters.
 */

public class WaterCounterRepository {

    private final Map<String, WaterCounter> waterCounters;

    /**
     * Constructs a new `WaterCounterRepository` with the given map of water counters.
     *
     * @param waterCounter The initial map of water counters to be used in the repository.
     */
    public WaterCounterRepository(Map<String, WaterCounter> waterCounter) {
        this.waterCounters = waterCounter;
    }

    /**
     * Retrieves a water counter with the specified serial number.
     *
     * @param serialNumber The serial number of the water counter to retrieve.
     * @return An Optional containing the water counter with the given serial number, or empty if not found.
     */
    public Optional<WaterCounter> getWaterCounter(String serialNumber) {
        return Optional.ofNullable(waterCounters.get(serialNumber));
    }

    /**
     * Adds a new water counter to the repository.
     *
     * @param waterCounter The water counter to be added.
     */
    public void addWaterCounter(WaterCounter waterCounter) {
        waterCounters.put(waterCounter.getSerialNumber(), waterCounter);
    }

    /**
     * Updates the information of an existing water counter with the specified serial number.
     *
     * @param serialNumber The serial number of the water counter to be updated.
     * @param waterCounter The updated water counter information.
     */
    public void update(String serialNumber, WaterCounter waterCounter) {
        waterCounters.replace(serialNumber, waterCounter);
    }

    /**
     * Deletes a water counter from the repository.
     *
     * @param waterCounter The water counter to be deleted.
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean delete(WaterCounter waterCounter) {
        waterCounters.remove(waterCounter.getSerialNumber());
        return true;
    }

    /**
     * Deletes a water counter from the repository using its serial number.
     *
     * @param serialNumber The serial number of the water counter to be deleted.
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean delete(String serialNumber) {
        waterCounters.remove(serialNumber);
        return true;
    }

    /**
     * Checks if a water counter with the specified serial number exists in the repository.
     *
     * @param serialNumber The serial number to check for existence.
     * @return True if the water counter exists, false otherwise.
     */
    public boolean isExist(String serialNumber) {
        return waterCounters.containsKey(serialNumber);
    }

    /**
     * Retrieves the entire list of water counters in the repository.
     *
     * @return The map of water counters in the repository.
     */
    public Map<String, WaterCounter> getAllWaterCounters() {
        return waterCounters;
    }
}
