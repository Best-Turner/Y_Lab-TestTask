package ru.ylab.service;

import java.util.Map;

public interface CounterDataStorageService {

    void registrationCounter(String serialNumber);

    boolean submitValue(String serialNumber, Float value);
    Float getCurrentValue(String serialNumber);

    Float getValueByDate(String serialNumber, String date);

    Map<String, Float> getValues(String serialNumber);

    boolean isExist(String serialNumber);

}
