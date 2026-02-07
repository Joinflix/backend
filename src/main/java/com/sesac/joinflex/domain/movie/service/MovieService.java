package com.sesac.joinflex.domain.movie.service;

import com.sesac.joinflex.domain.movie.dto.response.MovieResponse;
import com.sesac.joinflex.domain.movie.entity.Movie;
import com.sesac.joinflex.domain.movie.repository.MovieRepository;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieResponse getSingleMovie(Long movieId){
        Movie movie = movieRepository.findById(movieId).orElseThrow(()-> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
        MovieResponse movieResponse = MovieResponse.from(movie);
        return movieResponse;
    }

}
