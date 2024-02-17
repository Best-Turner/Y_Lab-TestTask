package ru.ylab.service.impl;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.MeterData;
import ru.ylab.model.WaterMeter;
import ru.ylab.repository.WaterCounterRepository;
import ru.ylab.service.MeterDataService;
import ru.ylab.service.WaterCounterService;

import java.util.*;

public class WaterCounterServiceImpl implements WaterCounterService {

    private final WaterCounterRepository repository;

    private final MeterDataService storageService;

    public WaterCounterServiceImpl(WaterCounterRepository repository, MeterDataService counterDataStorageService) {
        this.repository = repository;
        this.storageService = counterDataStorageService;
    }


    @Override
    public void save(WaterMeter waterCounter) {
        if (!repository.isExist(waterCounter.getSerialNumber())) {
            repository.addWaterCounter(waterCounter);
            storageService.registrationCounter(waterCounter);
        }
    }

    @Override
    public WaterMeter getWaterCounter(String serialNumber) throws WaterCounterNotFoundException {

        Optional<WaterMeter> waterCounter = repository.getWaterCounter(serialNumber);
        if (waterCounter.isEmpty()) {
            throw new WaterCounterNotFoundException("WaterCounter with this serial number = " + serialNumber + " not found");
        }
        return waterCounter.get();

    }

    @Override
    public Set<WaterMeter> allWaterCounter() {
        Set<WaterMeter> waterCounters = new HashSet<>();
        Map<String, WaterMeter> allWaterCounters = repository.getAllWaterCounters();
        if (!allWaterCounters.isEmpty()) {
            for (Map.Entry<String, WaterMeter> entry : allWaterCounters.entrySet()) {
                waterCounters.add(entry.getValue());
            }
            return waterCounters;
        }
        return Collections.emptySet();
    }

    @Override
    public void transferData(String serialNumber, Float newValue) throws InvalidDataException, WaterCounterNotFoundException {
        storageService.submitValue(getIdBySerialNumber(serialNumber), newValue);
    }

    @Override
    public boolean delete(String serialNumber) {
        if (!repository.isExist(serialNumber)) {
            return false;
        }
        Long waterMeterId = repository.getWaterCounter(serialNumber).get().getId();
        storageService.delete(waterMeterId);
        repository.delete(serialNumber);
        return true;
    }

    @Override
    public Float currentValue(String serialNumber) throws WaterCounterNotFoundException {
        WaterMeter waterCounter = getWaterCounter(serialNumber);
        return storageService.getCurrentValue(waterCounter.getId());
    }


    @Override
    public Float getValueByDate(String serialNumber, String date) throws WaterCounterNotFoundException {
        return storageService.getValueByDate(getIdBySerialNumber(serialNumber), date);
    }

    @Override
    public List<MeterData> getValues(String serialNumber) throws WaterCounterNotFoundException {
        return storageService.getValues(getIdBySerialNumber(serialNumber));
    }

    public boolean delete(WaterMeter waterCounter) {
        return delete(waterCounter.getSerialNumber());
    }


    private long getIdBySerialNumber(String serialNumber) throws WaterCounterNotFoundException {
        WaterMeter waterMeterNotFound = repository.getWaterCounter(serialNumber)
                .orElseThrow(() -> new WaterCounterNotFoundException("Water meter not found"));
        return waterMeterNotFound.getId();
    }
}
