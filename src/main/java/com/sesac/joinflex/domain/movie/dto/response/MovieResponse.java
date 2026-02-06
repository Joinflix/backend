package com.sesac.joinflex.domain.movie.dto.response;

import com.sesac.joinflex.domain.movie.entity.Movie;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieResponse {
    private Long id;
    private String title;
    private String poster;
    private String backdrop;
    private String description;


    public static MovieResponse from(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .poster(movie.getPoster())
                .backdrop(movie.getBackdrop())
                .description(movie.getDescription())
                .build();
    }
}
