package com.awesome.awesomenotes.label;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.awesome.awesomenotes.exception.ElementNotFoundException;
import com.awesome.awesomenotes.exception.LackOfPermissionsException;
import com.awesome.awesomenotes.logging.DontLogReturn;
import com.awesome.awesomenotes.note.NoteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LabelService {
    @Autowired
    LabelRepository labelRepository;
    @Autowired
    NoteRepository noteRepository;

    final String NOT_FOUND = "Label wasn't found";
    final String WRONG_AUTHOR = "Wrong author id";

    @Transactional
    @DontLogReturn
    public Label create(Label label) {
        label.setId(null);
        List<Long> ids = new ArrayList<>();
        for (var note : label.getNotes()) {
            ids.add(note.getId());
        }
        label.setNotes(new HashSet<>(noteRepository.findByIdInAndAuthor_id(ids, label.getAuthor().getId())));
        return labelRepository.save(label);
    }

    @Transactional
    @DontLogReturn
    public Label update(Label label, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        Label dbLabel = labelRepository.findById(label.getId()).get();
        if (dbLabel == null)
            throw new ElementNotFoundException(NOT_FOUND);
        else if (authorId != null && label.getAuthor().getId() == authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        List<Long> ids = new ArrayList<>();
        for (var note : label.getNotes()) {
            ids.add(note.getId());
        }
        label.setNotes(new HashSet<>(noteRepository.findByIdInAndAuthor_id(ids, label.getAuthor().getId())));

        return labelRepository.save(label);
    }

    @Transactional
    @DontLogReturn
    public void delete(Long id, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        Label label = labelRepository.findById(id).get();
        if (label == null)
            throw new ElementNotFoundException(NOT_FOUND);
        else if (authorId != null && label.getAuthor().getId() == authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        labelRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @DontLogReturn
    public Label getById(Long id, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        Label label = labelRepository.findById(id).get();
        if (label == null) {
            throw new ElementNotFoundException(NOT_FOUND);
        } else if (authorId != null && label.getAuthor().getId() == authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        return label;
    }

    @Transactional(readOnly = true)
    @DontLogReturn
    public Label getByIdWithNotes(Long id, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        Label label = labelRepository.findById(id).get();
        if (label == null) {
            throw new ElementNotFoundException(NOT_FOUND);
        } else if (authorId != null && label.getAuthor().getId() == authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        label.getNotes().size();
        return label;
    }

    @Transactional(readOnly = true)
    @DontLogReturn
    public List<Label> getByAuthorId(Long authorId) {
        return labelRepository.findByAuthorId(authorId);
    }
}
