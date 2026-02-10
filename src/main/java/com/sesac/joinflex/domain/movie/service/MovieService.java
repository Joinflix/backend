package com.sesac.joinflex.domain.movie.service;

import com.sesac.joinflex.domain.movie.dto.response.MovieDetailResponse;
import com.sesac.joinflex.domain.movie.dto.response.MovieResponse;
import com.sesac.joinflex.domain.movie.entity.Movie;
import com.sesac.joinflex.domain.movie.repository.MovieRepository;
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

    public MovieDetailResponse getSingleMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
        MovieDetailResponse movieDetailResponse = MovieDetailResponse.from(movie, averageRating(movieId));
        return movieDetailResponse;
    }
    public Slice<MovieResponse> getMovies(Long cursorId, Pageable pageable) {
        Slice<Movie> movies = movieRepository.findMoviesByCursor(
            cursorId == null ? Long.MAX_VALUE : cursorId, pageable);
        return movies.map(MovieResponse::from);
    }
    //findAverageRatingByMovieId는 null(리뷰가 없을 경우)이면 0으로 반환하도록 작성된 쿼리문
    private Integer averageRating(Long movieId){
        return reviewRepository.findAverageRatingByMovieId(movieId);
    }
}
