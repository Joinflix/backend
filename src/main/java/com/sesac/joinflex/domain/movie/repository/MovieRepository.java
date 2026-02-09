package com.sesac.joinflex.domain.movie.repository;

import com.sesac.joinflex.domain.movie.entity.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("select m from Movie m where m.id < :cursorId order by m.id desc")
    Slice<Movie> findMoviesByCursor(@Param("cursorId") Long cursorId, Pageable pageable);
}
