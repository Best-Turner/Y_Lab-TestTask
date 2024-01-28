package ru.ylab.repository;

import org.junit.Before;
import org.junit.Test;
import ru.ylab.model.Role;
import ru.ylab.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class UserRepositoryTest {
    private final static String NAME = "admin";
    private final static String EMAIL = "admin@mail.ru";
    private final static Role ADMIN = Role.ADMIN;
    private final static String PASSWORD = "1234";
    private final static Long USER_ID = 1L;
    private User user;
    private User user1;
    private Map<String, User> usersMap;
    private UserRepository repository;


    @Before
    public void setUp() throws Exception {
        usersMap = new HashMap<>();
        user = new User(USER_ID, NAME, EMAIL, PASSWORD, ADMIN);
        user1 = new User(USER_ID + 1, NAME, EMAIL.replace("ru", "@com"), PASSWORD, Role.USER);
        usersMap.put(EMAIL, user);
        repository = new UserRepository(usersMap);
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
        int actual = usersMap.size();
        assertEquals(1, actual);
        repository.save(user1);
        actual = usersMap.size();
        assertEquals(2, actual);
    }

    @Test
    public void whenSaveNotUniqueUserThenSizeShouldNotIncrease() {
        int actual = usersMap.size();
        assertEquals(1, actual);
        repository.save(user1);
        actual = usersMap.size();
        assertEquals(2, actual);
        repository.save(user1);
        repository.save(user1);
        actual = usersMap.size();
        assertEquals(2, actual);
    }

    @Test
    public void whenUpdateUserThenUserShouldBeChange() {
        User actual = usersMap.get(EMAIL);
        assertEquals(user, actual);
        actual.setName("anotherName");
        repository.update(EMAIL, user1);
        assertNotEquals(user, user1);
    }

    @Test
    public void whenDeleteExistUserThenSizeMustBeDecrease() {
        int actual = usersMap.size();
        assertEquals(1, actual);
        repository.delete(user);
        actual = usersMap.size();
        assertEquals(0, actual);
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
        Map<String, User> actual = repository.userList();
        assertEquals(HashMap.class, actual.getClass());
        assertEquals(usersMap, actual);
    }
}