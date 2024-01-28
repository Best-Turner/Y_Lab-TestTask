package ru.ylab.service.impl;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.repository.CounterDataStorageRepository;
import ru.ylab.service.CounterDataStorageService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

/**
 * The `CounterDataStorageServiceImpl` class implements the `CounterDataStorageService` interface
 * and provides services for managing water counter data storage operations. It utilizes a
 * `CounterDataStorageRepository` for data storage and offers methods for registering counters,
 * submitting values, retrieving current and historical values, checking existence, and deleting counters.
 */
public class CounterDataStorageServiceImpl implements CounterDataStorageService {
    private final CounterDataStorageRepository repository;

    /**
     * Constructs a new `CounterDataStorageServiceImpl` with the specified `CounterDataStorageRepository`.
     *
     * @param repository The repository to be used by the service implementation.
     */
    public CounterDataStorageServiceImpl(CounterDataStorageRepository repository) {
        this.repository = repository;
    }

    /**
     * Registers a water counter with the given serial number.
     *
     * @param serialNumber The serial number of the water counter to register.
     */
    @Override
    public void registrationCounter(String serialNumber) {
        repository.registrationWaterCounter(serialNumber);
    }

    /**
     * Submits a value for the specified water counter, updating the current value.
     *
     * @param serialNumber The serial number of the water counter.
     * @param value        The value to be submitted.
     * @return True if the submission was successful, false otherwise.
     * @throws InvalidDataException If the submitted value is less than the current value.
     */
    @Override
    public boolean submitValue(String serialNumber, Float value) throws InvalidDataException {
        if (!isRegistrInStorage(serialNumber)) {
            return false;
        }
        Float currentValue = getCurrentValue(serialNumber);
        if (currentValue == null) {
            currentValue = 0f;
        }
        if (value < currentValue) {
            throw new InvalidDataException("Invalid data. Transmitted value cannot be less than the current one");
        }
        currentValue = value;
        repository.addValue(serialNumber, getKeyFromDate(now()), currentValue);
        return true;
    }

    /**
     * Retrieves the current value associated with a water counter.
     *
     * @param serialNumber The serial number of the water counter.
     * @return The current value of the water counter.
     */
    @Override
    public Float getCurrentValue(String serialNumber) {
        Float value;
        if (!isRegistrInStorage(serialNumber)) {
            return null;
        }
        value = repository.getValue(serialNumber, getKeyFromDate(now()));
        if (value == null) {
            value = findLastValue(serialNumber);
        }
        return value;
    }

    /**
     * Retrieves the value associated with a water counter for a specific date.
     *
     * @param serialNumber The serial number of the water counter.
     * @param date         The date for which the value is requested ("yyyy-M").
     * @return The value associated with the specified serial number and date.
     */
    @Override
    public Float getValueByDate(String serialNumber, String date) {  // date - "yyyy - M"
        if (!isRegistrInStorage(serialNumber)) {
            return null;
        }
        return repository.getValue(serialNumber, date);
    }

    /**
     * Retrieves all values associated with a water counter.
     *
     * @param serialNumber The serial number of the water counter.
     * @return A map of all values associated with the specified water counter.
     */
    @Override
    public Map<String, Float> getValues(String serialNumber) {
        if (!isRegistrInStorage(serialNumber)) {
            return null;
        }
        return repository.getValues(serialNumber);
    }

    /**
     * Checks if a water counter with the specified serial number exists in the data storage.
     *
     * @param serialNumber The serial number to check for existence.
     * @return True if the water counter exists, false otherwise.
     */
    @Override
    public boolean isRegistrInStorage(String serialNumber) {
        return repository.isExist(serialNumber);
    }

    /**
     * Deletes a water counter and its associated data from the data storage.
     *
     * @param serialNumber The serial number of the water counter to delete.
     */
    @Override
    public void delete(String serialNumber) {
        repository.delete(serialNumber);
    }

    /**
     * Converts a LocalDate object to a formatted date key ("yyyy-M").
     *
     * @param date The LocalDate object to convert.
     * @return The formatted date key.
     */
    private String getKeyFromDate(LocalDate date) {
        StringBuilder builder = new StringBuilder();
        return builder.append(date.getYear())
                .append("-")
                .append(date.getMonthValue()).toString();

    }

    /**
     * Finds the last recorded value associated with a water counter.
     *
     * @param serialNumber The serial number of the water counter.
     * @return The last recorded value.
     */
    private Float findLastValue(String serialNumber) {
        Map<String, Float> values = repository.getValues(serialNumber);
        String keyFromLastDate = getKeyFromLastDate(values.keySet());
        if (keyFromLastDate == null) {
            return -1f;
        }
        return values.get(keyFromLastDate);

    }

    /**
     * Gets the key from the last recorded date in the set of dates.
     *
     * @param dates The set of date strings ("yyyy-M").
     * @return The key from the last recorded date.
     */

    private String getKeyFromLastDate(Set<String> dates) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M");
        LocalDate lastDate = null;

        for (String dateStr : dates) {
            LocalDate currentDate = YearMonth.parse(dateStr, formatter).atDay(1);

            if (lastDate == null || currentDate.isAfter(lastDate)) {
                lastDate = currentDate;
            }
        }

        if (lastDate != null) {
            return getKeyFromDate(lastDate);
        }
        return null;
    }

    /**
     * Gets the current date.
     *
     * @return The current date as a LocalDate object.
     */
    private LocalDate now() {
        return LocalDate.now();
    }

}
