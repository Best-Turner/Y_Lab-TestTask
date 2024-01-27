package ru.ylab.util.impl;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.model.CounterType;
import ru.ylab.model.User;
import ru.ylab.model.WaterCounter;
import ru.ylab.service.WaterCounterService;
import ru.ylab.util.WaterCounterValidator;

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

    private static Long count;
    private final static String MESSAGE_WRONG_SERIAL_NUMBER = "Invalid serial number format.\n" + PATTERN_SERIAL_NUMBER;
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
        service.save(new WaterCounter(++count, serialNumber, counterType, value, owner));
        return true;
    }

    @Override
    public Float getCurrentValue(String serialNumber) {
        if (!validateSerialNumber(serialNumber)) {
            return null;
        }
        return service.currentValue(serialNumber);
    }

    @Override
    public boolean transferData(String serialNumber, Float value) {
        if (!validateSerialNumber(serialNumber)) {
            return false;
        }
        service.transferData(serialNumber, value);
        return true;
    }

    @Override
    public boolean delete(String serialNumber) {
        if (validateSerialNumber(serialNumber)) {
            return false;
        }
        return service.delete(serialNumber);
    }

    @Override
    public WaterCounter findWaterCounterBySerialNumber(String serialNumber) {
        if (validateSerialNumber(serialNumber)) {
            return null;
        }
        return service.getWaterCounter(serialNumber);
    }

    @Override
    public Set<WaterCounter> getWaterCounters() {
        return service.allWaterCounter();
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
        CounterType counterType = null;
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
