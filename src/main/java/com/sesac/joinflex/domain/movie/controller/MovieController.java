package com.sesac.joinflex.domain.movie.controller;


import com.sesac.joinflex.domain.movie.dto.response.MovieDetailResponse;
import com.sesac.joinflex.domain.movie.dto.response.MovieResponse;
import com.sesac.joinflex.domain.movie.dto.response.MovieReviewResponse;
import com.sesac.joinflex.domain.movie.service.MovieService;
import com.sesac.joinflex.global.common.constants.ApiPath;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // 전체 영화 조회
    @GetMapping(ApiPath.MOVIE)
    public ResponseEntity<Slice<MovieResponse>> getMovies(
            @PageableDefault(size = 20) Pageable pageable) {
        Slice<MovieResponse> response = movieService.getMovies(pageable);
        return ResponseEntity.ok(response);
    }

    // 영화 상세 조회 (리뷰 포함)
    @GetMapping(ApiPath.MOVIE + "/{movieId}")
    public ResponseEntity<MovieDetailResponse> getMovieDetail(
            @PathVariable Long movieId,
            @PageableDefault(size = 5) Pageable pageable) {
        MovieDetailResponse response = movieService.getMovieDetail(movieId, pageable);
        return ResponseEntity.ok(response);
    }

    // 영화별 리뷰 목록 조회
    @GetMapping(ApiPath.MOVIE + "/{movieId}/reviews")
    public ResponseEntity<Slice<MovieReviewResponse>> getMovieReviews(
            @PathVariable Long movieId,
            @RequestParam(required = false) Long cursorId,
            @PageableDefault(size = 10) Pageable pageable) {
        Slice<MovieReviewResponse> response = movieService.getMovieReviews(movieId, cursorId, pageable);
        return ResponseEntity.ok(response);
    }
}
