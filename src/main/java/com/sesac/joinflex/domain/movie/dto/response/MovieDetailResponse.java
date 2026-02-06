package com.sesac.joinflex.domain.movie.dto.response;


import com.sesac.joinflex.domain.movie.entity.Movie;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
@Builder
public class MovieDetailResponse {
    private Long id;
    private String title;
    private Slice<MovieReviewResponse> reviews;

    public static MovieDetailResponse of(Movie movie, Slice<MovieReviewResponse> reviews) {
        return MovieDetailResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .reviews(reviews)
                .build();
    }
}
