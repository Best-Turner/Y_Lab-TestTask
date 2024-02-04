package ru.ylab;

import ru.ylab.app.in.ConsoleServiceMediator;
import ru.ylab.model.*;
import ru.ylab.repository.CounterDataStorageRepository;
import ru.ylab.repository.UserRepository;
import ru.ylab.repository.WaterCounterRepository;
import ru.ylab.service.CounterDataStorageService;
import ru.ylab.service.UserService;
import ru.ylab.service.WaterCounterService;
import ru.ylab.service.impl.CounterDataStorageServiceImpl;
import ru.ylab.service.impl.UserServiceImpl;
import ru.ylab.service.impl.WaterCounterServiceImpl;
import ru.ylab.util.UserValidator;
import ru.ylab.util.WaterCounterValidator;
import ru.ylab.util.impl.UserValidatorImpl;
import ru.ylab.util.impl.WaterCounterValidatorImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * The `Monitor Service` web service for submitting readings of heating, hot and cold water meters.
 *
 * @author [Alexander]
 * @version 1.0
 * @since [28/01/2024]
 */
public class Main {

    public static void main(String[] args) {
        Map<String, WaterCounter> counterMap = new HashMap<>();
        Map<String, User> userMap = new HashMap<>();

        User admin = new User(1L, "Admin", "admin@mail.ru", "1234", Role.ADMIN);
        User user = new User(2L, "User", "user@mail.ru", "1234", Role.USER);

        WaterCounter counter1 = new WaterCounter(1L, "H-1234", CounterType.HOT, 123f, admin);
        WaterCounter counter2 = new WaterCounter(2L, "C-4321", CounterType.COLD, 321f, user);
        admin.setWaterCounters(new HashSet<>());
        user.setWaterCounters(new HashSet<>()); //newUser@mail.ru  1234
        admin.getWaterCounterList().add(counter1);
        user.getWaterCounterList().add(counter2);

        userMap.put(admin.getEmail(), admin);
        userMap.put(user.getEmail(), user);

        counterMap.put(counter1.getSerialNumber(), counter1);
        counterMap.put(counter2.getSerialNumber(), counter2);

        UserRepository userRepository = new UserRepository(userMap);
        UserService userService = new UserServiceImpl(userRepository);
        UserValidator userValidator = new UserValidatorImpl(userService);
        WaterCounterRepository counterRepository = new WaterCounterRepository(counterMap);
        CounterDataStorage dataStorage = CounterDataStorage.getInstance();
        CounterDataStorageRepository dataStorageRepository = new CounterDataStorageRepository(dataStorage);
        CounterDataStorageService dataStorageService = new CounterDataStorageServiceImpl(dataStorageRepository);
        WaterCounterService counterService = new WaterCounterServiceImpl(counterRepository, dataStorageService);
        WaterCounterValidator counterValidator = new WaterCounterValidatorImpl(counterService);
        ConsoleServiceMediator console = new ConsoleServiceMediator(userValidator, counterValidator);

        dataStorage.registerWaterCounter("H-1234");
        dataStorage.registerWaterCounter("C-4321");
        dataStorage.addValue("H-1234", "2023-12", 123f);
        dataStorage.addValue("C-4321", "2024-1", 321f);

        while (true) {
            User user1 = console.processStartPageCommand();
            if (user1 != null) {
                console.pageForRegisteredUser(user1);
            }
        }
    }
}
