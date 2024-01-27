package ru.ylab.repository;

import ru.ylab.model.WaterCounter;

import java.util.*;

public class WaterCounterRepository {

    private final Map<String, WaterCounter> waterCounters;

    public WaterCounterRepository(Map<String, WaterCounter> waterCounter) {
        this.waterCounters = waterCounter;
    }

    public Optional<WaterCounter> getWaterCounter(String serialNumber) {
        return Optional.ofNullable(waterCounters.get(serialNumber));
    }

    public void addWaterCounter(WaterCounter waterCounter) {
        waterCounters.put(waterCounter.getSerialNumber(), waterCounter);
    }

    public void update(String serialNumber, WaterCounter waterCounter) {
        waterCounters.replace(serialNumber, waterCounter);
    }

    public boolean delete(WaterCounter waterCounter) {
        waterCounters.remove(waterCounter.getSerialNumber());
        return true;
    }
    public boolean delete(String serialNumber) {
        waterCounters.remove(serialNumber);
        return true;
    }

    public boolean isExist(String serialNumber) {
        return waterCounters.containsKey(serialNumber);
    }

    public Map<String, WaterCounter> getAllWaterCounters() {
        return waterCounters;
    }
}
