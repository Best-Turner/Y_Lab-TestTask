package ru.ylab.service;

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
     * @param userByEmail User's identifier.
     * @param password    User's password.
     * @return true if user is registrartion.
     */

    boolean checkUserCredentials(User userByEmail, String password) throws UserNotFoundException;

    /**
     * Add new water counter.
     *
     * @param user         User owner counter.
     * @param waterCounter new counter.
     * @return true if added.
     */

    boolean addWaterCounter(User user, WaterMeter waterCounter);

    List<WaterMeter> waterCounters(User owner);
}
