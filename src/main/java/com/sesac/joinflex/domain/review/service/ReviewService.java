package com.sesac.joinflex.domain.review.service;

import com.sesac.joinflex.domain.review.dto.response.ReviewResponse;
import com.sesac.joinflex.domain.review.repository.ReviewRepository;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import com.sesac.joinflex.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;


    public ReviewResponse createReview(){
        //리뷰 생성 로직
    }

    public Slice<ReviewResponse> Review(){
        // 리뷰 조회
        return ;
    }

    public ReviewResponse updateReview(){
        // 리뷰 수정 로직
        return ;
    }

    public void deleteReview(){
        // 리뷰 삭제 로직
    }


    //별점 수정 로직만 별도로 추가 해야할까? 라는 문제.
}
