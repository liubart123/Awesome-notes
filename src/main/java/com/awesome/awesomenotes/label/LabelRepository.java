package com.awesome.awesomenotes.label;

import java.util.List;
import java.util.Optional;

import com.awesome.awesomenotes.logging.DontLogReturn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    @DontLogReturn
    List<Label> findByIdInAndAuthor_id(Iterable<Long> id, Long authorId);

    @Query("SELECT distinct l FROM Label l JOIN FETCH l.author a WHERE a.id = (:authorId)")
    @DontLogReturn
    List<Label> findByAuthorId(Long authorId);

    @Query("SELECT distinct l FROM Label l JOIN FETCH l.notes n JOIN FETCH n.labels l2 JOIN FETCH l.author a WHERE l.id = (:id)")
    @DontLogReturn
    Optional<Label> findByIdWithNotesAndTheirLabels(Long id);
}
