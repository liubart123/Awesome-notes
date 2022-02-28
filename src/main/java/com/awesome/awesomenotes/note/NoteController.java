package com.awesome.awesomenotes.note;

import java.util.List;

import javax.validation.Valid;

import com.awesome.awesomenotes.authentication.AuthService;
import com.awesome.awesomenotes.exception.ElementNotFoundException;
import com.awesome.awesomenotes.exception.LackOfPermissionsException;
import com.awesome.awesomenotes.user.User;
import com.awesome.awesomenotes.user.role.ERole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestController
@RequestMapping("/api/notes")
public class NoteController {
    @Autowired
    NoteConverter noteConverter;

    // @GetMapping(path = "/")
    // public List<NoteDto.NoteResponse> getAllNotes(
    // @RequestAttribute(name = "user") User authorizedUser)
    // throws ElementNotFoundException, LackOfPermissionsException {
    // return noteService
    // .getNotesByAuthor(authorizedUser.getId())
    // .stream()
    // .map(note -> noteConverter.convert(note))
    // .toList();
    // }

    // @GetMapping(path = "/{id}")
    // public NoteDto.NoteResponse getNote(@PathVariable(name = "id") Long id,
    // @RequestAttribute(name = "user") User authorizedUser)
    // throws ElementNotFoundException, LackOfPermissionsException {
    // Note foundNote;
    // if (AuthService.doesUserContainAnyRole(authorizedUser, ERole.ROLE_ADMIN))
    // foundNote = noteService.getNote(id);
    // else
    // foundNote = noteService.getNoteByAuthor(id, authorizedUser.getId());
    // return noteConverter.convert(foundNote);
    // }

    // @PostMapping
    // @ResponseStatus(HttpStatus.CREATED)
    // public NoteDto.NoteResponse createNote(@Valid @RequestBody
    // NoteDto.NoteCreateRequest dto,
    // @RequestAttribute(name = "user") User authorizedUser) {
    // return
    // noteConverter.convert(noteService.createNote(noteConverter.convert(dto),
    // authorizedUser.getId()));
    // }

    // @PutMapping(path = "/{id}")
    // public NoteDto.NoteResponse updateNote(@PathVariable(name = "id") Long id,
    // @Valid @RequestBody NoteDto.NoteUpdateRequest dto,
    // @RequestAttribute(name = "user") User authorizedUser)
    // throws LackOfPermissionsException, ElementNotFoundException {
    // return noteConverter
    // .convert(noteService.updateNoteByAuthor(noteConverter.convert(dto), id,
    // authorizedUser.getId()));
    // }

    // @DeleteMapping(path = "/{id}")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    // public void deleteNote(@PathVariable(name = "id") Long id,
    // @RequestAttribute(name = "user") User authorizedUser)
    // throws LackOfPermissionsException, ElementNotFoundException {
    // noteService.deleteNoteByAuthor(id, authorizedUser.getId());
    // }
}
