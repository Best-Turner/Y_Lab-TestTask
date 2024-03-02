package ru.ylab.repository;

import org.junit.Before;
import org.junit.Test;
import ru.ylab.model.CounterType;
import ru.ylab.model.Role;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class WaterCounterRepositoryTest {

    private final static Long ID = 1l;
    private final static String SERIAL_NUMBER = "H-1234";
    private final static Float VALUE = 123f;
    private final static CounterType TYPE = CounterType.HOT;
    private Connection connection;
    private WaterMeter waterCounter;
    private WaterMeter waterCounter1;
    private User owner;
    private WaterCounterRepository repository;
    private List<WaterMeter> counterMap;


    @Before
    public void setUp() throws Exception {
        owner = new User(1L, "user", "user@mai.ru", "1234", Role.USER);
        waterCounter = new WaterMeter(ID, SERIAL_NUMBER, TYPE, VALUE, owner);
        waterCounter1 = new WaterMeter(ID + 1, SERIAL_NUMBER.concat("123"), TYPE, VALUE, owner);
        counterMap = new ArrayList<>();
        counterMap.add(waterCounter);
        repository = new WaterCounterRepository(connection);
    }


    @Test
    public void whenSaveUniqueWaterCounterThenSavingOccurs() {
        int actual = counterMap.size();
        assertEquals(1, actual);
        repository.addWaterCounter(waterCounter1);
        actual = counterMap.size();
        assertEquals(2, actual);

    }

    @Test
    public void whenSaveNonUniqueWaterCounterThenSavingNotOccur() {
        int actual = counterMap.size();
        assertEquals(1, actual);
        repository.addWaterCounter(waterCounter1);
        actual = counterMap.size();
        assertEquals(2, actual);
        repository.addWaterCounter(waterCounter1);
        repository.addWaterCounter(waterCounter1);
        actual = counterMap.size();
        assertEquals(2, actual);
    }

    @Test
    public void whenGetIsExistWaterCounterReturnWaterCounter() {
        Optional<WaterMeter> actual = repository.getWaterCounter(SERIAL_NUMBER);
        assertEquals(Optional.of(waterCounter), actual);
    }

    @Test
    public void whenGetNotExistWaterCounterThenReturnEmpty() {
        Optional<WaterMeter> actual = repository.getWaterCounter(SERIAL_NUMBER + "123");
        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void whenGetExistWaterCounterThenReturnTrue() {
        assertTrue(repository.isExist(SERIAL_NUMBER));
    }

    @Test
    public void whenGetNotExistWaterCounterThenReturnFalse() {
        assertFalse(repository.isExist(SERIAL_NUMBER + "123"));
    }


    @Test
    public void whenGetListWaterCounterThenReturnMap() {
        List<WaterMeter> actual = repository.getAllWaterCounters();
        assertEquals(List.class, actual.getClass());
        assertEquals(counterMap, actual);
    }


}