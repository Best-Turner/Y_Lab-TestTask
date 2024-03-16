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

    void saveUser(String name, String email, String password) throws InvalidDataException;

    /**
     * Gets users information by their identifier.
     *
     * @param email User's identifier.
     * @return A User object representing the user.
     */

    User getUserByEmail(String email) throws UserNotFoundException, InvalidDataException;
    User getUserById(String userId) throws InvalidDataException;

    List<User> allUsers();


    /**
     * Check users by their credentials.
     *
     * @param email    User's email.
     * @param password User's password.
     * @return true if user is registrartion.
     */

    boolean checkUserCredentials(String email, String password) throws UserNotFoundException, InvalidDataException;

    List<WaterMeter> getWaterCounters(User owner);

}
