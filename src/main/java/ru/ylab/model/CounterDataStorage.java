package ru.ylab.model;

import java.util.HashMap;
import java.util.Map;

public class CounterDataStorage {

    private static CounterDataStorage counterDataStorage;
    private static Map<String, Map<String, Float>> dataStorage;

    private CounterDataStorage() {}

    public static CounterDataStorage getInstance() {
        if (counterDataStorage == null) {
            counterDataStorage = new CounterDataStorage();
            dataStorage = new HashMap<>();
        }
        return counterDataStorage;
    }

    public Map<String, Map<String, Float>> getDataStorage() {
        return dataStorage;
    }
}
