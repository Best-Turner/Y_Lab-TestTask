package ru.ylab.repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CounterDataStorageRepository {

    private static CounterDataStorageRepository counterDataStorage;
    private Map<String, Map<String, Float>> dataStorage = new HashMap<>();
    private Map<String, Float> monthlyValues;


    private CounterDataStorageRepository() {
    }

    public static CounterDataStorageRepository getInstance() {
        if (counterDataStorage == null) {
            counterDataStorage = new CounterDataStorageRepository();
        }
        return counterDataStorage;
    }

    public void registrationWaterCounter(String serialNumber) {
        monthlyValues = new HashMap<>();
        dataStorage.put(serialNumber, monthlyValues);

    }


    public void addValue(String serialNumber, String date, Float value) {
        dataStorage.get(serialNumber).put(date, value);
    }

    public boolean isExist(String serialNumber) {
        return dataStorage.containsKey(serialNumber);
    }

    public Float getValue(String serialNumber, String dateKey) {
        return dataStorage.get(serialNumber).getOrDefault(dateKey, -1f);
    }

    public Map<String, Float> getValues(String serialNumber) {
        return dataStorage.get(serialNumber);
    }
}
