package com.sesac.joinflex.domain.userhistory.service;

import com.sesac.joinflex.domain.userhistory.entity.UserAction;
import com.sesac.joinflex.domain.userhistory.entity.UserHistory;
import com.sesac.joinflex.domain.userhistory.repository.UserHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserHistoryService {

    private final UserHistoryRepository userHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 독립적인 트랜잭션 생성
    public void saveLog(String email, UserAction action, String ip, String userAgent, Boolean isSuccess, String details) {
        try {
            UserHistory log = UserHistory.builder()
                    .email(email)
                    .action(action)
                    .ip(ip)
                    .userAgent(userAgent)
                    .isSuccess(isSuccess)
                    .details(details)
                    .build();
            userHistoryRepository.save(log);
        } catch (Exception e) {
            log.error("History Log 저장 실패: {}", e.getMessage());
        }
    }

    // 로그인 시도 제한 횟수 초과 여부
    public Boolean isLoginAttemptExceeded(String email) {
        //테스트 시 1시간 -> 1분, 5회 -> 2회로 수정
        long failCount = userHistoryRepository.countByEmailAndIsSuccessFalseAndCreatedAtAfter(
                email, LocalDateTime.now().minusHours(1));
        return failCount >= 5;
    }
}
