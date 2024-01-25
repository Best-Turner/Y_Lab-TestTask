package ru.ylab.service;

import java.util.Map;

public interface CounterDataStorageService {

    void registrationCounter(String serialNumber);

    void submitValue(String serialNumber, Float value);
    Float getCurrentValue(String serialNumber);

    Float getValueForDate(String serialNumber, String date);

    Map<String, Float> getValues(String serialNumber);

    boolean isExist(String serialNumber);

}
