package com.awesome.awesomenotes.label;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.awesome.awesomenotes.authentication.AuthService;
import com.awesome.awesomenotes.exception.ElementNotFoundException;
import com.awesome.awesomenotes.exception.LackOfPermissionsException;
import com.awesome.awesomenotes.label.LabelDto.LabelCreateRequest;
import com.awesome.awesomenotes.label.LabelDto.LabelResposnse;
import com.awesome.awesomenotes.label.LabelDto.LabelResposnseWithoutNotes;
import com.awesome.awesomenotes.label.LabelDto.LabelUpdateRequest;
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
import org.springframework.web.bind.annotation.RestController;

import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestController
@RequestMapping("/api/labels")
public class LabelController {
    @Autowired
    LabelService labelService;
    @Autowired
    LabelConverter labelConverter;
    @Autowired
    LabelNoteConverter labelNoteConverter;

    @GetMapping(path = "/")
    public List<LabelResposnseWithoutNotes> getAllLabels(
            @RequestAttribute(name = "user") User authorizedUser) {
        return labelService.getByAuthorId(authorizedUser.getId())
                .stream()
                .map((label) -> labelConverter.convertWithoutNotes(label))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}")
    public LabelResposnse getMethodName(@PathVariable(name = "id") Long id,
            @RequestAttribute(name = "user") User authorizedUser)
            throws ElementNotFoundException, LackOfPermissionsException {
        Label found;
        if (AuthService.doesUserContainAnyRole(authorizedUser, ERole.ROLE_ADMIN))
            found = labelService.getByIdWithNotes(id, null);
        else
            found = labelService.getByIdWithNotes(id, authorizedUser.getId());
        return labelNoteConverter.convert(found);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResposnseWithoutNotes createLabel(@Valid @RequestBody LabelCreateRequest dto,
            @RequestAttribute(name = "user") User authorizedUser) {
        return labelConverter.convertWithoutNotes(
                labelService.create(
                        labelConverter.convert(dto), authorizedUser.getId()));
    }

    @PutMapping(path = "/{id}")
    public LabelResposnseWithoutNotes updateLabel(@PathVariable(name = "id") Long id,
            @Valid @RequestBody LabelUpdateRequest dto,
            @RequestAttribute(name = "user") User authorizedUser)
            throws ElementNotFoundException, LackOfPermissionsException {
        return labelConverter.convertWithoutNotes(
                labelService.update(
                        labelConverter.convert(dto, id),
                        authorizedUser.getId()));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable(name = "id") Long id,
            @RequestAttribute(name = "user") User authorizedUser)
            throws LackOfPermissionsException, ElementNotFoundException {
        labelService.delete(id, authorizedUser.getId());
    }
}
