package ru.ylab.service.impl;

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
            storageService.registrationCounter(waterCounter.getSerialNumber());
        }
    }

    @Override
    public WaterCounter getWaterCounter(String serialNumber) {

        Optional<WaterCounter> waterCounter = repository.getWaterCounter(serialNumber);
        if (!waterCounter.isPresent()) {
            try {
                throw new WaterCounterNotFoundException("WaterCounter with this serial number = " + serialNumber + " not found");
            } catch (WaterCounterNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
        return waterCounter.get();

    }

    @Override
    public Set<WaterCounter> allWaterCounter() {
        return (Set<WaterCounter>) repository.getAllWaterCounters().values();
    }

    @Override
    public void changeCurrentValue(String serialNumber, Float newValue) {
        storageService.submitValue(serialNumber, newValue);
    }

    @Override
    public boolean delete(String serialNumber) {
        if (!repository.isExist(serialNumber)) {
            return false;
        }
        storageService.delete(serialNumber);
        repository.delete(serialNumber);
        return false;
    }

    @Override
    public Float currentValue(String serialNumber) {
        return storageService.getCurrentValue(serialNumber);
    }

    @Override
    public void transferData(String serialNumber, Float value) {
        storageService.submitValue(serialNumber, value);
    }

    @Override
    public Map<String, Float> values(String serialNumber) {
        return storageService.getValues(serialNumber);
    }

    @Override
    public Float getValueByDate(String serialNumber, String date) {
        return storageService.getValueByDate(serialNumber, date);
    }

    public boolean delete(WaterCounter waterCounter) {
        return delete(waterCounter.getSerialNumber());
    }
}
