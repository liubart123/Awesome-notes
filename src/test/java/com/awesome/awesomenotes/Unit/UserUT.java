package com.awesome.awesomenotes.Unit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import com.awesome.awesomenotes.authentication.AuthException;
import com.awesome.awesomenotes.exception.ElementCreationException;
import com.awesome.awesomenotes.exception.ElementNotFoundException;
import com.awesome.awesomenotes.user.User;
import com.awesome.awesomenotes.user.UserRepository;
import com.awesome.awesomenotes.user.UserService;
import com.awesome.awesomenotes.user.role.ERole;
import com.awesome.awesomenotes.util.CryptoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
public class UserUT {
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    UserService userService = new UserService();

    User registeredUser = new User(
            1L,
            "username",
            "email",
            "password",
            new HashSet<>(Arrays.asList(
                    ERole.ROLE_ADMIN,
                    ERole.ROLE_MODERATOR)));
    User savedUserInDb;

    @BeforeAll
    void initTest() throws ElementCreationException {
        CryptoUtil cryptoUtil = new CryptoUtil();
        cryptoUtil.setHashSalt("hashSalt");

        userService.setCryptoUtil(cryptoUtil);
        userService.setObjectMapper(new ObjectMapper());
        userService.setUserRepository(userRepository);

        // getting saved in DB user
        when(userRepository.save(any())).thenAnswer(user -> {
            savedUserInDb = user.getArgument(0);
            return user.getArgument(0);
        });
        userService.registerUser(registeredUser);

        reset(userRepository);
    }

    // USER_REGISTRATION
    @Test
    void userRegistration_shouldSaveUserWithHashedPass() throws ElementCreationException {
        // when
        userService.registerUser(registeredUser);

        // then
        verify(userRepository)
                .save(
                        argThat(user -> user.getPassword() != registeredUser.getPassword()
                                && user.getUsername() == registeredUser.getUsername()
                                && user.getEmail() == registeredUser.getEmail()));
    }

    // FIND_USER_BY_CREDS
    @Test
    void findUserByCreds_shouldReturnUserWithRightCreds() throws ElementNotFoundException {
        // given
        when(userRepository.findByEmail(registeredUser.getEmail())).thenReturn(Optional.of(savedUserInDb));

        // when
        User actualUser = userService.findUserByCreds(registeredUser);

        // then
        assertEquals(savedUserInDb, actualUser);
    }

    @Test
    void findUserByCreds_shouldFailWIthInvalidEmail() {
        // given
        when(userRepository.findByEmail(registeredUser.getEmail())).thenReturn(Optional.empty());

        // then
        assertThrows(ElementNotFoundException.class,
                // when
                () -> userService.findUserByCreds(registeredUser));
    }

    @Test
    void findUserByCreds_shouldFailWIthInvalidPassword() {
        // given
        when(userRepository.findByEmail(registeredUser.getEmail())).thenReturn(Optional.of(savedUserInDb));
        User userWithWrongPassword = registeredUser.clone();
        userWithWrongPassword.setPassword("wrong password");

        // then
        assertThrows(ElementNotFoundException.class,
                // when
                () -> userService.findUserByCreds(userWithWrongPassword));
    }

}
