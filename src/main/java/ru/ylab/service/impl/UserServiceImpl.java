package ru.ylab.service.impl;

import ru.ylab.exception.UserNotFoundException;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;
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
            Set<WaterMeter> counters = new HashSet<>();
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
        List<User> users = repository.usersGroup();
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users;
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
    public List<WaterMeter> waterCounters(User owner) {

        List<WaterMeter> waterCounters = repository.getWaterCounters(owner.getId());
        if (waterCounters.isEmpty()) {
            return Collections.emptyList();
        }
        return waterCounters;
    }

    @Override
    public User getUserById(int index) {
        return repository.getUser(index);
    }

//    @Override
//    public boolean addWaterCounter(User user, WaterMeter waterCounter) {
//        String email = user.getEmail();
//        Set<WaterMeter> waterCounters = repository.getWaterCounters(email);
//        if (waterCounters.contains(waterCounter)) {
//            return false;
//        }
//        repository.addWaterCounterToUser(user.getEmail(), waterCounter);
//        return true;
//    }

}
