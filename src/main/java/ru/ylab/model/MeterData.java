package ru.ylab.model;

import java.util.Objects;

/**
 * The `CounterDataStorage` class is a singleton class responsible for managing and storing water counter data.
 * It provides methods to register water counters, retrieve values by date, check registry status, add new values,
 * and delete water counters along with their associated data.
 */

public class MeterData {

    private String serialNumberMeterWater;
    private String date;
    private float value;

    /**
     * @param serialNumberMeterWater
     * @param date
     * @param value
     */
    public MeterData(String serialNumberMeterWater, String date, float value) {
        this.serialNumberMeterWater = serialNumberMeterWater;
        this.date = date;
        this.value = value;
    }

    public void setWaterMeter(String serialNumberMeterWater) {
        this.serialNumberMeterWater = serialNumberMeterWater;
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

    public String getSerialNumberMeterWater() {
        return serialNumberMeterWater;
    }

    public void setSerialNumberMeterWater(String serialNumberMeterWater) {
        this.serialNumberMeterWater = serialNumberMeterWater;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeterData data = (MeterData) o;
        return Float.compare(value, data.value) == 0 && Objects.equals(serialNumberMeterWater, data.serialNumberMeterWater) && Objects.equals(date, data.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumberMeterWater, date, value);
    }

    @Override
    public String toString() {
        return "MeterData{" +
                "serialNumberMeterWater='" + serialNumberMeterWater + '\'' +
                ", date='" + date + '\'' +
                ", value=" + value +
                '}';
    }
}
