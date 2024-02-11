package ru.ylab.service;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.model.MeterData;
import ru.ylab.model.WaterMeter;

import java.util.List;

public interface MeterDataService {

    void registrationCounter(WaterMeter waterCounter);

    boolean submitValue(String serialNumber, Float value) throws InvalidDataException;

    Float getCurrentValue(String serialNumber);

    Float getValueByDate(String serialNumber, String date);

    List<MeterData> getValues(String serialNumber);

    boolean isRegistrInStorage(String serialNumber);

    void delete(String serialNumber);

}
