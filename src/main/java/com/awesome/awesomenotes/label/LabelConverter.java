package com.awesome.awesomenotes.label;

import com.awesome.awesomenotes.label.LabelDto.LabelCreateRequest;
import com.awesome.awesomenotes.label.LabelDto.LabelResposnseWithoutNotes;
import com.awesome.awesomenotes.label.LabelDto.LabelUpdateRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Setter
@Getter
public class LabelConverter {

    @Autowired
    ModelMapper modelMapper;

    public LabelResposnseWithoutNotes convertWithoutNotes(Label label) {
        LabelResposnseWithoutNotes dto = modelMapper.map(label, LabelResposnseWithoutNotes.class);
        if (label.getAuthor() != null)
            dto.setAuthorId(label.getAuthor().getId());
        return dto;
    }

    public Label convert(LabelCreateRequest dto) {
        Label label = modelMapper.map(dto, Label.class);

        return label;
    }

    public Label convert(LabelUpdateRequest dto, Long id) {
        Label label = modelMapper.map(dto, Label.class);

        label.setId(id);
        return label;
    }
}
