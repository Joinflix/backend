package com.sesac.joinflex.domain.userhistory.repository;

import com.sesac.joinflex.domain.userhistory.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {
    // 최근 특정 시간 동안 특정 이메일의 로그인 실패 횟수 조회
    long countByEmailAndIsSuccessFalseAndCreatedAtAfter(String email, LocalDateTime dateTime);
}
