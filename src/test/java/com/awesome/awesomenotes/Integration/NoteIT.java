package com.awesome.awesomenotes.Integration;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.awesome.awesomenotes.note.Note;
import com.awesome.awesomenotes.note.NoteDto;
import com.awesome.awesomenotes.user.role.ERole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
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

//TODO: this test should be removed. I used it just for testing JpaRepository functionality with relations
@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class NoteIT {
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        TestUtils testUtils;
        String adminToken, user1Token, user2Token;

        @BeforeAll
        void initTest() throws Exception {
                log.info("initng NoteIT");

                adminToken = testUtils.getRegisteredUserWithToken(ERole.ROLE_ADMIN).getRight();
                user1Token = testUtils.getRegisteredUserWithToken(ERole.ROLE_USER).getRight();
                user2Token = testUtils.getRegisteredUserWithToken(ERole.ROLE_USER).getRight();
        }

        // TODO:I doubt, that it's right way t write all curd tests in one test
        @Test
        void testCrudForNoteController() throws JsonProcessingException, Exception {
                Note note1 = new Note("note1");
                Note note2 = new Note("note2");

                // POST
                String response = mockMvc.perform(
                                post("/api/notes")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token)
                                                .content(objectMapper.writeValueAsString(note1)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.text", is(note1.getText())))
                                .andExpect(jsonPath("$.authorId", IsNot.not(IsNull.nullValue())))
                                .andExpect(jsonPath("$.id", IsNot.not(IsNull.nullValue())))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                NoteDto.NoteResponse createdNote = objectMapper.readValue(response, NoteDto.NoteResponse.class);

                // Second note
                response = mockMvc.perform(
                                post("/api/notes")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token)
                                                .content(objectMapper.writeValueAsString(note2)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.text", is(note2.getText())))
                                .andExpect(jsonPath("$.authorId", IsNot.not(IsNull.nullValue())))
                                .andExpect(jsonPath("$.id", IsNot.not(IsNull.nullValue())))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                // GET
                response = mockMvc.perform(
                                get("/api/notes/" + createdNote.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                NoteDto.NoteResponse recievedNote = objectMapper.readValue(response, NoteDto.NoteResponse.class);
                assertEquals(createdNote, recievedNote);

                response = mockMvc.perform(
                                get("/api/notes/")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                List<NoteDto.NoteResponse> recievedNotes = objectMapper.readValue(response,
                                new TypeReference<List<NoteDto.NoteResponse>>() {
                                });
                assertEquals(2, recievedNotes.size());

                // PUT
                note1.setText(note1.getText() + "updated");
                response = mockMvc.perform(
                                put("/api/notes/" + createdNote.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token)
                                                .content(objectMapper.writeValueAsString(note1)))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                recievedNote = objectMapper.readValue(response, NoteDto.NoteResponse.class);
                assertEquals(note1.getText(), recievedNote.getText(), "Note's text should be updated");
                assertEquals(createdNote.getId(), recievedNote.getId(), "Note's id should stay the same");

                response = mockMvc.perform(
                                get("/api/notes/" + createdNote.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
                recievedNote = objectMapper.readValue(response, NoteDto.NoteResponse.class);
                assertEquals(note1.getText(), recievedNote.getText(), "Note's text should be updated");
                assertEquals(createdNote.getId(), recievedNote.getId(), "Note's id should stay the same");

                // DELETE
                mockMvc.perform(
                                delete("/api/notes/" + createdNote.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token))
                                .andExpect(status().isNoContent());

                // GET
                mockMvc.perform(
                                get("/api/notes/" + createdNote.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token))
                                .andExpect(status().isNotFound());
        }

        @Test
        void noAuthorShouldNotGetNote() throws JsonProcessingException, UnsupportedEncodingException, Exception {
                Note note1 = new Note("note1");
                String response = mockMvc.perform(
                                post("/api/notes")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token)
                                                .content(objectMapper.writeValueAsString(note1)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.text", is(note1.getText())))
                                .andExpect(jsonPath("$.authorId", IsNot.not(IsNull.nullValue())))
                                .andExpect(jsonPath("$.id", IsNot.not(IsNull.nullValue())))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                NoteDto.NoteResponse createdNote = objectMapper.readValue(response, NoteDto.NoteResponse.class);

                mockMvc.perform(
                                get("/api/notes/" + createdNote.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user2Token))
                                .andExpect(status().isForbidden());

                mockMvc.perform(
                                put("/api/notes/" + createdNote.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user2Token)
                                                .content(objectMapper.writeValueAsString(note1)))
                                .andExpect(status().isForbidden());
        }

        @Test
        void noAuthorAdminShouldHaveAccesToNotes()
                        throws JsonProcessingException, UnsupportedEncodingException, Exception {
                Note note1 = new Note("note1");
                String response = mockMvc.perform(
                                post("/api/notes")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + user1Token)
                                                .content(objectMapper.writeValueAsString(note1)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.text", is(note1.getText())))
                                .andExpect(jsonPath("$.authorId", IsNot.not(IsNull.nullValue())))
                                .andExpect(jsonPath("$.id", IsNot.not(IsNull.nullValue())))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                NoteDto.NoteResponse createdNote = objectMapper.readValue(response, NoteDto.NoteResponse.class);

                mockMvc.perform(
                                get("/api/notes/" + createdNote.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(status().isOk());

                mockMvc.perform(
                                put("/api/notes/" + createdNote.getId())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Bearer " + adminToken)
                                                .content(objectMapper.writeValueAsString(note1)))
                                .andExpect(status().isForbidden());
        }
}
