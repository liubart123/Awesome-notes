package com.awesome.awesomenotes.Unit;

import java.util.Arrays;
import java.util.HashSet;

import com.awesome.awesomenotes.authentication.AuthException;
import com.awesome.awesomenotes.authentication.AuthService;
import com.awesome.awesomenotes.user.User;
import com.awesome.awesomenotes.user.role.ERole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
public class AuthUT {
    final Integer jwtExpTimeMs = 60000;

    AuthService authService = new AuthService();

    User userSample = new User(
            1L,
            "username",
            "email",
            "password",
            new HashSet<>(Arrays.asList(
                    ERole.ROLE_ADMIN,
                    ERole.ROLE_MODERATOR)));

    @BeforeAll
    void initTest() throws AuthException {
        authService.setObjectMapper(new ObjectMapper());
        authService.setJwtExpirationMs(jwtExpTimeMs);
        authService.setJwtSecret("jwtSecretSecrte");
    }

    @Test
    void generateJwt_getUserFromToken_shouldParseUserFromToken() throws AuthException {
        // given
        User user = userSample.clone();
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
