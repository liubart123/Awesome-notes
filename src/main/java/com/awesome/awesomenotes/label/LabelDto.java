package com.awesome.awesomenotes.label;

import java.util.Set;

import com.awesome.awesomenotes.note.NoteDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class LabelDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class LabelResposnseWithoutNotes {
        private Long id;
        String name;
        Long authorId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class LabelResposnse {
        private Long id;
        String name;
        Long authorId;
        Set<NoteDto.NoteResponse> notes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class LabelCreateRequest {
        String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class LabelUpdateRequest {
        String name;
    }
}
