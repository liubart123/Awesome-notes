package com.awesome.awesomenotes.label;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.awesome.awesomenotes.note.Note;
import com.awesome.awesomenotes.user.User;

import lombok.*;

@Entity
@Table(name = "labels")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String name;
    @ManyToOne(fetch = FetchType.LAZY)
    User author;
    @ManyToMany(mappedBy = "labels", fetch = FetchType.LAZY)
    Set<Note> notes = new HashSet<>();

    public void addNote(Note note) {
        getNotes().add(note);
        // note.getLabels().add(this);
    }

    public void removeNote(Note note) {
        getNotes().remove(note);
        // note.getLabels().remove(this);
    }

    @Override
    public String toString() {
        String authorS = "";
        String notesS = "";
        try {
            authorS = "" + getAuthor().getId();
            authorS += getAuthor().getUsername();
        } catch (Exception e) {

        }
        try {
            notesS = "" + getNotes().size();
        } catch (Exception e) {

        }
        return "{" +
                " id='" + getId() + "'" +
                ", name='" + getName() + "'" +
                ", author='" + authorS + "'" +
                ", notes='" + notesS + "'" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Label)) {
            return false;
        }
        Label label = (Label) o;
        return Objects.equals(id, label.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
