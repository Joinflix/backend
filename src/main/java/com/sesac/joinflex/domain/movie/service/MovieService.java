package com.sesac.joinflex.domain.movie.service;


import com.sesac.joinflex.domain.movie.dto.response.MovieDetailResponse;
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

    public Slice<MovieResponse> getMovies(Pageable pageable) {
        return movieRepository.findAll(pageable).map(MovieResponse::from);
    }

    public MovieDetailResponse getMovieDetail(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
        return MovieDetailResponse.of(movie);
    }

}
