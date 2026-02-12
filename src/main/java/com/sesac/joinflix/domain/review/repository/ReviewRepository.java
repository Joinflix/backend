package com.sesac.joinflix.domain.review.repository;

import com.sesac.joinflix.domain.review.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r where r.user.id = :userId and r.movie.id = :movieId")
    Optional<Review> findByUserIdAndMovieId(@Param("userId") Long userId, @Param("movieId") Long movieId);

    @Query("select r from Review r join fetch r.user join fetch r.movie where r.id = :reviewId")
    Optional<Review> findByIdWithUserAndMovie(@Param("reviewId") Long reviewId);

    @Query("select r from Review r join fetch r.user join fetch r.movie where r.movie.id = :movieId and r.id < :cursorId order by r.id desc")
    Slice<Review> findReviewsByMovieId(@Param("movieId") Long movieId, @Param("cursorId") Long cursorId, Pageable pageable);

    @Query("select r from Review r join fetch r.user join fetch r.movie where r.user.id = :userId and r.id < :cursorId order by r.id desc")
    Slice<Review> findReviewsByUserId(@Param("userId") Long userId, @Param("cursorId") Long cursorId, Pageable pageable);

    //coalesce(..., 0): 리뷰가 하나도 없을 경우 null 대신 정수 0을 반환.
    @Query("select coalesce(cast(avg(r.starRating) as int), 0) from Review r where r.movie.id = :movieId")
    Integer findAverageRatingByMovieId(@Param("movieId") Long movieId);

}
