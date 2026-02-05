package com.sesac.joinflex.domain.review.controller;

import com.sesac.joinflex.domain.review.dto.response.ReviewResponse;
import com.sesac.joinflex.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;


    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long reviewId) {
        ReviewResponse response = reviewService.getReview(reviewId);
        return ResponseEntity.ok(response);

    }

    @PostMapping()
    public ResponseEntity<Void> createReview() {
        reviewService.createReview();
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview() {
        reviewService.updateReview();
        return ResponseEntity.ok().build();
    }
}


