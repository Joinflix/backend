package com.sesac.joinflex.domain.review.dto.response;

import com.sesac.joinflex.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReviewResponse {

    private Long id;

    private String content;

    private Integer starRating;

    private Long userId;

    private String nickname;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .starRating(review.getStarRating())
                .userId(review.getUser().getId())
                .nickname(review.getUser().getNickname())
                .build();
    }

}
