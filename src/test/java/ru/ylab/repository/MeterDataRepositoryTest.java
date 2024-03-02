package ru.ylab.repository;

import org.junit.Before;
import org.junit.Test;
import ru.ylab.model.MeterData;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.*;

public class MeterDataRepositoryTest {
    private final static long ID = 1L;
    private final static Float VALUE = 123f;
    private final static String DATE = "2024-01";
    private MeterDataRepository repository;

    private Connection connection;

    @Before
    public void setUp() throws Exception {
        repository = new MeterDataRepository(connection);
    }


    @Test
    public void methodShouldBeSaveMeterData() {
        Float actual = repository.getValue(ID, DATE);
        assertNull(actual);
        repository.addValue(new MeterData(ID, DATE, VALUE));
        actual = repository.getValue(ID, DATE);
        assertEquals(VALUE, actual);
    }

    @Test
    public void methodShouldBeReturnAllValuesBySerialNumber() {
        MeterData data1 = new MeterData(ID, "2024-01", 123f);
        MeterData data2 = new MeterData(ID, "2024-02", 555f);
        MeterData data3 = new MeterData(ID, "2024-05", 888f);
        MeterData data4 = new MeterData(ID, "2024-12", 12f);
        List<MeterData> waterMeterList = List.of(data1, data2, data3, data4);
        waterMeterList.forEach(repository::addValue);
        List<MeterData> dataFromDb = repository.getValuesByWaterMeterId(ID);
        boolean actual = waterMeterList.containsAll(dataFromDb);
        assertTrue(actual);
    }

}