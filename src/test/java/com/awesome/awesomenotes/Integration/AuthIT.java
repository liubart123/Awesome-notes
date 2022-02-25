package com.awesome.awesomenotes.Integration;

import java.util.Arrays;
import java.util.HashSet;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import lombok.extern.slf4j.Slf4j;

// @SpringBootTest
@SpringBootTest
// @TestPropertySource(locations = { "classpath:application.properties",
// "classpath:application-test2.properties" })
// @TestPropertySource(locations = "classpath:application-test2.properties")
// @TestPropertySource(locations = { "classpath:application-test2.properties",
// "classpath:application.properties" })
@Slf4j
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class AuthIT {
    @Value("${spring.datasource.url}")
    String db;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @Autowired
    TestUtils testUtils;

    String adminToken, moderatorToken, userToken;

    @BeforeAll
    void initTest() throws Exception {
        log.info("initng AuthTest");

        var tempUser = testUtils.getRegisteredUserWithToken(ERole.ROLE_ADMIN);
        adminToken = tempUser.getRight();

        tempUser = testUtils.getRegisteredUserWithToken(ERole.ROLE_MODERATOR);
        moderatorToken = tempUser.getRight();

        tempUser = testUtils.getRegisteredUserWithToken(ERole.ROLE_USER);
        userToken = tempUser.getRight();
    }

    // TODO:Add login and registration testssts
    @Test
    void requestWithoutTokenShouldFail() throws Exception {
        mockMvc.perform(
                get("/api/auth/current")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithInvalidTokenShouldFail() throws Exception {
        mockMvc.perform(
                get("/api/auth/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + "invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithJwtShouldBeSuccesful() throws Exception {
        mockMvc.perform(
                get("/api/auth/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void requestWithJwtOfAppropriateUserShouldBeSuccesful() throws Exception {
        mockMvc.perform(
                get("/api/auth/moderator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + moderatorToken))
                .andExpect(status().isOk());
    }

    @Test
    void requestWithJwtOfUnappropriateUserShouldFail() throws Exception {
        mockMvc.perform(
                get("/api/auth/moderator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
