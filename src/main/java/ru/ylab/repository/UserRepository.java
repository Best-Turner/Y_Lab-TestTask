package ru.ylab.repository;

import ru.ylab.model.User;
import ru.ylab.model.WaterCounter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class UserRepository {

    private final Map<String, User> users;


    public UserRepository(Map<String, User> userMap) {
        this.users = userMap;
    }

    public Optional<User> getUser(String email) {
        return Optional.ofNullable(users.get(email));
    }

    public void save(User user) {
        users.put(user.getEmail(), user);
    }

    public void update(String email, User user) {
        users.replace(email, user);
    }

    public boolean delete(User user) {
        users.remove(user.getEmail());
        return true;
    }

    public boolean isExist(String email) {
        return users.containsKey(email);
    }
    public List<User> userList() {
        return (List)users.values();
    }

    public Set<WaterCounter> getWaterCounters(String email) {
        return users.get(email).getWaterCounterList();
    }
    public void addWaterCounterToUser(String email, WaterCounter waterCounter) {
        users.get(email).getWaterCounterList().add(waterCounter);
    }
}
