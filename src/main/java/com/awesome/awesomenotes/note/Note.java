package com.awesome.awesomenotes.note;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import com.awesome.awesomenotes.label.Label;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE }, fetch = FetchType.LAZY)
    @JoinTable(name = "note_label", joinColumns = @JoinColumn(name = "note_id"), inverseJoinColumns = @JoinColumn(name = "label_id"))
    private Set<Label> labels = new HashSet<>();

    public void addLabel(Label label) {
        getLabels().add(label);
        // label.getNotes().add(this);
    }

    public void removeLabel(Label label) {
        getLabels().remove(label);
        // label.getNotes().remove(this);
    }

    public Note(String text, User author) {
        this.text = text;
        this.author = author;
    }

    public Note(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        String authorS = "";
        String labelsS = "";
        try {
            authorS = "" + getAuthor().getId();
            authorS += getAuthor().getUsername();
        } catch (Exception e) {

        }
        try {
            labelsS = "" + getLabels().size();
        } catch (Exception e) {

        }
        return "{" +
                " id='" + getId() + "'" +
                ", text='" + getText() + "'" +
                ", author='" + authorS + "'" +
                ", labels='" + labelsS + "'" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Note)) {
            return false;
        }
        Note note = (Note) o;
        return Objects.equals(id, note.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
