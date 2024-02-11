package ru.ylab.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnector {
    private static final DBConnector INSTANCE = new DBConnector();

    private DBConnector() {
    }

    public static DBConnector getInstance() {
        return INSTANCE;
    }

    private static String[] getProperties(String path) {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        final String[] connectionData = new String[3];
        try (InputStream resourceAsStream = loader.getResourceAsStream(path);) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            connectionData[0] = properties.get("url").toString();
            connectionData[1] = properties.get("username").toString();
            connectionData[2] = properties.get("password").toString();
        } catch (Exception e) {
            throw new RuntimeException("Some parameters are missing");
        }
        return connectionData;
    }

    public Connection getConnection() {
        String[] properties = getProperties("application.properties");

        try (Connection connection = DriverManager.getConnection(properties[0], properties[1], properties[2])) {
            System.out.println("Соединение установленно");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
