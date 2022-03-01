package com.awesome.awesomenotes.note;

import java.util.stream.Collectors;

import com.awesome.awesomenotes.label.Label;
import com.awesome.awesomenotes.label.LabelConverter;
import com.awesome.awesomenotes.note.NoteDto.NoteCreateRequest;
import com.awesome.awesomenotes.note.NoteDto.NoteResponse;
import com.awesome.awesomenotes.note.NoteDto.NoteUpdateRequest;

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
    @Autowired
    LabelConverter labelConverter;

    public Note convert(NoteCreateRequest dto) {
        Note note = modelMapper.map(dto, Note.class);
        if (dto.getLabelIds() != null) {
            note.setLabels(
                    dto.getLabelIds().stream()
                            .map(id -> new Label(id, null, null, null))
                            .collect(Collectors.toSet()));
        }
        return note;
    }

    public Note convert(NoteUpdateRequest dto, Long noteId) {
        Note note = modelMapper.map(dto, Note.class);
        if (dto.getLabelIds() != null) {
            note.setLabels(
                    dto.getLabelIds().stream()
                            .map(id -> new Label(id, null, null, null))
                            .collect(Collectors.toSet()));
        }
        note.setId(noteId);
        return note;
    }

    public NoteResponse convert(Note note) {
        NoteResponse dto = modelMapper.map(note, NoteResponse.class);
        if (note.getAuthor() != null)
            dto.setAuthorId(note.getAuthor().getId());
        if (note.getLabels() != null) {
            dto.setLabels(
                    note.getLabels()
                            .stream()
                            .map((label) -> labelConverter.convertWithoutNotes(label))
                            .collect(Collectors.toSet()));
        }
        return dto;
    }
}
