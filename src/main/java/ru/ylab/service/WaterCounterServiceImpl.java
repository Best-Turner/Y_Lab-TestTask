package ru.ylab.service;

import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.WaterCounter;
import ru.ylab.repository.WaterCounterRepository;

import java.util.List;
import java.util.Optional;

public class WaterCounterServiceImpl implements WaterCounterService {

    private final WaterCounterRepository repository;

    public WaterCounterServiceImpl(WaterCounterRepository repository) {
        this.repository = repository;
    }


    @Override
    public void add(WaterCounter waterCounter) {
        if (!repository.isExist(waterCounter.getSerialNumber())) {
            repository.addWaterCounter(waterCounter);
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
    public List<WaterCounter> allWaterCounter() {
        return repository.getAllWaterCounters();
    }

    @Override
    public boolean updateWaterCounter(String serialNumber, WaterCounter waterCounter) {
        return false;
    }

    @Override
    public boolean delete(String serialNumber) {
        if (!repository.isExist(serialNumber)) {
            return false;
        }
        repository.delete(serialNumber);
        return false;
    }

    public boolean delete(WaterCounter waterCounter) {
        return delete(waterCounter.getSerialNumber());
    }
}
