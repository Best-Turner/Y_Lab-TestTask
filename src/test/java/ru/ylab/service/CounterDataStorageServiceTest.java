package ru.ylab.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.ylab.exception.InvalidDataException;
import ru.ylab.model.CounterType;
import ru.ylab.model.User;
import ru.ylab.model.WaterCounter;
import ru.ylab.repository.CounterDataStorageRepository;
import ru.ylab.service.impl.CounterDataStorageServiceImpl;

import java.util.Collections;
import java.util.Map;

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
    CounterDataStorageRepository repository;
    @InjectMocks
    CounterDataStorageServiceImpl service;
    private WaterCounter waterCounter;
    private User owner;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void shouldCallMethodRegistrationWaterCounter() {
        service.registrationCounter(SERIAL_NUMBER);
        verify(repository, times(1)).registrationWaterCounter(SERIAL_NUMBER);
    }

    @Test
    public void whenChangeCurrentValueThenReturnTrue() throws InvalidDataException {
        when(repository.isExist(SERIAL_NUMBER)).thenReturn(true);
        when(repository.getValue(SERIAL_NUMBER, DATE)).thenReturn(VALUE);
        boolean actual = service.submitValue(SERIAL_NUMBER, VALUE);
        assertTrue(actual);
    }

    @Test
    public void whenGetValuesThenReturnMap() {
        Map<String, Float> expect = Collections.emptyMap();
        when(repository.isExist(SERIAL_NUMBER)).thenReturn(true);
        when(repository.getValues(SERIAL_NUMBER)).thenReturn(expect);
        Map<String, Float> actual = service.getValues(SERIAL_NUMBER);
        assertEquals(expect, actual);
    }

    @Test
    public void whenGetValueByDateThenReturnValue() {
        when(repository.isExist(SERIAL_NUMBER)).thenReturn(true);
        when(repository.getValue(SERIAL_NUMBER, DATE)).thenReturn(VALUE);
        Float actual = service.getValueByDate(SERIAL_NUMBER, DATE);
        assertEquals(VALUE, actual);
    }

}