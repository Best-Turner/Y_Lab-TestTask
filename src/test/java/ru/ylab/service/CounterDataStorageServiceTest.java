package ru.ylab.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.CounterType;
import ru.ylab.model.MeterData;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;
import ru.ylab.repository.MeterDataRepository;
import ru.ylab.service.impl.MeterDataServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CounterDataStorageServiceTest {

    private final static Long ID = 1l;
    private final static String SERIAL_NUMBER = "H-1234";
    private final static Float VALUE = 123f;
    private final static String DATE = "2024-1";
    private final static CounterType TYPE = CounterType.HOT;
    @Mock
    MeterDataRepository repository;
    @InjectMocks
    MeterDataServiceImpl service;
    private WaterMeter waterCounter;
    private User owner;

    @Before
    public void setUp() throws Exception {
        waterCounter = new WaterMeter(SERIAL_NUMBER, TYPE, VALUE, owner);
        waterCounter.setId(ID);
        MockitoAnnotations.openMocks(this);

    }


    @Test
    public void whenChangeCurrentValueThenReturnTrue() throws InvalidDataException, WaterCounterNotFoundException {
        when(repository.isExist(ID)).thenReturn(true);
        when(repository.getValue(ID, DATE)).thenReturn(VALUE);
        boolean actual = service.submitValue(ID, VALUE);
        assertTrue(actual);
    }

    @Test
    public void whenGetValuesThenReturnList() {
        List<MeterData> expect = Collections.emptyList();
        when(repository.isExist(ID)).thenReturn(true);
        when(repository.getValuesByWaterMeterId(ID)).thenReturn(expect);
        List<MeterData> actual = service.getValues(ID);
        assertEquals(expect, actual);
    }

    @Test
    public void whenGetValueByDateThenReturnValue() {
        when(repository.isExist(ID)).thenReturn(true);
        when(repository.getValue(ID, DATE)).thenReturn(VALUE);
        Float actual = service.getValueByDate(ID, DATE);
        assertEquals(VALUE, actual);
    }

}