package ru.ylab.util;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.MeterData;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;

import java.util.List;

public interface WaterCounterValidator {
    boolean createCounter(String serialNumber, String value, User owner);

    Float getCurrentValue(String serialNumber);

    List<MeterData> getHistoryValues(long waterMeterId);

    void changeCurrentValue(String serialNumber, String value) throws InvalidDataException, WaterCounterNotFoundException;

    List<WaterMeter> getWaterCounters();

    WaterMeter getWaterMeterById(String waterMeterId) throws InvalidDataException;

    WaterMeter getWaterMeterBySerialNumber(String serialNumber) throws InvalidDataException, WaterCounterNotFoundException;



}
