package ru.ylab.model;

import java.util.Objects;

/**
 * The `WaterCounter` class represents a water meter used to measure water consumption.
 * It stores information such as the unique identifier, serial number, type of counter,
 * current value, and the owner of the water counter.
 */
public class WaterMeter {

    private Long id;
    private String serialNumber;
    private CounterType type;
    private Float currentValue;
    private User owner;

    /**
     * Default constructor for the `WaterCounter` class.
     */
    public WaterMeter() {
    }

    /**
     * Parameterized constructor for the `WaterCounter` class.
     *
     * @param id           The unique identifier for the water counter.
     * @param serialNumber The serial number associated with the water counter.
     * @param type         The type of counter (e.g., residential, commercial).
     * @param currentValue The current measured value of the water consumption.
     * @param owner        The user who owns or is assigned the water counter.
     */
    public WaterMeter(Long id, String serialNumber, CounterType type, Float currentValue, User owner) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.type = type;
        this.currentValue = currentValue;
        this.owner = owner;
    }

    /**
     * Retrieves the unique identifier of the water counter.
     *
     * @return The unique identifier of the water counter.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the water counter.
     *
     * @param id The unique identifier to be set for the water counter.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the serial number associated with the water counter.
     *
     * @return The serial number of the water counter.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number for the water counter.
     *
     * @param serialNumber The serial number to be set for the water counter.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Retrieves the type of counter (e.g., residential, commercial).
     *
     * @return The type of the water counter.
     */
    public CounterType getType() {
        return type;
    }

    /**
     * Sets the type of counter for the water counter.
     *
     * @param type The type of counter to be set for the water counter.
     */
    public void setType(CounterType type) {
        this.type = type;
    }

    /**
     * Retrieves the current measured value of water consumption.
     *
     * @return The current value of water consumption.
     */
    public Float getCurrentValue() {
        return currentValue;
    }

    /**
     * Sets the current measured value of water consumption for the water counter.
     *
     * @param currentValue The current value to be set for water consumption.
     */
    public void setCurrentValue(Float currentValue) {
        this.currentValue = currentValue;
    }

    /**
     * Retrieves the owner or user associated with the water counter.
     *
     * @return The owner or user of the water counter.
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Sets the owner or user for the water counter.
     *
     * @param owner The owner or user to be set for the water counter.
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Checks if two water counters are equal based on their serial numbers.
     *
     * @param o The object to compare with the current water counter.
     * @return True if the two water counters are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaterMeter that = (WaterMeter) o;
        return Objects.equals(serialNumber, that.serialNumber);
    }

    /**
     * Generates a hash code for the water counter based on its serial number.
     *
     * @return The hash code for the water counter.
     */
    @Override
    public int hashCode() {
        return Objects.hash(serialNumber);
    }
}
