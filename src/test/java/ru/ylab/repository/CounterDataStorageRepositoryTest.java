package ru.ylab.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.ylab.model.CounterDataStorage;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CounterDataStorageRepositoryTest {
    private final static String SERIAL_NUMBER = "H-1234";
    private final static Float VALUE = 123f;
    private final static String DATE = "2024-01";
    @Mock
    CounterDataStorage dataStorage;
    @InjectMocks
    CounterDataStorageRepository repository;
    private Map<String, Map<String, Float>> storage;
    private Map<String, Float> values;

    @Before
    public void setUp() throws Exception {
        storage = new HashMap<>();
        values = new HashMap<>();
        values.put(DATE, VALUE);
        storage.put(SERIAL_NUMBER, values);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldCallMethodRegisterWaterCounter() {
        repository.registrationWaterCounter(SERIAL_NUMBER);
        repository.addValue(SERIAL_NUMBER, DATE, VALUE);
        verify(dataStorage, times(1)).registerWaterCounter(SERIAL_NUMBER);
    }

    @Test
    public void whenGetExistSerialNumberThenReturnTrue() {
        when(dataStorage.isRegistry(SERIAL_NUMBER)).thenReturn(true);
        repository.registrationWaterCounter(SERIAL_NUMBER);
        assertTrue(repository.isExist(SERIAL_NUMBER));
    }

    @Test
    public void whenGetValueShouldBeReturnValue() {
        when(dataStorage.getValueByDate(SERIAL_NUMBER, DATE)).thenReturn(VALUE);
        Float actual = repository.getValue(SERIAL_NUMBER, DATE);
        verify(dataStorage, times(1)).getValueByDate(SERIAL_NUMBER, DATE);
        assertEquals(VALUE, actual);
    }

    @Test
    public void whenGetValuesThenReturnMapValues() {
        when(dataStorage.getValuesWithDate(SERIAL_NUMBER)).thenReturn(values);
        Map<String, Float> actual = repository.getValues(SERIAL_NUMBER);
        assertEquals(values, actual);
    }

    @Test
    public void whenDeleteValueThenReturnTrue() {
        when(dataStorage.deleteWaterCounterAndData(SERIAL_NUMBER)).thenReturn(true);
        assertTrue(repository.delete(SERIAL_NUMBER));
    }

}