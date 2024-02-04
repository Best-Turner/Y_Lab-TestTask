package ru.ylab.service.impl;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.WaterCounter;
import ru.ylab.repository.WaterCounterRepository;
import ru.ylab.service.CounterDataStorageService;
import ru.ylab.service.WaterCounterService;

import java.util.*;

public class WaterCounterServiceImpl implements WaterCounterService {

    private final WaterCounterRepository repository;

    private final CounterDataStorageService storageService;

    public WaterCounterServiceImpl(WaterCounterRepository repository, CounterDataStorageService counterDataStorageService) {
        this.repository = repository;
        this.storageService = counterDataStorageService;
    }


    @Override
    public void save(WaterCounter waterCounter) {
        if (!repository.isExist(waterCounter.getSerialNumber())) {
            repository.addWaterCounter(waterCounter);
            storageService.registrationCounter(waterCounter);
        }
    }

    @Override
    public WaterCounter getWaterCounter(String serialNumber) throws WaterCounterNotFoundException {

        Optional<WaterCounter> waterCounter = repository.getWaterCounter(serialNumber);
        if (waterCounter.isEmpty()) {
            throw new WaterCounterNotFoundException("WaterCounter with this serial number = " + serialNumber + " not found");
        }
        return waterCounter.get();

    }

    @Override
    public Set<WaterCounter> allWaterCounter() {
        Set<WaterCounter> waterCounters = new HashSet<>();
        Map<String, WaterCounter> allWaterCounters = repository.getAllWaterCounters();
        if (!allWaterCounters.isEmpty()) {
            for (Map.Entry<String, WaterCounter> entry : allWaterCounters.entrySet()) {
                waterCounters.add(entry.getValue());
            }
            return waterCounters;
        }
        return Collections.emptySet();
    }

    @Override
    public void transferData(String serialNumber, Float newValue) throws InvalidDataException {
        storageService.submitValue(serialNumber, newValue);
    }

    @Override
    public boolean delete(String serialNumber) {
        if (!repository.isExist(serialNumber)) {
            return false;
        }
        storageService.delete(serialNumber);
        repository.delete(serialNumber);
        return true;
    }

    @Override
    public Float currentValue(String serialNumber) {
        return storageService.getCurrentValue(serialNumber);
    }


    @Override
    public Float getValueByDate(String serialNumber, String date) {
        return storageService.getValueByDate(serialNumber, date);
    }

    @Override
    public Map<String, Float> getValues(String serialNumber) {
        return storageService.getValues(serialNumber);
    }

    public boolean delete(WaterCounter waterCounter) {
        return delete(waterCounter.getSerialNumber());
    }
}
