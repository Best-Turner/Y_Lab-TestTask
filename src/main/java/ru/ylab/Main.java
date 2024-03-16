package ru.ylab;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.ylab.app.in.menu.EntryMenu;
import ru.ylab.app.in.menu.Menu;
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

/**
 * The `Monitor Service` web service for submitting readings of heating, hot and cold water meters.
 *
 * @author [Alexander]
 * @version 1.0
 * @since [28/01/2024]
 */
public class Main {
    //       admin@mail.ru
    //       user@mail.ru
    //       test@mail.ru
    private static Connection connection;

    public static void main(String[] args) {
        try {
            connection = DBConnector.getConnection();
            Database database =
                    DatabaseFactory.getInstance()
                            .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase("db/changelog/changelog-root.xml",
                            new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
            System.out.println("Миграции успешно выполнены!");

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }


        UserRepository userRepository = new UserRepository(connection);
        UserValidator userValidator = new UserValidatorImpl();
        UserService userService = new UserServiceImpl(userRepository, userValidator);
        WaterCounterRepository counterRepository = new WaterCounterRepository(connection);

        MeterDataRepository meterDataRepository = new MeterDataRepository(connection);
        MeterDataService dataStorageService = new MeterDataServiceImpl(meterDataRepository);
        WaterCounterService counterService = new WaterCounterServiceImpl(counterRepository, dataStorageService);
        WaterCounterValidator counterValidator = new WaterCounterValidatorImpl(counterService);

        Menu menu = new EntryMenu(counterValidator, userService);
        menu.start();
    }
}
