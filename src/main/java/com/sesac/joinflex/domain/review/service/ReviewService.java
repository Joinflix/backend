package com.sesac.joinflex.domain.review.service;

import com.sesac.joinflex.domain.movie.entity.Movie;
import com.sesac.joinflex.domain.movie.repository.MovieRepository;
import com.sesac.joinflex.domain.review.dto.request.ReviewUpsertRequest;
import com.sesac.joinflex.domain.review.dto.response.ReviewResponse;
import com.sesac.joinflex.domain.review.entity.Review;
import com.sesac.joinflex.domain.review.repository.ReviewRepository;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.repository.UserRepository;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public ReviewResponse upsertReview(Long userId, Long movieId, ReviewUpsertRequest request) {
        if (request.getContent() == null && request.getStarRating() == null) {
            throw new CustomException(ErrorCode.REVIEW_CONTENT_OR_RATING_REQUIRED);
        }
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
        Review review = reviewRepository.findByUserIdAndMovieId(userId, movieId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
                return Review.builder()
                    .user(user)
                    .movie(movie)
                    .build();
            });
        if (request.getStarRating() != null) {
            Integer previousRating = review.getStarRating();
            if (previousRating == null) {
                movie.addRating(request.getStarRating());
            } else {
                movie.updateRating(previousRating, request.getStarRating());
            }
            review.updateStarRating(request.getStarRating());
        }

        if (request.getContent() != null) {
            review.updateContent(request.getContent());
        }

        try {
            Review savedReview = reviewRepository.save(review);
            return ReviewResponse.from(savedReview);
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 인한 유니크 제약 위반 시 ALREADY_REVIEWED로 처리
            throw new CustomException(ErrorCode.ALREADY_REVIEWED);
        }
    }


    @Transactional(readOnly = true)
    public Slice<ReviewResponse> getMovieReviews(Long movieId, Long cursorId, Pageable pageable) {
        Slice<Review> reviews = reviewRepository.
            findReviewsByMovieId(movieId, cursorId == null ? Long.MAX_VALUE : cursorId, pageable);
        return reviews.map(ReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public Slice<ReviewResponse> getUserReviews(Long userId, Long cursorId, Pageable pageable) {
        Slice<Review> reviews = reviewRepository.
            findReviewsByUserId(userId, cursorId == null ? Long.MAX_VALUE : cursorId, pageable);
        return reviews.map(ReviewResponse::from);
    }

    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findByIdWithMovie(reviewId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_REVIEW_OWNER);
        }

        // 별점이 있는 경우 Movie 집계에서 차감
        if (review.getStarRating() != null) {
            review.getMovie().removeRating(review.getStarRating());
        }
        reviewRepository.delete(review);
    }
}

