package ru.ylab;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.ylab.app.in.ConsoleServiceMediator;
import ru.ylab.model.*;
import ru.ylab.repository.MeterDataRepository;
import ru.ylab.repository.UserRepository;
import ru.ylab.repository.WaterCounterRepository;
import ru.ylab.service.MeterDataService;
import ru.ylab.service.UserService;
import ru.ylab.service.WaterCounterService;
import ru.ylab.service.impl.MeterDataServiceImpl;
import ru.ylab.service.impl.UserServiceImpl;
import ru.ylab.service.impl.WaterCounterServiceImpl;
import ru.ylab.util.DBConnector;
import ru.ylab.util.UserValidator;
import ru.ylab.util.WaterCounterValidator;
import ru.ylab.util.impl.UserValidatorImpl;
import ru.ylab.util.impl.WaterCounterValidatorImpl;

import java.sql.Connection;
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
        try {
            Connection connection = DBConnector.getConnection();
            Database database =
                    DatabaseFactory.getInstance()
                            .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase("db/changelog/changelog-root.xml",
                            new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("Миграции успешно выполнены!");

        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } catch (LiquibaseException ex) {
            throw new RuntimeException(ex);
        }


        Map<String, WaterMeter> counterMap = new HashMap<>();
        Map<String, User> userMap = new HashMap<>();


        User admin = new User(1L, "Admin", "admin@mail.ru", "1234", Role.ADMIN);
        User user = new User(2L, "User", "user@mail.ru", "1234", Role.USER);

        WaterMeter counter1 = new WaterMeter(1L, "H-1234", CounterType.HOT, 123f, admin);
        WaterMeter counter2 = new WaterMeter(2L, "C-4321", CounterType.COLD, 321f, user);
        admin.setWaterCounters(new HashSet<>());
        user.setWaterCounters(new HashSet<>());
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

        MeterDataRepository meterDataRepository = new MeterDataRepository();
        MeterDataService dataStorageService = new MeterDataServiceImpl(meterDataRepository);
        WaterCounterService counterService = new WaterCounterServiceImpl(counterRepository, dataStorageService);
        WaterCounterValidator counterValidator = new WaterCounterValidatorImpl(counterService);
        ConsoleServiceMediator console = new ConsoleServiceMediator(userValidator, counterValidator);
        meterDataRepository.registrationWaterMeter(counter1.getId());
        meterDataRepository.registrationWaterMeter(counter2.getId());

        meterDataRepository.addValue(new MeterData(counter1.getId(), "2023-12", 123f));
        meterDataRepository.addValue(new MeterData(counter2.getId(), "2024-1", 321f));


        while (true) {
            User user1 = console.processStartPageCommand();
            if (user1 != null) {
                console.pageForRegisteredUser(user1);
            }
        }
    }
}
