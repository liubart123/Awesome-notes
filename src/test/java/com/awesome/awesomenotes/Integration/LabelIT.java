package com.awesome.awesomenotes.Integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.awesome.awesomenotes.authentication.AuthException;
import com.awesome.awesomenotes.exception.ElementCreationException;
import com.awesome.awesomenotes.label.LabelDto.LabelCreateRequest;
import com.awesome.awesomenotes.label.LabelDto.LabelResposnse;
import com.awesome.awesomenotes.label.LabelDto.LabelResposnseWithoutNotes;
import com.awesome.awesomenotes.label.LabelDto.LabelUpdateRequest;
import com.awesome.awesomenotes.note.Note;
import com.awesome.awesomenotes.note.NoteService;
import com.awesome.awesomenotes.user.User;
import com.awesome.awesomenotes.user.role.ERole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class LabelIT {

        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        TestUtils testUtils;
        String adminToken, user1Token, user2Token;
        @Autowired
        NoteService noteService;
        Note note1, note2, note3;

        @BeforeAll
        void init() throws ElementCreationException, AuthException {
                log.info("initng LabelIT");

                adminToken = testUtils.getRegisteredUserWithToken(ERole.ROLE_ADMIN).getRight();
                Pair<User, String> user1 = testUtils.getRegisteredUserWithToken(ERole.ROLE_USER);
                user1Token = user1.getRight();
                user2Token = testUtils.getRegisteredUserWithToken(ERole.ROLE_USER).getRight();
        }

        @Test
        void testCrudForLabelController() throws JsonProcessingException, UnsupportedEncodingException, Exception {
                LabelCreateRequest label1 = new LabelCreateRequest(
                                "label1");

                LabelCreateRequest label2 = new LabelCreateRequest(
                                "label2");

                // POST
                String response = mockMvc.perform(
                                post("/api/labels")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token)
                                                .content(objectMapper.writeValueAsString(label1)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                LabelResposnseWithoutNotes createdLabel = objectMapper.readValue(
                                response,
                                LabelResposnseWithoutNotes.class);
                assertEquals(label1.getName(), createdLabel.getName());
                assertTrue(createdLabel.getId() != null);
                assertTrue(createdLabel.getAuthorId() != null);
                // Second POST
                response = mockMvc.perform(
                                post("/api/labels")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token)
                                                .content(objectMapper.writeValueAsString(label2)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                // GET
                response = mockMvc.perform(
                                get("/api/labels/" + createdLabel.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                LabelResposnse recievedLabel = objectMapper.readValue(response, LabelResposnse.class);
                assertEquals(label1.getName(), recievedLabel.getName());
                assertEquals(0, recievedLabel.getNotes().size());

                // GET all
                response = mockMvc.perform(
                                get("/api/labels/")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                List<LabelResposnseWithoutNotes> recievedLabels = objectMapper.readValue(
                                response,
                                new TypeReference<List<LabelResposnseWithoutNotes>>() {
                                });
                assertEquals(2, recievedLabels.size());

                // PUT
                LabelUpdateRequest updatedLabel = new LabelUpdateRequest(label1.getName() + "updated");
                response = mockMvc.perform(
                                put("/api/labels/" + recievedLabel.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token)
                                                .content(objectMapper.writeValueAsString(updatedLabel)))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                LabelResposnseWithoutNotes recievedUpdatedLabel = objectMapper.readValue(
                                response,
                                LabelResposnseWithoutNotes.class);
                assertEquals(updatedLabel.getName(), recievedUpdatedLabel.getName());

                // GET
                response = mockMvc.perform(
                                get("/api/labels/" + recievedUpdatedLabel.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                recievedLabel = objectMapper.readValue(response, LabelResposnse.class);
                assertEquals(updatedLabel.getName(), recievedLabel.getName());

                // DELETE
                mockMvc.perform(
                                delete("/api/labels/" + recievedUpdatedLabel.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token))
                                .andExpect(status().isNoContent());

                // GET
                mockMvc.perform(
                                get("/api/labels/" + recievedUpdatedLabel.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token))
                                .andExpect(status().isNotFound());
        }
}
