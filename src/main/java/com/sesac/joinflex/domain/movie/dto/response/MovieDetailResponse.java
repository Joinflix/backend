package com.sesac.joinflex.domain.movie.dto.response;

import com.sesac.joinflex.domain.movie.entity.Movie;

public record MovieDetailResponse (
        Long movieId,
        String title,
        String poster,
        String backdrop,
        String description,
        Integer averageRating

    ){

    public static MovieDetailResponse from(Movie movie, Integer averageRating){
        return new MovieDetailResponse(
            movie.getId(),
            movie.getTitle(),
            movie.getPoster(),
            movie.getBackdrop(),
            movie.getDescription(),
            averageRating
        );
    }
}
