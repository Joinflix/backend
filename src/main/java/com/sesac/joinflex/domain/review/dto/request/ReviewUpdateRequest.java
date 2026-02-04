package com.sesac.joinflex.domain.review.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateRequest {

    @Size(min = 1, max = 250, message = "250자 이내로 작성해야 합니다.")
    private String content;

    @Min(1) @Max(5)
    private Integer starRating;
}
