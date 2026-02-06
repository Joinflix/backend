package com.sesac.joinflex.domain.review.entity;

import com.sesac.joinflex.domain.movie.entity.Movie;
import com.sesac.joinflex.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(5)
    private Integer starRating;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Builder
    public Review(Integer starRating, String content, User user, Movie movie) {
        this.starRating = starRating;
        this.content = content;
        this.user = user;
        this.movie = movie;
    }

    public void updateStarRating(Integer rating){
        this.starRating = rating;

    }

    public void updateContent(String content){
        this.content = content;
    }
}
