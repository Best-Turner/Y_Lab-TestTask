package ru.ylab.service;

import ru.ylab.model.WaterCounter;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WaterCounterService {

    void save(WaterCounter waterCounter);

    WaterCounter getWaterCounter(String serialNumber);

    Set<WaterCounter> allWaterCounter();

    void changeCurrentValue(String serialNumber, Float newValue);
    boolean delete(String serialNumber);

    Float currentValue(String serialNumber);
    void transferData(String serialNumber, Float value);

    Map<String, Float> values(String serialNumber);
    Float getValueByDate(String serialNumber, String date);
}
