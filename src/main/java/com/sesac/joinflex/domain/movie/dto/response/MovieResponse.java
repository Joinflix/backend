package com.sesac.joinflex.domain.movie.dto.response;

import com.sesac.joinflex.domain.movie.entity.Movie;

public record MovieResponse(
        Long movieId,
        String title,
        String poster,
        String backdrop,
        String description
){

    public static MovieResponse from(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getPoster(),
                movie.getBackdrop(),
                movie.getDescription()
        );
    }
}