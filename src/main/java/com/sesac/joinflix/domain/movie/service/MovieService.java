package com.sesac.joinflix.domain.movie.service;

import com.sesac.joinflix.domain.movie.dto.response.MovieResponse;
import com.sesac.joinflix.domain.movie.entity.Movie;
import com.sesac.joinflix.domain.movie.repository.MovieRepository;
import com.sesac.joinflix.domain.review.repository.ReviewRepository;
import com.sesac.joinflix.global.exception.CustomException;
import com.sesac.joinflix.global.exception.ErrorCode;
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

    public MovieResponse getSingleMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
        return movieResponse(movie);
    }

    public Slice<MovieResponse> getMovies(Long cursorId, Pageable pageable) {
        Slice<Movie> movies = movieRepository.findMoviesByCursor(
            cursorId == null ? Long.MAX_VALUE : cursorId, pageable);
        return movies.map(movie -> movieResponse(movie));
    }


    private Integer averageRating(Long movieId){
        //findAverageRatingByMovieId는 null(리뷰가 없을 경우)이면 0으로 반환하도록 작성된 쿼리문
        return reviewRepository.findAverageRatingByMovieId(movieId);
    }

    private MovieResponse movieResponse(Movie movie) {
        return MovieResponse.from(movie, averageRating(movie.getId()));
    }
}
