package com.awesome.awesomenotes.Integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.awesome.awesomenotes.exception.ElementNotFoundException;
import com.awesome.awesomenotes.exception.LackOfPermissionsException;
import com.awesome.awesomenotes.label.Label;
import com.awesome.awesomenotes.label.LabelRepository;
import com.awesome.awesomenotes.label.LabelService;
import com.awesome.awesomenotes.note.Note;
import com.awesome.awesomenotes.note.NoteRepository;
import com.awesome.awesomenotes.note.NoteService;
import com.awesome.awesomenotes.user.User;
import com.awesome.awesomenotes.user.UserRepository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class RepositoriesIT {
    @Autowired
    LabelRepository labelRepository;

    @Autowired
    LabelService labelService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    NoteService noteService;

    User user1, user2;

    final Integer labelsCount = 5;
    final Integer notesCount = 20;
    List<Long> labelIds = new ArrayList<>();
    List<Long> noteIds = new ArrayList<>();

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @BeforeAll
    // @Transactional
    void initTests() {
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                user1 = userRepository.save(new User(null, "user1", "user1.asd@ff.a", "password", null));
                user2 = userRepository.save(new User(null, "user2", "user2.asd@ff.a", "password", null));

                for (Integer i = 0; i < labelsCount; i++) {
                    labelIds.add(
                            labelRepository
                                    .save(new Label(null, "label" + i, user1, new HashSet<>()))
                                    .getId());
                }

                List<Note> notes = new ArrayList<>();
                for (Integer i = 0; i < notesCount; i++) {
                    Note note = noteRepository.save(new Note(null, "note" + i, user1, new HashSet<>()));
                    for (Integer j = 0; j < labelsCount; j++) {
                        Label l = labelRepository.findById(labelIds.get(j)).get();
                        if (l != null)
                            note.addLabel(l);
                    }
                    notes.add(note);
                    noteIds.add(note.getId());
                }

                for (Integer i = 0; i < notesCount; i++) {
                    notes.set(i, noteRepository.save(notes.get(i)));
                }
            }
        });

    }

    @Test
    void testBehaviourOfLabelsDuringOperationsWIthRElatedNote()
            throws ElementNotFoundException, LackOfPermissionsException {
        Integer noteId = 0;
        Integer labelId = 0;
        // create note with labels -> check labels
        // given
        Label label = labelService.getByIdWithNotes(labelIds.get(labelId), null);
        Integer notesOfLabelBeforeCreation = label.getNotes().size();
        Note note = new Note("added note", user1);
        note.getLabels().add(new Label(label.getId(), null, null, null));
        // when
        note = noteService.create(note);
        // then
        // check note's label
        assertEquals(1, note.getLabels().size());
        assertEquals(label.getId(), note.getLabels().stream().findFirst().get().getId());
        note = noteService.getByIdWithLabels(note.getId(), null);
        assertEquals(1, note.getLabels().size());
        assertEquals(label.getId(), note.getLabels().stream().findFirst().get().getId());
        // check label's note
        label = labelService.getByIdWithNotes(noteIds.get(noteId), null);
        assertEquals(notesOfLabelBeforeCreation + 1, label.getNotes().size());

        // update note with labels (add and remove labels) -> check labels
        // given
        note = noteService.getByIdWithLabels(note.getId(), null);
        Label removedLabel = note.getLabels().stream().findFirst().get();
        removedLabel = labelService.getByIdWithNotes(removedLabel.getId(), null);
        note.getLabels().clear();
        labelId++;
        label = labelService.getByIdWithNotes(labelIds.get(labelId), null);
        Integer notesOfRemovedLabelBeforeUpdate = removedLabel.getNotes().size();
        Integer notesOfAddedLabelBeforeUpdate = label.getNotes().size();
        note.addLabel(new Label(label.getId(), null, null, null));
        // when
        note = noteService.update(note, null);
        // then
        // check note
        assertEquals(1, note.getLabels().size());
        assertEquals(label.getId(), note.getLabels().stream().findFirst().get().getId());
        note = noteService.getByIdWithLabels(note.getId(), null);
        assertEquals(1, note.getLabels().size());
        assertEquals(label.getId(), note.getLabels().stream().findFirst().get().getId());
        // check labels
        removedLabel = labelService.getByIdWithNotes(removedLabel.getId(), null);
        assertEquals(notesOfRemovedLabelBeforeUpdate - 1, removedLabel.getNotes().size());
        label = labelService.getByIdWithNotes(label.getId(), null);
        assertEquals(notesOfAddedLabelBeforeUpdate + 1, label.getNotes().size());

        // delete note -> check label's notes
        // given
        note = noteService.getByIdWithLabels(note.getId(), null);
        label = note.getLabels().stream().findFirst().get();
        label = labelService.getByIdWithNotes(label.getId(), null);
        Integer notesOfLabelBeforeDeletion = label.getNotes().size();
        // when
        noteService.delete(note.getId(), null);
        // then
        label = labelService.getByIdWithNotes(label.getId(), null);
        assertEquals(notesOfLabelBeforeDeletion - 1, label.getNotes().size());
    }

    @Test
    void testNoteCreationShouldNotChangeLabelsData() throws ElementNotFoundException, LackOfPermissionsException {
        // given
        Note note = new Note(null, "text", user1, new HashSet<>());
        Label label = labelService.getByIdWithNotes(labelIds.get(0), null);
        Label savedLabel = new Label(label.getId(), "updated", user2, new HashSet<>());
        note.addLabel(savedLabel);
        // when
        note = noteService.create(note);
        // then
        assertEquals(1, note.getLabels().size());
        Label actualLabel = labelService.getByIdWithNotes(labelIds.get(0), null);
        assertEquals(label.getNotes().size() + 1, actualLabel.getNotes().size());
        assertEquals(label.getName(), actualLabel.getName());
        assertEquals(label.getAuthor().getId(), actualLabel.getAuthor().getId());
    }

    @Test
    void testNoteCreationShouldNotCreateNonExistentLabel() throws ElementNotFoundException, LackOfPermissionsException {
        // given
        Note note = new Note(null, "text", user1, new HashSet<>());
        Label savedLabel1 = new Label(null, "saved", user1, new HashSet<>());
        Label savedLabel2 = new Label(-123123123L, "saved", user1, new HashSet<>());
        note.addLabel(savedLabel1);
        note.addLabel(savedLabel2);
        // when
        note = noteService.create(note);
        // then
        assertEquals(0, note.getLabels().size());
    }

    @Test
    void testNoteCreationShouldNotAddLabelOfOtherAuthor() throws ElementNotFoundException, LackOfPermissionsException {
        // given
        Note note = new Note(null, "text", user1, new HashSet<>());
        Label savedLabel = labelService.create(new Label(null, "saved", user2, new HashSet<>()));
        note.addLabel(savedLabel);
        // when
        note = noteService.create(note);
        // then
        assertEquals(0, note.getLabels().size());
        Label actualLabel = labelService.getByIdWithNotes(savedLabel.getId(), null);
        assertEquals(0, actualLabel.getNotes().size());
    }
}
