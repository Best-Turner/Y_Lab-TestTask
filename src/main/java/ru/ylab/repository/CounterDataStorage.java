package ru.ylab.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CounterDataStorage {

    private static CounterDataStorage counterDataStorage;
    private final Map<String, List<Float>> dataStorage = new HashMap<>();

    private List<Float> values;

    private CounterDataStorage() {
    }

    public static CounterDataStorage getInstance() {
        if (counterDataStorage == null) {
            counterDataStorage = new CounterDataStorage();
        }
        return counterDataStorage;
    }

    public boolean registrationWaterCounter(String serialNumber) {
        values = new ArrayList<>();
        dataStorage.put(serialNumber, values);
        return true;
    }


    public void addValue(String serialNumber, Float value) {
        dataStorage.get(serialNumber).add(value);
    }

    public boolean isExist(String serialNumber) {
        return dataStorage.containsKey(serialNumber);
    }

    public Float getCurrentValue(String serialNumber) {
        List<Float> values = dataStorage.get(serialNumber);
        return values.get(values.size() - 1);
    }

    public List<Float> getValues(String serialNumber) {
        return dataStorage.get(serialNumber);
    }
}
