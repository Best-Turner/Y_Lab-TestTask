package ru.ylab.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnector {
    private static final DBConnector INSTANCE = new DBConnector();

    private DBConnector() {
    }

    public DBConnector getInstance() {
        return INSTANCE;
    }


    public Connection getConnection() {


        try (Connection connection = DriverManager.getConnection("path", " user", "password")) {


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static String[] getProperties(String path) {
        ClassLoader loader = ClassLoader.getSystemClassLoader();

        try (InputStream resourceAsStream = loader.getResourceAsStream(path);) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            String pathDB = properties.get("url").toString();
            String username = properties.get("username").toString();
            String password = properties.get("password").toString();
        } catch (Exception e) {
            throw new RuntimeException("Some parameters are missing");
        }
        return null;
    }

}
