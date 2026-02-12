package com.sesac.joinflix.domain.movie.dto.response;

import com.sesac.joinflix.domain.movie.entity.Movie;

public record MovieResponse(
        Long movieId,
        String title,
        String poster,
        String backdrop,
        String description,
        Integer averageRating
){

    public static MovieResponse from(Movie movie, Integer averageRating) {
        return new MovieResponse(
            movie.getId(),
            movie.getTitle(),
            movie.getPoster(),
            movie.getBackdrop(),
            movie.getDescription(),
            averageRating
        );
    }
}