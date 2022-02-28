package com.awesome.awesomenotes.label;

import java.util.stream.Collectors;

import com.awesome.awesomenotes.label.LabelDto.LabelResposnse;
import com.awesome.awesomenotes.note.NoteConverter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LabelNoteConverter {
    @Autowired
    LabelConverter labelConverter;
    @Autowired
    NoteConverter noteConverter;
    @Autowired
    ModelMapper modelMapper;

    public LabelResposnse convert(Label label) {
        LabelResposnse dto = modelMapper.map(label, LabelResposnse.class);
        if (label.getAuthor() != null)
            dto.setAuthorId(label.getAuthor().getId());
        dto.setNotes(label.getNotes()
                .stream()
                .map((note) -> noteConverter.convert(note))
                .collect(Collectors.toSet()));
        return dto;
    }
}
