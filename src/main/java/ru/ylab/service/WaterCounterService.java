package ru.ylab.service;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.WaterCounter;

import java.util.Map;
import java.util.Set;

public interface WaterCounterService {

    void save(WaterCounter waterCounter);

    WaterCounter getWaterCounter(String serialNumber) throws WaterCounterNotFoundException;

    Set<WaterCounter> allWaterCounter();

    void transferData(String serialNumber, Float newValue) throws InvalidDataException;

    boolean delete(String serialNumber);

    Float currentValue(String serialNumber);

    Float getValueByDate(String serialNumber, String date);

    Map<String, Float> getValues(String serialNumber);
}

