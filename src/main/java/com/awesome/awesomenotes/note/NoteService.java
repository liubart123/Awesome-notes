package com.awesome.awesomenotes.note;

import java.util.List;

import javax.transaction.Transactional;

import com.awesome.awesomenotes.exception.ElementNotFoundException;
import com.awesome.awesomenotes.exception.LackOfPermissionsException;
import com.awesome.awesomenotes.user.User;
import com.awesome.awesomenotes.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.*;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteService {
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    UserRepository userRepository;

    public Note createNote(Note note, Long authorId) {
        User author = userRepository.getById(authorId);
        note.setAuthor(author);
        note.setId(null);
        return noteRepository.save(note);
    }

    public Note updateNoteByAuthor(Note note, Long noteId, Long authorId)
            throws LackOfPermissionsException, ElementNotFoundException {
        if (noteRepository.findByIdAndFetchAuthor(noteId)
                .orElseThrow(
                        () -> new ElementNotFoundException("Note not found with id: " + noteId))
                .getAuthor()
                .getId() != authorId)
            throw new LackOfPermissionsException("provided user isn't author of the note");
        note.setId(noteId);
        note.setAuthor(userRepository.getById(authorId));
        return noteRepository.save(note);
    }

    public List<Note> getNotesByAuthor(Long authorId) {
        User author = userRepository.getById(authorId);
        return noteRepository.findByAuthor(author);
    }

    public Note getNote(Long id) throws ElementNotFoundException {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Note not found with id: " + id));
    }

    @Transactional
    public Note getNoteByAuthor(Long id, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        Note note = noteRepository.findByIdAndFetchAuthor(id)
                .orElseThrow(() -> new ElementNotFoundException("Note not found with id: " + id));
        if (note.getAuthor().getId() != authorId)
            throw new LackOfPermissionsException("provided user isn't author of the note");
        return note;
    }

    public void deleteNote(Long id) {
        noteRepository.deleteById(id);
    }

    public void deleteNoteByAuthor(Long noteId, Long authorId)
            throws LackOfPermissionsException, ElementNotFoundException {
        if (noteRepository.findByIdAndFetchAuthor(noteId)
                .orElseThrow(
                        () -> new ElementNotFoundException("Note not found with id: " + noteId))
                .getAuthor()
                .getId() != authorId)
            throw new LackOfPermissionsException("provided user isn't author of the note");
        noteRepository.deleteById(noteId);
    }
}
