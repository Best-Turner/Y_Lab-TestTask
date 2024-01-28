package ru.ylab.service.impl;

import ru.ylab.exception.UserNotFoundException;
import ru.ylab.model.User;
import ru.ylab.model.WaterCounter;
import ru.ylab.repository.UserRepository;
import ru.ylab.service.UserService;

import java.util.*;

public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveUser(User user) {
        if (!isExist(user.getEmail())) {
            Set<WaterCounter> counters = new HashSet<>();
            user.setWaterCounters(counters);
            repository.save(user);
        }
    }

    @Override
    public User getUser(String email) throws UserNotFoundException {
        return repository.getUser(email).orElseThrow(() ->
                new UserNotFoundException("User with this" + email + " is not registered"));
    }

    @Override
    public List<User> allUsers() {
        Map<String, User> userMap = repository.userList();
        if (!userMap.isEmpty()) {
            List<User> users = new ArrayList<>();

            for (Map.Entry<String, User> entry : userMap.entrySet()) {
                users.add(entry.getValue());
            }
            return users;
        }
        return Collections.emptyList();
    }

    @Override
    public void updateUser(String email, User updatedUser) {
        User oldUser = repository.getUser(email).get();
        repository.save(updatedUser);
        repository.delete(oldUser);
    }

    @Override
    public boolean delete(String email) {
        return repository.delete(repository.getUser(email).get());
    }

    @Override
    public boolean isExist(String email) {
        return repository.isExist(email);
    }

    @Override
    public boolean checkUserCredentials(User userByEmail, String inputPassword) throws UserNotFoundException {
        boolean isAuthentication = false;
        if (userByEmail == null) {
            throw new UserNotFoundException("You haven't registered yet");

        } else {
            if (userByEmail.getPassword().equals(inputPassword)) {
                isAuthentication = true;
            }
        }
        return isAuthentication;
    }

    @Override
    public List<WaterCounter> waterCounters(User owner) {
        List<WaterCounter> list = new ArrayList<>();
        String email = owner.getEmail();
        Set<WaterCounter> waterCounters = repository.getWaterCounters(email);
        waterCounters.forEach(list::add);
        return list;
    }

    @Override
    public boolean addWaterCounter(User user, WaterCounter waterCounter) {
        String email = user.getEmail();
        Set<WaterCounter> waterCounters = repository.getWaterCounters(email);
        if (waterCounters.contains(waterCounter)) {
            return false;
        }
        repository.addWaterCounterToUser(user.getEmail(), waterCounter);
        return true;
    }

}
