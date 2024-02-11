package ru.ylab.service;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.MeterData;
import ru.ylab.model.WaterMeter;

import java.util.List;
import java.util.Set;

public interface WaterCounterService {

    void save(WaterMeter waterCounter);

    WaterMeter getWaterCounter(String serialNumber) throws WaterCounterNotFoundException;

    Set<WaterMeter> allWaterCounter();

    void transferData(String serialNumber, Float newValue) throws InvalidDataException;

    boolean delete(String serialNumber);

    Float currentValue(String serialNumber);

    Float getValueByDate(String serialNumber, String date);

    List<MeterData> getValues(String serialNumber);
}

