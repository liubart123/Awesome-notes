package com.awesome.awesomenotes.note;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.awesome.awesomenotes.exception.ElementNotFoundException;
import com.awesome.awesomenotes.exception.LackOfPermissionsException;
import com.awesome.awesomenotes.label.Label;
import com.awesome.awesomenotes.label.LabelRepository;
import com.awesome.awesomenotes.logging.DontLogReturn;
import com.awesome.awesomenotes.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoteService {
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    LabelRepository labelRepository;
    @Autowired
    UserRepository userRepository;

    final String NOT_FOUND = "Note wasn't found";
    final String WRONG_AUTHOR = "Wrong author id";

    @DontLogReturn
    @Transactional(readOnly = true)
    public List<Note> getByAuthorId(Long authorId) {
        return noteRepository.findByAuthorIdWithLabels(authorId);
    }

    @DontLogReturn
    @Transactional(readOnly = true)
    public Note getByIdWithLabels(Long id, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        Optional<Note> note = noteRepository.findById(id);
        if (!note.isPresent()) {
            throw new ElementNotFoundException(NOT_FOUND);
        } else if (authorId != null && note.get().getAuthor().getId() != authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        note.get().getLabels().size();
        return note.get();
    }

    @DontLogReturn
    @Transactional
    public void delete(Long id, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        Optional<Note> note = noteRepository.findById(id);
        if (!note.isPresent()) {
            throw new ElementNotFoundException(NOT_FOUND);
        } else if (authorId != null && note.get().getAuthor().getId() != authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        noteRepository.delete(note.get());
    }

    @DontLogReturn
    @Transactional
    public Note create(Note note, Long authorId) {
        note.setId(null);

        if (authorId != null) {
            note.setAuthor(userRepository.getById(authorId));
        }

        List<Long> ids = new ArrayList<>();
        for (Label label : note.getLabels()) {
            ids.add(label.getId());
        }

        note.setLabels(new HashSet<>(labelRepository.findByIdInAndAuthor_id(ids, note.getAuthor().getId())));
        return noteRepository.save(note);
    }

    @DontLogReturn
    @Transactional
    public Note update(Note note, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        Optional<Note> found = noteRepository.findById(note.getId());
        if (!found.isPresent()) {
            throw new ElementNotFoundException(NOT_FOUND);
        } else if (authorId != null && found.get().getAuthor().getId() != authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        if (note.getAuthor() == null) {
            note.setAuthor(found.get().getAuthor());
        }
        List<Long> ids = new ArrayList<>();
        for (Label label : note.getLabels()) {
            ids.add(label.getId());
        }

        note.setLabels(new HashSet<>(labelRepository.findByIdInAndAuthor_id(ids, found.get().getAuthor().getId())));
        return noteRepository.save(note);
    }
}
