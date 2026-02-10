package com.sesac.joinflex.domain.movie.controller;

import com.sesac.joinflex.domain.movie.dto.response.MovieResponse;
import com.sesac.joinflex.domain.movie.service.MovieService;
import com.sesac.joinflex.global.common.constants.ApiPath;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.MOVIE)
public class MovieController {

    private final MovieService movieService;

    // 전체 영화 조회
    // http://localhost:8080/api/movies
    @GetMapping
    public ResponseEntity<Slice<MovieResponse>> getMovies(
        @RequestParam(required = false) Long cursorId,
        @PageableDefault(size = 20) Pageable pageable) {
        Slice<MovieResponse> response = movieService.getMovies(cursorId, pageable);
        return ResponseEntity.ok(response);
    }

    // 영화 상세 조회
    // http://localhost:8080/api/movies/{movieId}
    @GetMapping("/{movieId}")
    public ResponseEntity<MovieResponse> getSingleMovie(@PathVariable Long movieId) {
        MovieResponse response = movieService.getSingleMovie(movieId);
        return ResponseEntity.ok(response);
    }

}
