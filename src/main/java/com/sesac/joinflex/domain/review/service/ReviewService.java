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
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    @Transactional
    public ReviewResponse upsertReview(Long userId, Long movieId, ReviewUpsertRequest request) {
        if (request.hasNoContent()) {
            throw new CustomException(ErrorCode.REVIEW_CONTENT_OR_RATING_REQUIRED);
        }

        Review review = reviewRepository.findByUserIdAndMovieId(userId, movieId)
            .orElseGet(() -> getReview(userId, movieId));
        updateReview(review, request);

        try {
            Review savedReview = reviewRepository.save(review);
            return ReviewResponse.from(savedReview);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.ALREADY_REVIEWED);
        }
    }

    public Slice<ReviewResponse> getMovieReviews(Long movieId, Long cursorId, Pageable pageable) {
        if (!movieRepository.existsById(movieId)) {
            throw new CustomException(ErrorCode.MOVIE_NOT_FOUND);
        }
        Slice<Review> reviews = reviewRepository.
            findReviewsByMovieId(movieId, cursorId == null ? Long.MAX_VALUE : cursorId, pageable);
        return reviews.map(ReviewResponse::from);
    }


    public Slice<ReviewResponse> getUserReviews(Long userId, Long cursorId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        Slice<Review> reviews = reviewRepository.
            findReviewsByUserId(userId, cursorId == null ? Long.MAX_VALUE : cursorId, pageable);
        return reviews.map(ReviewResponse::from);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findByIdWithUserAndMovie(reviewId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_REVIEW_OWNER);
        }
        reviewRepository.delete(review);
    }

    private Review getReview(Long userId, Long movieId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
        return Review.builder()
                .user(user)
                .movie(movie)
                .build();
    }

    private void updateReview(Review review, ReviewUpsertRequest request) {
        if (request.getStarRating() != null) {
            review.updateStarRating(request.getStarRating());
        }
        String content = request.getContent();
        if (content != null && !content.isBlank()) {
            review.updateContent(content.trim());
        }
    }
}
