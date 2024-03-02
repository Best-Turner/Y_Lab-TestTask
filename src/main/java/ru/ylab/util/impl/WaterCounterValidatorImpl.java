package ru.ylab.util.impl;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.CounterType;
import ru.ylab.model.MeterData;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;
import ru.ylab.service.WaterCounterService;
import ru.ylab.util.AuditLogger;
import ru.ylab.util.WaterCounterValidator;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WaterCounterValidatorImpl implements WaterCounterValidator {
    private final static String PATTERN_SERIAL_NUMBER = "(H-[0-9]{4})|(C-[0-9]{4})|(O-[0-9]{4})";

    private final static String FORMAT_SERIAL_NUMBER = "Формат - \"H-XXXX\"\n" +
            "H - горячая;\n" +
            "C - холодная;\n" +
            "O - отопление;\n" +
            "XXXX - серийный номер";
    private final static String MESSAGE_WRONG_SERIAL_NUMBER = "Не верный формат серийного номера.\n" + FORMAT_SERIAL_NUMBER;
    private static final String WRONG_COMMAND = "Неверная команда";
    private final WaterCounterService service;

    public WaterCounterValidatorImpl(WaterCounterService service) {
        this.service = service;
    }

    @Override
    public boolean createCounter(String serialNumber, String currentValue, User owner) {
        try {
            validateSerialNumber(serialNumber);
        } catch (InvalidDataException e) {
            System.out.println(e.getMessage());
            return false;
        }
        float newValue = validateInputValue(currentValue);

        CounterType counterType = determineCounterType(serialNumber);

        service.save(new WaterMeter(serialNumber, counterType, newValue, owner));
        return true;
    }

    private float validateInputValue(String currentValue) {
        float inputValue;
        try {
            inputValue = Float.parseFloat(currentValue);
        } catch (NumberFormatException e) {
            System.out.println("\nНеверный формат. Значение по умолчанию = 0\n");
            return 0;
        }
        return inputValue;
    }

    @Override
    public Float getCurrentValue(String serialNumber) {
        float currentValue = 0;
        try {
            currentValue = service.currentValue(serialNumber);

        } catch (WaterCounterNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return currentValue;
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
    public void changeCurrentValue(String serialNumber, String value) throws InvalidDataException, WaterCounterNotFoundException {
        float newValue;
        try {
            newValue = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Неверный формат. Ожидается число");
        }
        service.updateCurrentValue(serialNumber, newValue);
    }


    @Override
    public WaterMeter getWaterMeterById(String waterMeterId) throws InvalidDataException {
        long inputWaterMeterId;
        try {
            inputWaterMeterId = Long.parseLong(waterMeterId);
        } catch (NumberFormatException e) {
            AuditLogger.log(WRONG_COMMAND);
            throw new InvalidDataException("Введите числовое значение");
        }
        return service.getWaterCounter(inputWaterMeterId);

    }

    @Override
    public WaterMeter getWaterMeterBySerialNumber(String serialNumber) throws WaterCounterNotFoundException, InvalidDataException {
        validateSerialNumber(serialNumber);
        return service.getWaterCounter(serialNumber);
    }

    private void validateSerialNumber(String serialNumber) throws InvalidDataException {
        Matcher matcher = Pattern.compile(PATTERN_SERIAL_NUMBER).matcher(serialNumber);
        if (!matcher.matches()) {
            throw new InvalidDataException(MESSAGE_WRONG_SERIAL_NUMBER);
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
