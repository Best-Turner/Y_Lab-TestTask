package ru.ylab.repository;

import ru.ylab.model.CounterType;
import ru.ylab.model.Role;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The `UserRepository` class represents a repository for managing user data and associated operations.
 * It provides methods for retrieving, saving, updating, and deleting user information, as well as checking
 * for user existence, retrieving the list of users, and managing water counters associated with users.
 */
public class UserRepository {

    private final Connection connection;
    private String sql;

    /**
     * Constructs a new `UserRepository` with the given map of users.
     *
     * @param connection
     */
    public UserRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Retrieves a user with the specified email address.
     *
     * @param email The email address of the user to retrieve.
     * @return An Optional containing the user with the given email, or empty if not found.
     */
    public Optional<User> getUser(String email) {
        User userFromDb = null;
        sql = "SELECT * FROM model.users WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userFromDb = userFromIncomingDatabaseData(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(userFromDb);
    }

    public User getUser(long id) {
        User userFromDb = null;
        sql = "SELECT * FROM model.users u WHERE u.id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userFromDb = userFromIncomingDatabaseData(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userFromDb;
    }

    /**
     * Saves a user to the repository.
     *
     * @param user The user to be saved.
     */
    public void save(User user) {
        sql = "INSERT INTO model.users (name, email, password, role) VALUES(?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(true);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setObject(4, user.getRole(), Types.OTHER);
            preparedStatement.executeUpdate();
            //connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Deletes a user from the repository.
     *
     * @param user The user to be deleted.
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean delete(User user) {
        sql = "DELETE FROM model.users WHERE id = " + user.getId();
        boolean executeResult = false;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                executeResult = true;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return executeResult;
    }

    /**
     * Checks if a user with the specified email exists in the repository.
     *
     * @param email The email address to check for existence.
     * @return True if the user exists, false otherwise.
     */
    public boolean isExist(String email) {
        boolean executeResult = false;
        sql = "SELECT EXISTS(SELECT 1 FROM model.users WHERE email = ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                executeResult = resultSet.getBoolean(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return executeResult;
    }

    /**
     * Retrieves the entire list of users in the repository.
     *
     * @return The map of users in the repository.
     */
    public List<User> usersGroup() {
        List<User> users = new ArrayList<>();
        sql = "SELECT * FROM model.users";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(userFromIncomingDatabaseData(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    /**
     * Retrieves the set of water counters associated with a user.
     *
     * @param ownerId The id address of the user.
     * @return The set of water counters associated with the user.
     */
    public List<WaterMeter> getWaterCounters(long ownerId) {
        List<WaterMeter> meters = new ArrayList<>();
        sql = "SELECT * FROM model.water_meters w WHERE w.owner = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, ownerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String serialNumber = resultSet.getString("serial_number");
                String typeFromDb = resultSet.getString("type");
                CounterType type = CounterType.valueOf(typeFromDb);
                float value = resultSet.getFloat("current_value");
                WaterMeter buildNewWaterMeter = new WaterMeter(serialNumber, type, value, null);
                buildNewWaterMeter.setId(id);
                meters.add(buildNewWaterMeter);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return meters;
    }


    private User userFromIncomingDatabaseData(ResultSet resultSet) {
        User userFromDb;
        try {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String emailFromDb = resultSet.getString("email");
            String password = resultSet.getString("password");
            String roleFromDb = resultSet.getString("role");
            Role role = Role.valueOf(roleFromDb);
            userFromDb = new User(name, emailFromDb, password, role);
            userFromDb.setId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userFromDb;
    }
}