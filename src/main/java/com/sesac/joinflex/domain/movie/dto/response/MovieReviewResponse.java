package com.sesac.joinflex.domain.movie.dto.response;

import com.sesac.joinflex.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieReviewResponse {
    private Long id;
    private String content;
    private Integer starRating;
    private Long userId;
    private String nickname;

    public static MovieReviewResponse from(Review review) {
        return MovieReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .starRating(review.getStarRating())
                .userId(review.getUser().getId())
                .nickname(review.getUser().getNickname())
                .build();
    }
}
