package com.awesome.awesomenotes.Interation;

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

        String adminToken, moderatorToken, userToken;

        @BeforeAll
        void initTest() throws Exception {
                User admin = new User(
                                null,
                                "admin",
                                "admin@asd.asd",
                                "password",
                                new HashSet<>(Arrays.asList(
                                                ERole.ROLE_ADMIN,
                                                ERole.ROLE_MODERATOR,
                                                ERole.ROLE_USER)));
                User moderator = new User(
                                null,
                                "moderator",
                                "moderator@asd.asd",
                                "password",
                                new HashSet<>(Arrays.asList(
                                                ERole.ROLE_MODERATOR,
                                                ERole.ROLE_USER)));
                User user = new User(
                                null,
                                "user",
                                "user@asd.asd",
                                "password",
                                new HashSet<>(Arrays.asList(
                                                ERole.ROLE_USER)));

                userService.registerUser(admin);
                userService.registerUser(moderator);
                userService.registerUser(user);

                String response = mockMvc.perform(
                                post("/api/auth/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(admin)))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                adminToken = objectMapper.readValue(response, ObjectNode.class).get("token").asText();

                response = mockMvc.perform(
                                post("/api/auth/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(moderator)))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                moderatorToken = objectMapper.readValue(response, ObjectNode.class).get("token").asText();

                response = mockMvc.perform(
                                post("/api/auth/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(user)))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                userToken = objectMapper.readValue(response, ObjectNode.class).get("token").asText();
        }

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
