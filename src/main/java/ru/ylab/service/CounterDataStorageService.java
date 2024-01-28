package ru.ylab.service;

import ru.ylab.exception.InvalidDataException;

import java.util.Map;

public interface CounterDataStorageService {

    void registrationCounter(String serialNumber);

    boolean submitValue(String serialNumber, Float value) throws InvalidDataException;

    Float getCurrentValue(String serialNumber);

    Float getValueByDate(String serialNumber, String date);

    Map<String, Float> getValues(String serialNumber);

    boolean isRegistrInStorage(String serialNumber);

    void delete(String serialNumber);

}
