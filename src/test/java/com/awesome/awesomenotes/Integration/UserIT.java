package com.awesome.awesomenotes.Integration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.constraints.Null;

import com.awesome.awesomenotes.AwesomeNotesApplication;
import com.awesome.awesomenotes.user.User;
import com.awesome.awesomenotes.user.UserService;
import com.awesome.awesomenotes.user.role.ERole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Transactional
public class UserIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @Autowired
    TestUtils testUtils;

    String adminToken, moderatorToken, userToken;
    User admin, moderator, user;

    @BeforeAll
    void initTest() throws Exception {
        log.info("initng UserIT");

        var tempUser = testUtils.getRegisteredUserWithToken(ERole.ROLE_ADMIN);
        adminToken = tempUser.getRight();
        admin = tempUser.getLeft();

        tempUser = testUtils.getRegisteredUserWithToken(ERole.ROLE_MODERATOR);
        moderatorToken = tempUser.getRight();
        moderator = tempUser.getLeft();

        tempUser = testUtils.getRegisteredUserWithToken(ERole.ROLE_USER);
        userToken = tempUser.getRight();
        user = tempUser.getLeft();

    }

    @Test
    void changingRolesShouldChangeRoles() throws Exception {
        // checkin start condition
        assertEquals(user.getRoles().size(), userService.findUserByEmail(user.getEmail()).getRoles().size());

        // adding all roles for user
        Set<ERole> allRoles = new HashSet<>(Arrays.asList(
                ERole.ROLE_ADMIN,
                ERole.ROLE_MODERATOR,
                ERole.ROLE_USER));
        mockMvc.perform(
                patch("/api/users/" + user.getId() + "/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(objectMapper.writeValueAsString(allRoles)))
                .andExpect(status().isOk());

        // verifying all roles for user
        assertEquals(allRoles.size(), userService.findUserByEmail(user.getEmail()).getRoles().size());

        Set<ERole> userRole = new HashSet<>(Arrays.asList(
                ERole.ROLE_USER));
        mockMvc.perform(
                patch("/api/users/" + user.getId() + "/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(objectMapper.writeValueAsString(userRole)))
                .andExpect(status().isOk());

        // verifying all roles for user
        assertEquals(userRole.size(), userService.findUserByEmail(user.getEmail()).getRoles().size());
    }
}
