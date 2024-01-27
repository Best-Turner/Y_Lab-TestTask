package ru.ylab.repository;

import ru.ylab.model.CounterDataStorage;

import java.util.HashMap;
import java.util.Map;

public class CounterDataStorageRepository {

    private final CounterDataStorage dataStorage;

    public CounterDataStorageRepository(CounterDataStorage counterDataStorage) {
        this.dataStorage = counterDataStorage;
    }

    public void registrationWaterCounter(String serialNumber) {
        Map<String, Float> monthlyValues = new HashMap<>();
        dataStorage.getDataStorage().put(serialNumber, monthlyValues);
    }


    public void addValue(String serialNumber, String date, Float value) {
        dataStorage.getDataStorage().get(serialNumber).put(date, value);
    }

    public boolean isExist(String serialNumber) {
        return dataStorage.getDataStorage().containsKey(serialNumber);
    }

    public Float getValue(String serialNumber, String dateKey) {
        return dataStorage.getDataStorage().get(serialNumber).getOrDefault(dateKey, -1f);
    }

    public Map<String, Float> getValues(String serialNumber) {
        return dataStorage.getDataStorage().get(serialNumber);
    }

    public void delete(String serialNumber) {
        dataStorage.getDataStorage().remove(serialNumber);
    }
}
