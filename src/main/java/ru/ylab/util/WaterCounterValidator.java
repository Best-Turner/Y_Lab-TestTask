package ru.ylab.util;

import ru.ylab.model.MeterData;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;

import java.util.List;
import java.util.Set;

public interface WaterCounterValidator {
    boolean createCounter(String serialNumber, Float value, User owner);

    Float getCurrentValue(String serialNumber);

    List<MeterData> getHistoryValues(long waterMeterId);

    boolean transferData(String serialNumber, Float value);


    List<WaterMeter> getWaterCounters();

    WaterMeter getOneWaterMeter(long inputCommand);
}
