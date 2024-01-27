package ru.ylab.service.impl;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.repository.CounterDataStorageRepository;
import ru.ylab.service.CounterDataStorageService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class CounterDataStorageServiceImpl implements CounterDataStorageService {
    private final CounterDataStorageRepository repository;

    public CounterDataStorageServiceImpl(CounterDataStorageRepository repository) {
        this.repository = repository;
    }


    @Override
    public void registrationCounter(String serialNumber) {
        repository.registrationWaterCounter(serialNumber);
    }

    @Override
    public boolean submitValue(String serialNumber, Float value) {
        if (!isRegistrInStorage(serialNumber)) {
            return false;
        }
        Float currentValue = getCurrentValue(serialNumber);
        if (currentValue == null) {
            currentValue = 0f;
        }
        if (value < currentValue) {
            try {
                throw new InvalidDataException("Invalid data. Transmitted value cannot be less than the current one");
            } catch (InvalidDataException e) {
                System.out.println("Check your details and try again");
                return false;
            }
        }
        currentValue = value;
        repository.addValue(serialNumber, getKeyFromDate(now()), currentValue);
        return true;
    }

    @Override
    public Float getCurrentValue(String serialNumber) {
        Float value;
        if (!isRegistrInStorage(serialNumber)) {
            return null;
        }
        value = repository.getValue(serialNumber, getKeyFromDate(now()));
        if (value == -1f) {
            value = findLastValue(serialNumber);
        }
        return value;
    }


    @Override
    public Float getValueByDate(String serialNumber, String date) {  // date - "yyyy - M"
        if (!isRegistrInStorage(serialNumber)) {
            return null;
        }
        return repository.getValue(serialNumber, date);  // return -1f else value empty
    }

    @Override
    public Map<String, Float> getValues(String serialNumber) {
        if (!isRegistrInStorage(serialNumber)) {
            return null;
        }
        return repository.getValues(serialNumber);
    }

    @Override
    public boolean isRegistrInStorage(String serialNumber) {
        return repository.isExist(serialNumber);
    }

    @Override
    public void delete(String serialNumber) {
        repository.delete(serialNumber);
    }

    private String getKeyFromDate(LocalDate date) {
        StringBuilder builder = new StringBuilder();
        return builder.append(date.getYear())
                .append(" - ")
                .append(date.getMonthValue()).toString();

    }

    private Float findLastValue(String serialNumber) {
        Map<String, Float> values = repository.getValues(serialNumber);
        String keyFromLastDate = getKeyFromLastDate(values.keySet());
        if (keyFromLastDate == null) {
            return -1f;
        }
        return values.get(keyFromLastDate);

    }

    private String getKeyFromLastDate(Set<String> dates) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy - M");
        LocalDate lastDate = null;

        for (String dateStr : dates) {
            LocalDate currentDate = LocalDate.parse(dateStr, formatter);

            if (lastDate == null || currentDate.isAfter(lastDate)) {
                lastDate = currentDate;
            }
        }

        if (lastDate != null) {
            return getKeyFromDate(lastDate);
        }
        return null;
    }

    private LocalDate now() {
        return LocalDate.now();
    }

}
