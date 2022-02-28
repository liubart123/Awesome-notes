package com.awesome.awesomenotes.note;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.awesome.awesomenotes.exception.ElementNotFoundException;
import com.awesome.awesomenotes.exception.LackOfPermissionsException;
import com.awesome.awesomenotes.label.Label;
import com.awesome.awesomenotes.label.LabelRepository;
import com.awesome.awesomenotes.logging.DontLogReturn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoteService {
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    LabelRepository labelRepository;

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
        Note note = noteRepository.findById(id).get();
        if (note == null) {
            throw new ElementNotFoundException(NOT_FOUND);
        } else if (authorId != null && note.getAuthor().getId() == authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        note.getLabels().size();
        return note;
    }

    @DontLogReturn
    @Transactional
    public void delete(Long id, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        Note note = noteRepository.findById(id).get();
        if (note == null) {
            throw new ElementNotFoundException(NOT_FOUND);
        } else if (authorId != null && note.getAuthor().getId() == authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        noteRepository.delete(note);
    }

    @DontLogReturn
    @Transactional
    public Note create(Note note) {
        note.setId(null);

        List<Long> ids = new ArrayList<>();
        for (var label : note.getLabels()) {
            ids.add(label.getId());
        }

        note.setLabels(new HashSet<>(labelRepository.findByIdInAndAuthor_id(ids, note.getAuthor().getId())));
        return noteRepository.save(note);
    }

    @DontLogReturn
    @Transactional
    public Note update(Note note, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        if (noteRepository.findById(note.getId()).isPresent() == false) {
            throw new ElementNotFoundException(NOT_FOUND);
        } else if (authorId != null && note.getAuthor().getId() == authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        List<Long> ids = new ArrayList<>();
        for (var label : note.getLabels()) {
            ids.add(label.getId());
        }

        note.setLabels(new HashSet<>(labelRepository.findByIdInAndAuthor_id(ids, note.getAuthor().getId())));
        return noteRepository.save(note);
    }
}
