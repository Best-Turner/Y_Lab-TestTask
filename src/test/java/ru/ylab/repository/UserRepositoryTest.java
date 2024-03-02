package ru.ylab.repository;

import org.junit.Before;
import org.junit.Test;
import ru.ylab.model.Role;
import ru.ylab.model.User;
import ru.ylab.util.DBConnector;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class UserRepositoryTest {
    private final static String NAME = "admin";
    private final static String EMAIL = "admin@mail.ru";
    private final static Role ADMIN = Role.ADMIN;
    private final static String PASSWORD = "1234";
    private final static Long USER_ID = 1L;
    private Connection connection;
    private User user;
    private User user1;
    //private Map<String, User> usersMap;
    private List<User> users;
    private UserRepository repository;


    @Before
    public void setUp() throws Exception {

        users = new ArrayList<>();
        user = new User(USER_ID, NAME, EMAIL, PASSWORD, ADMIN);
        user1 = new User(USER_ID + 1, NAME, EMAIL.replace("@ru", "@com"), PASSWORD, Role.USER);
        users.add(user);
        connection = DBConnector.getConnection();
        repository = new UserRepository(connection);
    }

    @Test
    public void whenGetUserIsExistThenReturnOptionalOfUser() {
        Optional<User> actual = repository.getUser(EMAIL);
        assertEquals(Optional.of(user), actual);
    }

    @Test
    public void whenGetUserNotExistThenReturnEmpty() {
        Optional<User> actual = repository.getUser(EMAIL.concat("12"));
        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void whenSaveUniqueUserThenSizeMustBeIncrease() {
        int actual = users.size();
        assertEquals(1, actual);
        repository.save(user1);
        actual = users.size();
        assertEquals(2, actual);
    }

    @Test
    public void whenSaveNotUniqueUserThenSizeShouldNotIncrease() {
        int actual = users.size();
        assertEquals(1, actual);
        repository.save(user1);
        actual = users.size();
        assertEquals(2, actual);
        repository.save(user1);
        repository.save(user1);
        actual = users.size();
        assertEquals(2, actual);
    }


    @Test
    public void whenUserExistThenReturnTrue() {
        assertTrue(repository.isExist(EMAIL));
    }

    @Test
    public void whenUserNotExistThenReturnFalse() {
        assertFalse(repository.isExist(EMAIL.concat("123")));
    }

    @Test
    public void whenGetUsersThenReturnMapUser() {
        List<User> actual = repository.usersGroup();
        assertEquals(ArrayList.class, actual.getClass());
        assertEquals(users, actual);
    }
}