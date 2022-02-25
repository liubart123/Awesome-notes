package com.awesome.awesomenotes.note;

import javax.persistence.*;

import com.awesome.awesomenotes.user.User;

import lombok.*;

@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    // @Transient
    // private Long authorId;

    public Note(String text, User author) {
        this.text = text;
        this.author = author;
    }

    public Note(String text) {
        this.text = text;
    }

}
