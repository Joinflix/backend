package com.sesac.joinflex.domain.movie.entity;

import com.sesac.joinflex.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "movies")
@Entity
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String poster;

    private String backdrop;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer totalStarRating = 0;

    @Column(nullable = false)
    private Integer ratingCount = 0;

    public Double getAverageRating() {
        if (ratingCount == 0) {
            return null;
        }
        return Math.round((double) totalStarRating / ratingCount * 10) / 10.0;
    }

    public void addRating(int rating) {
        this.totalStarRating += rating;
        this.ratingCount++;
    }

    public void updateRating(int oldRating, int newRating) {
        this.totalStarRating = this.totalStarRating - oldRating + newRating;
    }

    public void removeRating(int rating) {
        this.totalStarRating -= rating;
        this.ratingCount--;
    }
}

