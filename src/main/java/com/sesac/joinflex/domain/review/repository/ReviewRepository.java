package com.sesac.joinflex.domain.review.repository;

import com.sesac.joinflex.domain.review.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByUserIdAndMovieId(Long userId, Long movieId);

    @Query("select r from Review r where r.movie.id = :movieId and r.id < :cursorId order by r.id desc")
    Slice<Review> findReviewsByMovieId(@Param("movieId") Long movieId, @Param("cursorId") Long cursorId, Pageable pageable);

    @Query("select r from Review r where r.user.id = :userId and r.id < :cursorId order by r.id desc")
    Slice<Review> findReviewsByUserId(@Param("userId") Long userId, @Param("cursorId") Long cursorId, Pageable pageable);

}
