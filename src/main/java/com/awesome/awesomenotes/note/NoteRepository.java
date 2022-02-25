package com.awesome.awesomenotes.note;

import java.util.List;
import java.util.Optional;

import com.awesome.awesomenotes.user.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByAuthor(User author);

    Optional<Note> findByIdAndAuthor(Long id, User author);

    @Query("SELECT n FROM Note n JOIN FETCH n.author WHERE n.id = (:id)")
    Optional<Note> findByIdAndFetchAuthor(@Param("id") Long id);
}
