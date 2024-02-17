package ru.ylab.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.ylab.exception.UserNotFoundException;
import ru.ylab.model.Role;
import ru.ylab.model.User;
import ru.ylab.repository.UserRepository;
import ru.ylab.service.impl.UserServiceImpl;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private final static String NAME = "admin";
    private final static String EMAIL = "admin@mail.ru";
    private final static Role ADMIN = Role.ADMIN;
    private final static String PASSWORD = "1234";
    private final static Long PLAYER_ID = 1L;
    @Mock
    UserRepository repository;
    @InjectMocks
    UserServiceImpl service;
    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User(PLAYER_ID, NAME, EMAIL, PASSWORD, ADMIN);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenSaveUniqueUserThanSavingOccurs() {
        when(repository.isExist(EMAIL)).thenReturn(false);
        verify(repository, never()).isExist(EMAIL);
        service.saveUser(user);
        verify(repository, times(1)).isExist(EMAIL);
        verify(repository, times(1)).save(user);
    }

    @Test
    public void whenSaveNonUniqueUserThanSavingNotOccur() {
        when(repository.isExist(EMAIL)).thenReturn(true);
        verify(repository, never()).isExist(EMAIL);
        service.saveUser(user);
        verify(repository, times(1)).isExist(EMAIL);
        verify(repository, never()).save(user);
    }

    @Test
    public void whenGetUserWhichExistThanReturnThisUser() throws UserNotFoundException {
        when(repository.getUser(EMAIL)).thenReturn(Optional.of(user));
        verify(repository, never()).getUser(EMAIL);
        User actualUser = service.getUser(EMAIL);
        verify(repository, times(1)).getUser(EMAIL);
        assertEquals(user, actualUser);
    }

    @Test
    public void whenGetUserWhichNotExistThanThrowUserNotFoundException() throws UserNotFoundException {
        when(repository.getUser(EMAIL)).thenReturn(Optional.empty());
        assertThrows("User with this" + EMAIL + " is not registered",
                UserNotFoundException.class,
                () -> service.getUser(EMAIL));
    }

    @Test
    public void whenGetUserListThewReturnUserList() {
        when(repository.usersGroup()).thenReturn(Collections.emptyList());
        verify(repository, never()).usersGroup();
        service.allUsers();
        verify(repository, times(1)).usersGroup();

    }

    @Test
    public void whenUserExistThanReturnTrue() {
        when(repository.isExist(EMAIL)).thenReturn(true);
        verify(repository, never()).isExist(EMAIL);
        boolean actual = service.isExist(EMAIL);
        verify(repository, times(1)).isExist(EMAIL);
        assertTrue(actual);
    }

    @Test
    public void whenDeleteExistUserReturnTrue() {
        when(repository.getUser(EMAIL)).thenReturn(Optional.of(user));
        when(repository.delete(user)).thenReturn(true);
        boolean actual = service.delete(EMAIL);
        verify(repository, times(1)).delete(user);
        verify(repository, times(1)).getUser(EMAIL);
        assertTrue(actual);
    }

    @Test
    public void whenInputPasswordEqualsUserPasswordThanReturnTrue() throws UserNotFoundException {
        boolean actual = service.checkUserCredentials(user, PASSWORD);
        assertTrue(actual);
    }

    @Test
    public void whenInputPasswordNotEqualsUserPasswordThanReturnFalse() throws UserNotFoundException {
        boolean actual = service.checkUserCredentials(user, PASSWORD.concat("1234"));
        assertFalse(actual);
    }

    @Test()
    public void whenUserIsNullThanThrowUserNotFoundException() {
        assertThrows("You haven't registered yet",
                UserNotFoundException.class,
                () -> service.checkUserCredentials(null, PASSWORD));
    }

}