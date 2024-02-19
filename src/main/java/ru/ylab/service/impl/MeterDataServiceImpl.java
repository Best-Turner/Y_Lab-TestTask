package ru.ylab.service.impl;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.model.MeterData;
import ru.ylab.model.WaterMeter;
import ru.ylab.repository.MeterDataRepository;
import ru.ylab.repository.WaterCounterRepository;
import ru.ylab.service.MeterDataService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The `MeterDataServiceImpl` class implements the `MeterDataService` interface
 * and provides services for managing water counter data storage operations. It utilizes a
 * `WaterMeterRepository` for data storage and offers methods for registering counters,
 * submitting values, retrieving current and historical values, checking existence, and deleting counters.
 */
public class MeterDataServiceImpl implements MeterDataService {
    private final MeterDataRepository repository;
    private final WaterCounterRepository waterCounterRepository;

    /**
     * Constructs a new `MeterDataServiceImpl` with the specified `WaterMeterRepository`.
     *
     * @param repository             The repository to be used by the service implementation.
     * @param waterCounterRepository
     */
    public MeterDataServiceImpl(MeterDataRepository repository, WaterCounterRepository waterCounterRepository) {
        this.repository = repository;
        this.waterCounterRepository = waterCounterRepository;
    }

    /**
     * Gets the current date.
     *
     * @return The current date as a LocalDate object.
     */
    private static LocalDate now() {
        return LocalDate.now();
    }

    /**
     * Registers a water counter with the given serial number.
     *
     * @param serialNumber The serial number of the water counter to register.
     */
    @Override
    public void registrationCounter(String serialNumber) {
        Optional<WaterMeter> waterCounter = waterCounterRepository.getWaterCounter(serialNumber);
        WaterMeter waterMeter = waterCounter.get();
        repository.addValue(new MeterData(waterMeter.getId(), getKeyFromDate(now()), waterMeter.getCurrentValue()));
    }

    /**
     * Submits a value for the specified water counter, updating the current value.
     *
     * @param waterMeterId The id of the water counter.
     * @param value        The value to be submitted.
     * @return True if the submission was successful, false otherwise.
     * @throws InvalidDataException If the submitted value is less than the current value.
     */
    @Override
    public boolean submitValue(long waterMeterId, Float value) throws InvalidDataException {
        if (!canChangeDate(waterMeterId)) {
            return false;
        }
        Float currentValue = getCurrentValue(waterMeterId);
        if (currentValue == null) {
            currentValue = 0f;
        }
        if (value < currentValue) {
            throw new InvalidDataException("Invalid data. Transmitted value cannot be less than the current one");
        }

        currentValue = value;
        repository.addValue(new MeterData(waterMeterId, getKeyFromDate(now()), currentValue));
        return true;
    }

    /**
     * Retrieves the current value associated with a water counter.
     *
     * @param waterMeterId The id of the water counter.
     * @return The current value of the water counter.
     */
    @Override
    public Float getCurrentValue(long waterMeterId) {
        Float value;
        if (!isRegistrInStorage(waterMeterId)) {
            return null;
        }
        value = repository.getValue(waterMeterId, getKeyFromDate(now()));
        if (value == -1) {
            value = findLastValue(waterMeterId);
        }
        return value;
    }

    /**
     * Retrieves the value associated with a water counter for a specific date.
     *
     * @param waterMeterId The id of the water counter.
     * @param date         The date for which the value is requested ("yyyy-M").
     * @return The value associated with the specified serial number and date.
     */
    @Override
    public Float getValueByDate(long waterMeterId, String date) {  // date - "yyyy - M"
        if (!isRegistrInStorage(waterMeterId)) {
            return null;
        }
        return repository.getValue(waterMeterId, date);
    }

    /**
     * Retrieves all values associated with a water counter.
     *
     * @param waterMeterId The id of the water counter.
     * @return A map of all values associated with the specified water counter.
     */
    @Override
    public List<MeterData> getValues(long waterMeterId) {
        if (!isRegistrInStorage(waterMeterId)) {
            return Collections.emptyList();
        }
        return repository.getValuesByWaterMeterId(waterMeterId);
    }

    /**
     * Checks if a water counter with the specified serial number exists in the data storage.
     *
     * @param waterMeterId The id to check for existence.
     * @return True if the water counter exists, false otherwise.
     */
    @Override
    public boolean isRegistrInStorage(long waterMeterId) {
        return repository.isExist(waterMeterId);
    }

    /**
     * Deletes a water counter and its associated data from the data storage.
     *
     * @param waterMeterId The id of the water counter to delete.
     */
    @Override
    public void delete(long waterMeterId) {
        repository.delete(waterMeterId);
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
     * @param waterMeterId The id of the water counter.
     * @return The last recorded value.
     */
    private Float findLastValue(long waterMeterId) {
        List<MeterData> data = repository.getValuesByWaterMeterId(waterMeterId);
        LocalDate lastDateDataTransfer = getLastDateTransfer(data);
        String keyFromDate = getKeyFromDate(lastDateDataTransfer);
        if (keyFromDate == null) {
            return -1f;
        }
        return data.stream().filter(el -> el.getDate().equals(keyFromDate)).findFirst().get().getValue();
    }

    /**
     * Gets the key from the last recorded date in the set of dates.
     *
     * @param dates The set of date strings ("yyyy-M").
     * @return The key from the last recorded date.
     */

    private LocalDate getLastDateTransfer(List<MeterData> dates) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M");
        LocalDate lastDate = null;

        for (MeterData data : dates) {
            LocalDate currentDate = YearMonth.parse(data.getDate(), formatter).atDay(1);

            if (lastDate == null || currentDate.isAfter(lastDate)) {
                lastDate = currentDate;
            }
        }
        return lastDate;
    }

    private boolean canChangeDate(long waterMeterId) {

        List<MeterData> data = repository.getValuesByWaterMeterId(waterMeterId);
        LocalDate lastDateTransfer = getLastDateTransfer(data);
        if (lastDateTransfer == null) {
            return true;
        }
        LocalDate now = now();
        return lastDateTransfer.plusMonths(1).minusDays(1).isBefore(now);

    }

}
