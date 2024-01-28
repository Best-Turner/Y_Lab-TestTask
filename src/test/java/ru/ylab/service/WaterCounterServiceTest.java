package ru.ylab.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.CounterType;
import ru.ylab.model.Role;
import ru.ylab.model.User;
import ru.ylab.model.WaterCounter;
import ru.ylab.repository.WaterCounterRepository;
import ru.ylab.service.impl.WaterCounterServiceImpl;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WaterCounterServiceTest {
    private final static Long ID = 1l;
    private final static String SERIAL_NUMBER = "H-1234";
    private final static Float VALUE = 123f;
    private final static CounterType TYPE = CounterType.HOT;
    @Mock
    CounterDataStorageService storageService;
    @Mock
    WaterCounterRepository repository;
    private WaterCounter waterCounter;
    private User owner;
    @InjectMocks
    private WaterCounterServiceImpl service;

    @Before
    public void setUp() throws Exception {
        owner = new User(1L, "user", "user@mai.ru", "1234", Role.USER);
        waterCounter = new WaterCounter(ID, SERIAL_NUMBER, TYPE, VALUE, owner);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldCallMethodAddWaterCounterAndRegistrationCounterWhenSaveCounter() {
        when(repository.isExist(SERIAL_NUMBER)).thenReturn(false);
        service.save(waterCounter);
        verify(repository, times(1)).addWaterCounter(waterCounter);
        verify(storageService, times(1)).registrationCounter(SERIAL_NUMBER);
    }

    @Test
    public void NotShouldCallMethodAddWaterCounterAndRegistrationCounterWhenSaveCounter() {
        when(repository.isExist(SERIAL_NUMBER)).thenReturn(true);
        service.save(waterCounter);
        verify(repository, never()).addWaterCounter(waterCounter);
        verify(storageService, never()).registrationCounter(SERIAL_NUMBER);
    }

    @Test
    public void whenGetIsExistWaterCounterReturnWaterCounter() throws WaterCounterNotFoundException {
        when(repository.getWaterCounter(SERIAL_NUMBER)).thenReturn(Optional.of(waterCounter));
        WaterCounter actual = service.getWaterCounter(SERIAL_NUMBER);
        verify(repository, times(1)).getWaterCounter(SERIAL_NUMBER);
        assertEquals(waterCounter, actual);
    }

    @Test
    public void whenGetNotExistWaterCounterThenThrowWaterCounterNoFoundException() throws WaterCounterNotFoundException {
        when(repository.getWaterCounter(SERIAL_NUMBER)).thenReturn(Optional.empty());
        assertThrows("WaterCounter with this serial number = " + SERIAL_NUMBER + " not found",
                WaterCounterNotFoundException.class, () -> service.getWaterCounter(SERIAL_NUMBER));
    }

    @Test
    public void whenGetSetWaterCountersThenReturnSet() {
        Map<String, WaterCounter> counterMap = new HashMap<>();
        Set<WaterCounter> expect = new HashSet<>();
        expect.add(waterCounter);
        counterMap.put(SERIAL_NUMBER, waterCounter);
        counterMap.put(SERIAL_NUMBER.concat("123"), waterCounter);
        when(repository.getAllWaterCounters()).thenReturn(counterMap);
        Set<WaterCounter> actual = service.allWaterCounter();
        assertEquals(expect, actual);
    }

    @Test
    public void shouldCallMethodSubmitValueWhenValueChanges() throws InvalidDataException {
        when(storageService.submitValue(SERIAL_NUMBER, VALUE)).thenReturn(true);
        service.transferData(SERIAL_NUMBER, 123f);
        verify(storageService, times(1)).submitValue(SERIAL_NUMBER, 123f);
    }

    @Test
    public void shouldCallMethodGetCurrentValueAndReturnValue() {
        when(storageService.getCurrentValue(SERIAL_NUMBER)).thenReturn(VALUE);
        Float actual = service.currentValue(SERIAL_NUMBER);
        verify(storageService, times(1)).getCurrentValue(SERIAL_NUMBER);
        assertEquals(VALUE, actual);
    }

    @Test
    public void whenDeleteIsExistWaterCounterReturnTrue() {
        when(repository.isExist(SERIAL_NUMBER)).thenReturn(true);
        service.delete(SERIAL_NUMBER);
        verify(storageService, times(1)).delete(SERIAL_NUMBER);
        verify(repository, times(1)).delete(SERIAL_NUMBER);
        boolean actual = service.delete(SERIAL_NUMBER);
        assertTrue(actual);
    }

    @Test
    public void whenDeleteIsNotExistWaterCounterReturnTrue() {
        when(repository.isExist(SERIAL_NUMBER)).thenReturn(false);
        service.delete(SERIAL_NUMBER);
        verify(storageService, never()).delete(SERIAL_NUMBER);
        verify(repository, never()).delete(SERIAL_NUMBER);
        boolean actual = service.delete(SERIAL_NUMBER);
        assertFalse(actual);
    }

    @Test
    public void shouldCallMethodGetValuesAndReturnMap() {
        when(storageService.getValues(SERIAL_NUMBER)).thenReturn(Collections.emptyMap());
        Map<String, Float> actual = service.getValues(SERIAL_NUMBER);
        verify(storageService, times(1)).getValues(SERIAL_NUMBER);
        assertEquals(Collections.emptyMap(), actual);
    }
}