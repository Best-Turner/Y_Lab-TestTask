package ru.ylab.util;

import ru.ylab.model.CounterType;
import ru.ylab.model.User;
import ru.ylab.model.WaterCounter;

import java.util.Set;

public interface WaterCounterValidator {
    boolean createCounter(String serialNumber, Float value, User owner);
    Float getCurrentValue(String serialNumber);
    boolean transferData(String serialNumber, Float value);
    boolean delete(String serialNumber);
    WaterCounter findWaterCounterBySerialNumber(String serialNumber);
    Set<WaterCounter> getWaterCounters();
}
