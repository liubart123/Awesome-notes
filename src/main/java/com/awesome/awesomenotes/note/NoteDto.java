package com.awesome.awesomenotes.note;

import lombok.*;

public class NoteDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class NoteResponse {
        private Long id;
        private String text;
        private Long authorId;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteCreateRequest {
        private String text;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteUpdateRequest {
        private String text;
    }
}
