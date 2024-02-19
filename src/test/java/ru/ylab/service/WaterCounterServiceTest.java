package ru.ylab.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.*;
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
    MeterDataService storageService;
    @Mock
    WaterCounterRepository repository;
    private WaterMeter waterCounter;
    private User owner;
    @InjectMocks
    private WaterCounterServiceImpl service;

    @Before
    public void setUp() throws Exception {
        owner = new User(1L, "user", "user@mai.ru", "1234", Role.USER);
        waterCounter = new WaterMeter(SERIAL_NUMBER, TYPE, VALUE, owner);
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
        WaterMeter actual = service.getWaterCounter(SERIAL_NUMBER);
        verify(repository, times(1)).getWaterCounter(SERIAL_NUMBER);
        assertEquals(waterCounter, actual);
    }

    @Test
    public void whenGetNotExistWaterCounterThenThrowWaterCounterNoFoundException() throws WaterCounterNotFoundException {
        when(repository.getWaterCounter(SERIAL_NUMBER)).thenReturn(Optional.empty());
        assertThrows("WaterCounter with this serial number = " + SERIAL_NUMBER + " not found",
                WaterCounterNotFoundException.class, () -> service.getWaterCounter(SERIAL_NUMBER));
    }

//    @Test
//    public void whenGetSetWaterCountersThenReturnSet() {
//        Map<String, WaterMeter> counterMap = new HashMap<>();
//        Set<WaterMeter> expect = new HashSet<>();
//        expect.add(waterCounter);
//        counterMap.put(SERIAL_NUMBER, waterCounter);
//        counterMap.put(SERIAL_NUMBER.concat("123"), waterCounter);
//        when(repository.getAllWaterCounters()).thenReturn(counterMap);
//        Set<WaterMeter> actual = service.allWaterCounter();
//        assertEquals(expect, actual);
//    }

    @Test
    public void shouldCallMethodSubmitValueWhenValueChanges() throws InvalidDataException, WaterCounterNotFoundException {
        when(storageService.submitValue(ID, VALUE)).thenReturn(true);
        when(repository.getWaterCounter(SERIAL_NUMBER)).thenReturn(Optional.of(waterCounter));
        service.transferData(SERIAL_NUMBER, 123f);
        verify(storageService, times(1)).submitValue(ID, 123f);
    }

    @Test
    public void shouldCallMethodGetCurrentValueAndReturnValue() throws WaterCounterNotFoundException {
        when(storageService.getCurrentValue(ID)).thenReturn(VALUE);
        when(repository.getWaterCounter(SERIAL_NUMBER)).thenReturn(Optional.of(waterCounter));
        Float actual = service.currentValue(SERIAL_NUMBER);
        verify(storageService, times(1)).getCurrentValue(ID);
        assertEquals(VALUE, actual);
    }



    @Test
    public void shouldCallMethodGetValuesAndReturnMap() throws WaterCounterNotFoundException {
        when(storageService.getValues(ID)).thenReturn(Collections.emptyList());
        when(repository.getWaterCounter(SERIAL_NUMBER)).thenReturn(Optional.of(waterCounter));
        List<MeterData> actual = service.getValues(ID);
        verify(storageService, times(1)).getValues(ID);
        assertEquals(Collections.emptyList(), actual);
    }
}