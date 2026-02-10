package com.sesac.joinflex.domain.review.entity;

import com.sesac.joinflex.domain.movie.entity.Movie;
import com.sesac.joinflex.domain.review.dto.request.ReviewUpsertRequest;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 동일 사용자가 같은 영화에 중복 리뷰를 작성하지 못하도록 DB 레벨에서 유니크 제약 설정
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "movie_id"})
        }
)
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

    @AssertTrue(message = "리뷰 또는 별점 중 하나는 필수입니다.")
    private boolean isContentOrRatingRequired() {
        return starRating != null || content != null;
    }
    
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
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new CustomException(ErrorCode.INVALID_REVIEW_RATING);
        }
        this.starRating = rating;

    }

    public void updateContent(String content){
        this.content = content;
    }


    public void updateReview(Review review, ReviewUpsertRequest request){
        if (request.getStarRating() != null) {
            review.updateStarRating(request.getStarRating());
        }
        if (request.getContent() != null) {
            review.updateContent(request.getContent());
        }
    }
}
