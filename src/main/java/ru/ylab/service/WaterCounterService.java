package ru.ylab.service;

import ru.ylab.model.User;
import ru.ylab.model.WaterCounter;

import java.util.List;

public interface WaterCounterService {

    void add(WaterCounter waterCounter);

    WaterCounter getWaterCounter(String serialNumber);

    List<WaterCounter> allWaterCounter();

    boolean updateWaterCounter(String serialNumber, WaterCounter waterCounter);

    boolean delete(String serialNumber);
}
