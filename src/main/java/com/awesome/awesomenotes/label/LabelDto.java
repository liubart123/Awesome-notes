package com.awesome.awesomenotes.label;

import java.util.HashSet;
import java.util.Set;

import com.awesome.awesomenotes.note.Note;
import com.awesome.awesomenotes.note.NoteDto;
import com.awesome.awesomenotes.note.NoteDto.NoteResponse;

import lombok.*;

public class LabelDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LabelResposnseWithoutNotes {
        private Long id;
        String name;
        Long authorId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
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
    public static class LabelCreateRequest {
        String name;
        Long authorId;
        Set<Long> notes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LabelUpdateRequest {
        String name;
        Long authorId;
        Set<Long> notes;
    }
}
