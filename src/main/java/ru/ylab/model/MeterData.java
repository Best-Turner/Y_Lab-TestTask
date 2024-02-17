package ru.ylab.model;

import java.util.Objects;

/**
 * The `CounterDataStorage` class is a singleton class responsible for managing and storing water counter data.
 * It provides methods to register water counters, retrieve values by date, check registry status, add new values,
 * and delete water counters along with their associated data.
 */

public class MeterData {

    private Long id;

    private long waterMeterId;

    private String date;
    private float value;

    /**
     * @param waterMeterId
     * @param date
     * @param value
     */
    public MeterData(long waterMeterId, String date, float value) {
        this.waterMeterId = waterMeterId;
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }


    public long getWaterMeterId() {
        return waterMeterId;
    }

    public void setWaterMeterId(long waterMeterId) {
        this.waterMeterId = waterMeterId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeterData data = (MeterData) o;
        return waterMeterId == data.waterMeterId && Float.compare(value, data.value) == 0 && Objects.equals(id, data.id) && Objects.equals(date, data.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, waterMeterId, date, value);
    }
}
