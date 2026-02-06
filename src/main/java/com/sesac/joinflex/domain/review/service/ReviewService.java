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
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        Review review = reviewRepository.findByUserIdAndMovieId(userId, movieId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
                    Movie movie = movieRepository.findById(movieId)
                            .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
                    return Review.builder()
                            .user(user)
                            .movie(movie)
                            .build();
                });

        if (request.getStarRating() != null) {
            review.updateStarRating(request.getStarRating());
        }
        if (request.getContent() != null) {
            review.updateContent(request.getContent());
        }

        Review savedReview = reviewRepository.save(review);
        return ReviewResponse.builder()
                .id(savedReview.getId())
                .content(savedReview.getContent())
                .starRating(savedReview.getStarRating())
                .userId(savedReview.getUser().getId())
                .nickname(savedReview.getUser().getNickname())
                .movieId(review.getMovie().getId())
                .movieTitle(review.getMovie().getTitle())
                .build();
    }

    @Transactional(readOnly = true)
    public Slice<ReviewResponse> getMovieReviews(Long movieId, Long cursorId, Pageable pageable) {
        Slice<Review> reviews = reviewRepository.findReviewsByMovieId(
                movieId, cursorId == null ? Long.MAX_VALUE : cursorId, pageable);

        return reviews.map(review -> ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .starRating(review.getStarRating())
                .userId(review.getUser().getId())
                .nickname(review.getUser().getNickname())
                .movieId(review.getMovie().getId())
                .movieTitle(review.getMovie().getTitle())
                .build());
    }

    @Transactional(readOnly = true)
    public Slice<ReviewResponse> getUserReviews(Long userId, Long cursorId, Pageable pageable) {
        Slice<Review> reviews = reviewRepository.findReviewsByUserId(
                userId, cursorId == null ? Long.MAX_VALUE : cursorId, pageable);

        return reviews.map(review -> ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .starRating(review.getStarRating())
                .userId(review.getUser().getId())
                .nickname(review.getUser().getNickname())
                .movieId(review.getMovie().getId())
                .movieTitle(review.getMovie().getTitle())
                .build());
    }

    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_REVIEW_OWNER);
        }

        reviewRepository.delete(review);
    }
}
