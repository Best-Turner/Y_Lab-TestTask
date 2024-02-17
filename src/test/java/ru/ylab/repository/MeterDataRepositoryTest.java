package ru.ylab.repository;

import org.junit.Before;
import org.junit.Test;
import ru.ylab.model.MeterData;

import java.util.List;

import static org.junit.Assert.*;

public class MeterDataRepositoryTest {
    private final static long ID = 1L;
    private final static Float VALUE = 123f;
    private final static String DATE = "2024-01";
    private MeterDataRepository repository;

    @Before
    public void setUp() throws Exception {
        repository = new MeterDataRepository();
    }

    @Test
    public void methodShouldBeSaveNewSerialNumber() {
        boolean actual = repository.isExist(ID);
        assertFalse(actual);
        repository.registrationWaterMeter(ID);
        actual = repository.isExist(ID);
        assertTrue(actual);
    }


    @Test
    public void methodShouldBeSaveMeterData() {
        repository.registrationWaterMeter(ID);
        Float actual = repository.getValue(ID, DATE);
        assertNull(actual);
        repository.addValue(new MeterData(ID, DATE, VALUE));
        actual = repository.getValue(ID, DATE);
        assertEquals(VALUE, actual);
    }

    @Test
    public void methodShouldBeReturnAllValuesBySerialNumber() {
        repository.registrationWaterMeter(ID);
        MeterData data1 = new MeterData(ID, "2024-01", 123f);
        MeterData data2 = new MeterData(ID, "2024-02", 555f);
        MeterData data3 = new MeterData(ID, "2024-05", 888f);
        MeterData data4 = new MeterData(ID, "2024-12", 12f);
        List<MeterData> waterMeterList = List.of(data1, data2, data3, data4);
        waterMeterList.forEach(repository::addValue);
        List<MeterData> dataFromDb = repository.getValues(ID);
        boolean actual = waterMeterList.containsAll(dataFromDb);
        assertTrue(actual);
    }

}