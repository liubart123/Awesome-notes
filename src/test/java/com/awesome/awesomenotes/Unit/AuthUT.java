package com.awesome.awesomenotes.Unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.awesome.awesomenotes.authentication.AuthException;
import com.awesome.awesomenotes.authentication.AuthService;
import com.awesome.awesomenotes.user.User;
import com.awesome.awesomenotes.user.UserRepository;
import com.awesome.awesomenotes.user.role.ERole;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
public class AuthUT {

    UserRepository userRepository = Mockito.mock(UserRepository.class);
    AuthService authService = new AuthService();

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
    void initTest() throws AuthException {
        log.info("initing test");
        authService.setUserRepository(userRepository);
        authService.setObjectMapper(new ObjectMapper());

        authService.setHashSalt("hashSalt");
        authService.setJwtExpirationMs(60000);
        authService.setJwtSecret("jwtSecretSecrte");

        // getting saved in DB user
        when(userRepository.save(any())).thenAnswer(user -> {
            savedUserInDb = user.getArgument(0);
            return user.getArgument(0);
        });
        authService.registerUser(registeredUser);

        reset(userRepository);
    }

    @AfterEach
    void afterTestCleanup() {

    }

    // USER_REGISTRATION
    @Test
    void userRegistration_shouldSaveUserWithHashedPass() throws AuthException {
        // when
        authService.registerUser(registeredUser);

        // then
        verify(userRepository)
                .save(
                        argThat(user -> user.getPassword() != registeredUser.getPassword()
                                && user.getUsername() == registeredUser.getUsername()
                                && user.getEmail() == registeredUser.getEmail()));
    }

    // FIND_USER_BY_CREDS
    @Test
    void findUserByCreds_shouldReturnUserWithRightCreds() throws AuthException {
        // given
        when(userRepository.findByEmail(registeredUser.getEmail())).thenReturn(Optional.of(savedUserInDb));

        // when
        User actualUser = authService.findUserByCreds(registeredUser);

        // then
        assertEquals(savedUserInDb, actualUser);
    }

    @Test
    void findUserByCreds_shouldFailWIthInvalidEmail() throws AuthException {
        // given
        when(userRepository.findByEmail(registeredUser.getEmail())).thenReturn(Optional.empty());

        // then
        assertThrows(AuthException.class,
                // when
                () -> authService.findUserByCreds(registeredUser));
    }

    @Test
    void findUserByCreds_shouldFailWIthInvalidPassword() throws AuthException {
        // given
        when(userRepository.findByEmail(registeredUser.getEmail())).thenReturn(Optional.of(savedUserInDb));
        User userWithWrongPassword = registeredUser.clone();
        userWithWrongPassword.setPassword("wrong password");

        // then
        assertThrows(AuthException.class,
                // when
                () -> authService.findUserByCreds(userWithWrongPassword));
    }

    @Test
    void generateJwt_getUserFromToken_shouldParseUserFromToken() throws AuthException {
        // given
        User user = registeredUser.clone();
        String jwt = authService.generateJwt(user);

        // when
        User actualUser = authService.getUserFromToken(jwt);

        // then
        assertEquals(user.getEmail(), actualUser.getEmail());
        assertEquals(user.getUsername(), actualUser.getUsername());
        assertEquals(user.getRoles(), actualUser.getRoles());
        assertNotEquals(user.getPassword(), actualUser.getPassword());
    }

}
