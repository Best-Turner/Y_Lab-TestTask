package ru.ylab.util.impl;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.CounterType;
import ru.ylab.model.MeterData;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;
import ru.ylab.service.WaterCounterService;
import ru.ylab.util.WaterCounterValidator;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WaterCounterValidatorImpl implements WaterCounterValidator {
    private final static String PATTERN_SERIAL_NUMBER = "(H-[0-9]{4})|(C-[0-9]{4})|(O-[0-9]{4})";

    private final static String FORMAT_SERIAL_NUMBER = "Format - \"H-XXXX\"\n" +
            "Where G is hot;\n" +
            "C - cold;\n" +
            "O - heating;\n" +
            "XXXX - serial number";
    private final static String MESSAGE_WRONG_SERIAL_NUMBER = "Invalid serial number format.\n" + PATTERN_SERIAL_NUMBER;
    private static Long count = 1L;
    private final WaterCounterService service;

    public WaterCounterValidatorImpl(WaterCounterService service) {
        this.service = service;
    }

    @Override
    public boolean createCounter(String serialNumber, Float value, User owner) {
        if (!validateSerialNumber(serialNumber)) {
            return false;
        }
        CounterType counterType = determineCounterType(serialNumber);
        if (value == null) {
            value = 0f;
        }
        WaterMeter newWaterCounter = new WaterMeter(++count, serialNumber, counterType, value, owner);
        service.save(newWaterCounter);
        owner.getWaterCounterList().add(newWaterCounter);
        return true;
    }

    @Override
    public Float getCurrentValue(String serialNumber) {
        float value = 0;
        if (!validateSerialNumber(serialNumber)) {
            return value;
        }
        try {
            value = service.currentValue(serialNumber);

        } catch (WaterCounterNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return value;
    }

    @Override
    public List<MeterData> getHistoryValues(long waterMeterId) {
        List<MeterData> meterData = Collections.emptyList();
        try {
            meterData = service.getValues(waterMeterId);
        } catch (WaterCounterNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return meterData;
    }

    @Override
    public boolean transferData(String serialNumber, Float value) {
        boolean result = false;
        if (!validateSerialNumber(serialNumber)) {
            return result;
        }
        try {
            service.transferData(serialNumber, value);
            result = true;
        } catch (InvalidDataException | WaterCounterNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    public List<WaterMeter> getWaterCounters() {
        return service.allWaterCounter();
    }

    @Override
    public WaterMeter getOneWaterMeter(long inputCommand) {
        return service.getWaterCounter(inputCommand);

    }

    private boolean validateSerialNumber(String serialNumber) {
        Matcher matcher = Pattern.compile(PATTERN_SERIAL_NUMBER).matcher(serialNumber);
        if (matcher.matches()) {
            return true;
        }
        try {
            throw new InvalidDataException(MESSAGE_WRONG_SERIAL_NUMBER);
        } catch (InvalidDataException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private CounterType determineCounterType(String serialNumber) {
        String hot = "H";
        String cold = "C";
        CounterType counterType;
        if (serialNumber.startsWith(hot)) {
            counterType = CounterType.HOT;
        } else if (serialNumber.startsWith(cold)) {
            counterType = CounterType.COLD;
        } else {
            counterType = CounterType.HEATING;
        }
        return counterType;
    }
}
