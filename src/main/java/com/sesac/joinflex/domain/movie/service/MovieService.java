package com.sesac.joinflex.domain.movie.service;


import com.sesac.joinflex.domain.movie.dto.response.MovieResponse;
import com.sesac.joinflex.domain.movie.entity.Movie;
import com.sesac.joinflex.domain.movie.repository.MovieRepository;
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

    public MovieResponse getSingleMovie(Long movieId){
        Movie movie = movieRepository.findById(movieId).orElseThrow(()-> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
        MovieResponse movieResponse = MovieResponse.from(movie);
        return movieResponse;
        }
    public Slice<MovieResponse> getMovies(Long cursorId, Pageable pageable) {
        Slice<Movie> movies = movieRepository.findMoviesByCursor(
            cursorId == null ? Long.MAX_VALUE : cursorId, pageable);
        return movies.map(MovieResponse::from);
    }

}
