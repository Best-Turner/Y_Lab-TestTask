package ru.ylab.model;

import java.util.Objects;
import java.util.Set;

/**
 * The class represents a player in the system.
 * <p>
 * A user has a unique identifier (ID), a name, a password,
 * a role, and waterCounter list.
 */

public class User {
    /**
     * The unique identifier of the player.
     */
    private Long id;
    /**
     * The name of the user.
     */
    private String name;
    /**
     * The email of the user.
     */
    private String email;
    /**
     * The password of the user.
     */
    private String password;
    /**
     * The role of the user.
     */
    private Role role;
    /**
     * The list water counters..
     */
    private Set<WaterMeter> waterCounterList;

    public User() {
    }

    /**
     * Constructor for the User class.
     *
     * @param id
     * @param name     The name of the user.
     * @param email    The name of the user.
     * @param password The password of the user.
     * @param role     The password of the user.
     */
    public User(Long id, String name, String email, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Get the unique identifier of the user.
     *
     * @return The unique identifier of the user.
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the name of the user.
     *
     * @return The name of the user.
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the email of the user.
     *
     * @return The email of the user.
     */

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the password of the user.
     *
     * @return The password of the user.
     */

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the set of user's water counters.
     *
     * @return The list of user's water counters.
     */

    public Set<WaterMeter> getWaterCounterList() {
        return waterCounterList;
    }

    public void setWaterCounters(Set<WaterMeter> waterCounterList) {
        this.waterCounterList = waterCounterList;
    }

    /**
     * Get the role of the user.
     *
     * @return The role of the user.
     */

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Override the equals method to compare players based on their unique identifiers.
     *
     * @param o The object to compare.
     * @return true if the objects are equal, otherwise false.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) && Objects.equals(password, user.password);
    }

    /**
     * Override the hashCode method to calculate the hash code based on the player's unique identifier.
     *
     * @return The hash code of the object.
     */

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}
