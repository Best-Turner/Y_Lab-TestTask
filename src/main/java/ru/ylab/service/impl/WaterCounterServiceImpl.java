package ru.ylab.service.impl;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.MeterData;
import ru.ylab.model.WaterMeter;
import ru.ylab.repository.WaterCounterRepository;
import ru.ylab.service.MeterDataService;
import ru.ylab.service.WaterCounterService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class WaterCounterServiceImpl implements WaterCounterService {

    private final WaterCounterRepository repository;

    private final MeterDataService meterDataService;

    public WaterCounterServiceImpl(WaterCounterRepository repository, MeterDataService counterDataStorageService) {
        this.repository = repository;
        this.meterDataService = counterDataStorageService;
    }


    @Override
    public void save(WaterMeter waterCounter) {
        if (!repository.isExist(waterCounter.getSerialNumber())) {
            repository.addWaterCounter(waterCounter);
        }
        meterDataService.registrationCounter(waterCounter.getId(), waterCounter.getCurrentValue());
    }

    @Override
    public WaterMeter getWaterCounter(String serialNumber) throws WaterCounterNotFoundException {
        Optional<WaterMeter> waterCounter = repository.getWaterCounter(serialNumber);
        return waterCounter.orElseThrow(() ->
                new WaterCounterNotFoundException("WaterCounter with this serial number = " + serialNumber + " not found"));

    }

    @Override
    public List<WaterMeter> allWaterCounter() {
        List<WaterMeter> allWaterCounters = repository.getAllWaterCounters();
        if (allWaterCounters.isEmpty()) {
            return Collections.emptyList();
        }
        return allWaterCounters;
    }

    @Override
    public void updateCurrentValue(String serialNumber, Float newValue) throws InvalidDataException, WaterCounterNotFoundException {
        long idBySerialNumber = getIdBySerialNumber(serialNumber);
        repository.updateCurrentValue(idBySerialNumber, newValue);
        meterDataService.submitValue(idBySerialNumber, newValue);
    }


    @Override
    public Float currentValue(String serialNumber) throws WaterCounterNotFoundException {
        return getWaterCounter(serialNumber).getCurrentValue();
    }


    @Override
    public Float getValueByDate(String serialNumber, String date) throws WaterCounterNotFoundException {
        return meterDataService.getValueByDate(getIdBySerialNumber(serialNumber), date);
    }

    @Override
    public List<MeterData> getValues(long waterMeterId) {
        return meterDataService.getValues(waterMeterId);
    }

    @Override
    public WaterMeter getWaterCounter(long inputCommand) {
        return repository.getWaterCounter(inputCommand);
    }

    private long getIdBySerialNumber(String serialNumber) throws WaterCounterNotFoundException {
        WaterMeter waterMeterNotFound = repository.getWaterCounter(serialNumber)
                .orElseThrow(() -> new WaterCounterNotFoundException("Water meter not found"));
        return waterMeterNotFound.getId();
    }
}
