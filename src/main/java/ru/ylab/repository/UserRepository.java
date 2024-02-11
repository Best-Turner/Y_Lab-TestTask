package ru.ylab.repository;

import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The `UserRepository` class represents a repository for managing user data and associated operations.
 * It provides methods for retrieving, saving, updating, and deleting user information, as well as checking
 * for user existence, retrieving the list of users, and managing water counters associated with users.
 */
public class UserRepository {

    private final Map<String, User> users;

    /**
     * Constructs a new `UserRepository` with the given map of users.
     *
     * @param userMap The initial map of users to be used in the repository.
     */
    public UserRepository(Map<String, User> userMap) {
        this.users = userMap;
    }

    /**
     * Retrieves a user with the specified email address.
     *
     * @param email The email address of the user to retrieve.
     * @return An Optional containing the user with the given email, or empty if not found.
     */
    public Optional<User> getUser(String email) {
        return Optional.ofNullable(users.get(email));
    }

    /**
     * Saves a user to the repository.
     *
     * @param user The user to be saved.
     */
    public void save(User user) {
        users.put(user.getEmail(), user);
    }

    /**
     * Updates the user information with the specified email.
     *
     * @param email The email address of the user to be updated.
     * @param user  The updated user information.
     */
    public void update(String email, User user) {
        users.put(email, user);
    }

    /**
     * Deletes a user from the repository.
     *
     * @param user The user to be deleted.
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean delete(User user) {
        users.remove(user.getEmail());
        return true;
    }

    /**
     * Checks if a user with the specified email exists in the repository.
     *
     * @param email The email address to check for existence.
     * @return True if the user exists, false otherwise.
     */
    public boolean isExist(String email) {
        return users.containsKey(email);
    }

    /**
     * Retrieves the entire list of users in the repository.
     *
     * @return The map of users in the repository.
     */
    public Map<String, User> userList() {
        return users;
    }

    /**
     * Retrieves the set of water counters associated with a user.
     *
     * @param email The email address of the user.
     * @return The set of water counters associated with the user.
     */
    public Set<WaterMeter> getWaterCounters(String email) {
        Set<WaterMeter> waterCounterList = users.get(email).getWaterCounterList();
        return waterCounterList;
    }

    /**
     * Adds a water counter to the list of water counters associated with a user.
     *
     * @param email        The email address of the user.
     * @param waterCounter The water counter to be added.
     */
    public void addWaterCounterToUser(String email, WaterMeter waterCounter) {
        users.get(email).getWaterCounterList().add(waterCounter);
    }
}
