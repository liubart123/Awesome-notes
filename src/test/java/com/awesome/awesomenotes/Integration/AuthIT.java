package com.awesome.awesomenotes.Integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.awesome.awesomenotes.user.User;
import com.awesome.awesomenotes.user.UserService;
import com.awesome.awesomenotes.user.role.ERole;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
        UserService userService;

        @Autowired
        TestUtils testUtils;

        String adminToken, moderatorToken, userToken;

        @BeforeAll
        void initTest() throws Exception {
                log.info("initng AuthTest");

                Pair<User, String> tempUser = testUtils.getRegisteredUserWithToken(ERole.ROLE_ADMIN);
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
