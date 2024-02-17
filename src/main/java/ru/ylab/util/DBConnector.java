package ru.ylab.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {

    private static Connection connection;

    private DBConnector() {
    }


    private static String[] getProperties(String path) {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        final String[] connectionData = new String[3];
        try (InputStream resourceAsStream = loader.getResourceAsStream(path)) {
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

    public static Connection getConnection() {
        String[] properties = getProperties("liquibase.properties");
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(properties[0], properties[1], properties[2]);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }
}
