package ru.ylab.service;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.MeterData;

import java.util.List;

public interface MeterDataService {

    void registrationCounter(long waterMeterId, float value);

    boolean submitValue(long waterMeterId, Float value) throws InvalidDataException, WaterCounterNotFoundException;

    Float getCurrentValue(long waterMeterId);

    Float getValueByDate(long waterMeterId, String date);

    List<MeterData> getValues(long waterMeterId);

    boolean isRegistrInStorage(long waterMeterId);

    void delete(long waterMeterId);

}
