package ru.ylab.service;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.UserNotFoundException;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;

import java.util.List;

/**
 * The `UserService` interface provides methods for managing players and their transactions.
 */

public interface UserService {

    void saveUser(User user);

    /**
     * Gets users information by their identifier.
     *
     * @param email User's identifier.
     * @return A User object representing the user.
     */

    User getUser(String email) throws UserNotFoundException;

    List<User> allUsers();

    void updateUser(String email, User updatedUser);

    /**
     * Delete user by their identifier.
     *
     * @param email User's identifier.
     * @return A User object representing the user.
     */
    boolean delete(String email);

    /**
     * Check users by their identifier.
     *
     * @param email User's identifier.
     * @return true if user exist..
     */
    boolean isExist(String email);

    /**
     * Check users by their credentials.
     *
     * @param email    User's email.
     * @param password User's password.
     * @return true if user is registrartion.
     */

    boolean checkUserCredentials(String email, String password) throws UserNotFoundException, InvalidDataException;

    List<WaterMeter> waterCounters(User owner);

    User getUserById(long index);
}
