package com.awesome.awesomenotes.label;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.awesome.awesomenotes.exception.ElementNotFoundException;
import com.awesome.awesomenotes.exception.LackOfPermissionsException;
import com.awesome.awesomenotes.logging.DontLogReturn;
import com.awesome.awesomenotes.note.NoteRepository;
import com.awesome.awesomenotes.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LabelService {
    @Autowired
    LabelRepository labelRepository;
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    UserRepository userRepository;

    final String NOT_FOUND = "Label wasn't found";
    final String WRONG_AUTHOR = "Wrong author id";

    @Transactional
    @DontLogReturn
    public Label create(Label label, Long authorId) {
        label.setId(null);

        if (authorId != null) {
            label.setAuthor(userRepository.getById(authorId));
        }
        label.setNotes(new HashSet<>());
        label = labelRepository.save(label);
        return label;
    }

    @Transactional
    @DontLogReturn
    public Label update(Label label, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        Optional<Label> dbLabel = labelRepository.findById(label.getId());
        if (!dbLabel.isPresent())
            throw new ElementNotFoundException(NOT_FOUND);
        else if (authorId != null && dbLabel.get().getAuthor().getId() != authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        if (label.getAuthor() == null) {
            label.setAuthor(dbLabel.get().getAuthor());
        }
        label.setNotes(new HashSet<>());
        label = labelRepository.save(label);
        return label;
    }

    @Transactional
    @DontLogReturn
    public void delete(Long id, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        Optional<Label> label = labelRepository.findById(id);
        if (!label.isPresent())
            throw new ElementNotFoundException(NOT_FOUND);
        else if (authorId != null && label.get().getAuthor().getId() != authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        labelRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @DontLogReturn
    public Label getById(Long id, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        Optional<Label> label = labelRepository.findById(id);
        if (!label.isPresent()) {
            throw new ElementNotFoundException(NOT_FOUND);
        } else if (authorId != null && label.get().getAuthor().getId() != authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        return label.get();
    }

    @Transactional(readOnly = true)
    @DontLogReturn
    public Label getByIdWithNotes(Long id, Long authorId) throws ElementNotFoundException, LackOfPermissionsException {
        Optional<Label> label = labelRepository.findByIdWithNotesAndTheirLabels(id);
        if (!label.isPresent()) {
            throw new ElementNotFoundException(NOT_FOUND);
        } else if (authorId != null && label.get().getAuthor().getId() != authorId) {
            throw new LackOfPermissionsException(WRONG_AUTHOR);
        }
        return label.get();
    }

    @Transactional(readOnly = true)
    @DontLogReturn
    public List<Label> getByAuthorId(Long authorId) {
        return labelRepository.findByAuthorId(authorId);
    }
}
