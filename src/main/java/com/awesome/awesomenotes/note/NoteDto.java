package com.awesome.awesomenotes.note;

import java.util.Set;

import com.awesome.awesomenotes.label.LabelDto.LabelResposnseWithoutNotes;

import lombok.*;

public class NoteDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteResponse {
        private Long id;
        private String text;
        private Long authorId;
        private Set<LabelResposnseWithoutNotes> labels;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteCreateRequest {
        private String text;
        private Set<Long> labelIds;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteUpdateRequest {
        private String text;
        private Set<Long> labelIds;
    }
}
