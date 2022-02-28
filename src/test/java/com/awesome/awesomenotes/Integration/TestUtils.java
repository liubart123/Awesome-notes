package com.awesome.awesomenotes.Integration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.awesome.awesomenotes.authentication.AuthException;
import com.awesome.awesomenotes.authentication.AuthService;
import com.awesome.awesomenotes.exception.ElementCreationException;
import com.awesome.awesomenotes.user.User;
import com.awesome.awesomenotes.user.UserService;
import com.awesome.awesomenotes.user.role.ERole;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestUtils {
    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;

    static Integer fastUserCount = 0;

    static Integer getFastUserCount() {
        return fastUserCount++;
    }

    public Pair<User, String> getRegisteredUserWithToken(User user) throws ElementCreationException, AuthException {
        User registered = userService.registerUser(user);
        String token = authService.generateJwt(registered);
        return Pair.of(registered, token);
    }

    public Pair<User, String> getRegisteredUserWithToken(ERole... roles)
            throws ElementCreationException, AuthException {
        Integer userCount = getFastUserCount();
        User registered = userService.registerUser(new User(
                null,
                "user" + userCount,
                "user@asd.asd" + userCount,
                "password",
                new HashSet<>(Arrays.asList(roles))));
        String token = authService.generateJwt(registered);
        return Pair.of(registered, token);
    }

    public Map<ERole, Pair<User, String>> getThreeRegisteredUsersWithRoles(String emailAndUsernamePrefix)
            throws ElementCreationException, AuthException {
        Map<ERole, Pair<User, String>> map = new HashMap<>();
        var pair = getRegisteredUserWithToken(new User(
                null,
                "admin" + emailAndUsernamePrefix,
                "admin@asd.asd" + emailAndUsernamePrefix,
                "password",
                new HashSet<>(Arrays.asList(
                        ERole.ROLE_ADMIN))));
        map.put(ERole.ROLE_ADMIN, pair);

        pair = getRegisteredUserWithToken(new User(
                null,
                "moderator" + emailAndUsernamePrefix,
                "moderator@asd.asd" + emailAndUsernamePrefix,
                "password",
                new HashSet<>(Arrays.asList(
                        ERole.ROLE_MODERATOR))));
        map.put(ERole.ROLE_MODERATOR, pair);
        pair = getRegisteredUserWithToken(new User(
                null,
                "user" + emailAndUsernamePrefix,
                "user@asd.asd" + emailAndUsernamePrefix,
                "password",
                new HashSet<>(Arrays.asList(
                        ERole.ROLE_USER))));
        map.put(ERole.ROLE_USER, pair);
        return map;
    }

}
