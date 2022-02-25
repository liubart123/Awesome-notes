package com.awesome.awesomenotes.note;

import com.awesome.awesomenotes.note.NoteDto.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Setter
@Getter
public class NoteConverter {
    @Autowired
    ModelMapper modelMapper;

    public Note convert(NoteCreateRequest dto) {
        return modelMapper.map(dto, Note.class);
    }

    public Note convert(NoteUpdateRequest dto) {
        return modelMapper.map(dto, Note.class);
    }

    public NoteResponse convert(Note note) {
        NoteResponse dto = modelMapper.map(note, NoteResponse.class);
        if (note.getAuthor() != null)
            dto.setAuthorId(note.getAuthor().getId());
        return dto;
    }
}
