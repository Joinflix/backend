package com.sesac.joinflex.domain.movie.service;


import com.sesac.joinflex.domain.movie.dto.response.MovieDetailResponse;
import com.sesac.joinflex.domain.movie.dto.response.MovieResponse;
import com.sesac.joinflex.domain.movie.dto.response.MovieReviewResponse;
import com.sesac.joinflex.domain.movie.entity.Movie;
import com.sesac.joinflex.domain.movie.repository.MovieRepository;
import com.sesac.joinflex.domain.review.entity.Review;
import com.sesac.joinflex.domain.review.repository.ReviewRepository;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    public Slice<MovieResponse> getMovies(Pageable pageable) {
        return movieRepository.findAll(pageable)
                .map(MovieResponse::from);
    }

    public Slice<MovieReviewResponse> getMovieReviews(Long movieId, Long cursorId, Pageable pageable) {
        Slice<Review> reviews = reviewRepository.findReviewsByMovieId(movieId, cursorId == null ? Long.MAX_VALUE : cursorId, pageable);
        return reviews.map(MovieReviewResponse::from);
    }

    public MovieDetailResponse getMovieDetail(Long movieId, Pageable pageable) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
        
        Slice<MovieReviewResponse> reviews = getMovieReviews(movieId, null, pageable);
        
        return MovieDetailResponse.of(movie, reviews);
    }
}
