package ru.ylab.service;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.model.MeterData;
import ru.ylab.model.WaterMeter;

import java.util.List;

public interface MeterDataService {

    void registrationCounter(WaterMeter waterCounter);

    boolean submitValue(long waterMeterId, Float value) throws InvalidDataException;

    Float getCurrentValue(long waterMeterId);

    Float getValueByDate(long waterMeterId, String date);

    List<MeterData> getValues(long waterMeterId);

    boolean isRegistrInStorage(long waterMeterId);

    void delete(long waterMeterId);

}
