package com.awesome.awesomenotes.note;

import java.util.List;

import com.awesome.awesomenotes.logging.DontLogReturn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    @DontLogReturn
    List<Note> findByIdInAndAuthor_id(Iterable<Long> id, Long authorId);

    @Query("SELECT distinct n FROM Note n LEFT JOIN FETCH n.labels l JOIN FETCH n.author a WHERE a.id = (:authorId)")
    @DontLogReturn
    List<Note> findByAuthorIdWithLabels(@Param("authorId") Long authorId);
}
