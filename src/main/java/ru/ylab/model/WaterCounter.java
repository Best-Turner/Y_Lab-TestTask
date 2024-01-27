package ru.ylab.model;

import java.util.Objects;

public class WaterCounter {

    private Long id;
    private String serialNumber;
    private CounterType type;
    private Float currentValue;
    private User owner;

    public WaterCounter() {
    }

    public WaterCounter(Long id, String serialNumber, CounterType type, Float currentValue, User owner) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.type = type;
        this.currentValue = currentValue;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public CounterType getType() {
        return type;
    }

    public void setType(CounterType type) {
        this.type = type;
    }

    public Float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Float currentValue) {
        this.currentValue = currentValue;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaterCounter that = (WaterCounter) o;
        return Objects.equals(serialNumber, that.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber);
    }
}
