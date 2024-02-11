package ru.ylab.repository;

import org.junit.Before;
import org.junit.Test;
import ru.ylab.model.MeterData;

import java.util.List;

import static org.junit.Assert.*;

public class MeterDataRepositoryTest {
    private final static String SERIAL_NUMBER = "H-1234";
    private final static Float VALUE = 123f;
    private final static String DATE = "2024-01";
    private MeterDataRepository repository;

    @Before
    public void setUp() throws Exception {
        repository = new MeterDataRepository();
    }

    @Test
    public void methodShouldBeSaveNewSerialNumber() {
        boolean actual = repository.isExist(SERIAL_NUMBER);
        assertFalse(actual);
        repository.registrationWaterMeter(SERIAL_NUMBER);
        actual = repository.isExist(SERIAL_NUMBER);
        assertTrue(actual);
    }


    @Test
    public void methodShouldBeSaveMeterData() {
        repository.registrationWaterMeter(SERIAL_NUMBER);
        Float actual = repository.getValue(SERIAL_NUMBER, DATE);
        assertNull(actual);
        repository.addValue(new MeterData(SERIAL_NUMBER, DATE, VALUE));
        actual = repository.getValue(SERIAL_NUMBER, DATE);
        assertEquals(VALUE, actual);
    }

    @Test
    public void methodShouldBeReturnAllValuesBySerialNumber() {
        repository.registrationWaterMeter(SERIAL_NUMBER);
        MeterData data1 = new MeterData(SERIAL_NUMBER, "2024-01", 123f);
        MeterData data2 = new MeterData(SERIAL_NUMBER, "2024-02", 555f);
        MeterData data3 = new MeterData(SERIAL_NUMBER, "2024-05", 888f);
        MeterData data4 = new MeterData(SERIAL_NUMBER, "2024-12", 12f);
        List<MeterData> waterMeterList = List.of(data1, data2, data3, data4);
        waterMeterList.forEach(repository::addValue);
        List<MeterData> dataFromDb = repository.getValues(SERIAL_NUMBER);
        boolean actual = waterMeterList.containsAll(dataFromDb);
        assertTrue(actual);
    }

}