package com.sesac.joinflex.domain.review.controller;

import com.sesac.joinflex.domain.review.dto.request.ReviewUpsertRequest;
import com.sesac.joinflex.domain.review.dto.response.ReviewResponse;
import com.sesac.joinflex.domain.review.service.ReviewService;
import com.sesac.joinflex.global.common.constants.ApiPath;
import com.sesac.joinflex.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // 리뷰 작성 및 수정 (Upsert)
    // http://localhost:8080/api/reviews/{movieId}
    @PostMapping(ApiPath.REVIEW + "/{movieId}")
    public ResponseEntity<ReviewResponse> upsertReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long movieId,
            @Valid @RequestBody ReviewUpsertRequest request) {
        ReviewResponse response = reviewService.upsertReview(userDetails.getId(), movieId, request);
        return ResponseEntity.ok(response);
    }

    // 영화별 리뷰 조회
    // http://localhost:8080/api/movies/{movieId}/reviews
    @GetMapping(ApiPath.MOVIE + "/{movieId}/reviews")
    public ResponseEntity<Slice<ReviewResponse>> getMovieReviews(
            @PathVariable Long movieId,
            @RequestParam(required = false) Long cursorId,
            @PageableDefault(size = 10) Pageable pageable) {
        Slice<ReviewResponse> response = reviewService.getMovieReviews(movieId, cursorId, pageable);
        return ResponseEntity.ok(response);
    }

    // 사용자별 리뷰 조회
    // http://localhost:8080/api/users/{userId}/reviews
    @GetMapping(ApiPath.USER + "/{userId}/reviews")
    public ResponseEntity<Slice<ReviewResponse>> getUserReviews(
            @PathVariable Long userId,
            @RequestParam(required = false) Long cursorId,
            @PageableDefault(size = 10) Pageable pageable) {
        Slice<ReviewResponse> response = reviewService.getUserReviews(userId, cursorId, pageable);
        return ResponseEntity.ok(response);
    }

    // 리뷰 삭제
    // http://localhost:8080/api/reviews/{reviewId}
    @DeleteMapping(ApiPath.REVIEW + "/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(userDetails.getId(), reviewId);
        return ResponseEntity.noContent().build();
    }
}


