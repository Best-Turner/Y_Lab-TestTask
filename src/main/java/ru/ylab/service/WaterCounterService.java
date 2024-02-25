package ru.ylab.service;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.MeterData;
import ru.ylab.model.WaterMeter;

import java.util.List;

public interface WaterCounterService {

    void save(WaterMeter waterCounter);

    WaterMeter getWaterCounter(String serialNumber) throws WaterCounterNotFoundException;

    List<WaterMeter> allWaterCounter();

    void updateCurrentValue(String serialNumber, Float newValue) throws InvalidDataException, WaterCounterNotFoundException;

    Float currentValue(String serialNumber) throws WaterCounterNotFoundException;

    Float getValueByDate(String serialNumber, String date) throws WaterCounterNotFoundException;

    List<MeterData> getValues(long id) throws WaterCounterNotFoundException;

    WaterMeter getWaterCounter(long inputCommand);
}

