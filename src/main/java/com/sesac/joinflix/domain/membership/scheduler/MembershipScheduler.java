package com.sesac.joinflix.domain.membership.scheduler;

import com.sesac.joinflix.domain.user.entity.User;
import com.sesac.joinflix.domain.user.service.UserService;
import com.sesac.joinflix.domain.userhistory.entity.UserAction;
import com.sesac.joinflix.domain.userhistory.service.UserHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MembershipScheduler {
    private final UserService userService;
    private final UserHistoryService userHistoryService;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 실행
    @Transactional
    public void checkMembershipExpiry() {
        // 만료일이 지났고, 현재 멤버십이 null이 아닌 유저만 조회
        List<User> expiredUsers = userService.findAllByMembershipExpiryDateBeforeAndMembershipIsNotNull();

        for (User user : expiredUsers) {
            String expiredInfo = String.format("멤버십 만료 처리됨 (이전 만료일: %s)", user.getMembershipExpiryDate());

            // 1. 유저 멤버십 정보 초기화
            user.updateMembership(null, null);

            // 2. 히스토리 저장
            userHistoryService.saveLog(
                    user.getEmail(),
                    UserAction.MEMBERSHIP_AUTO_EXPIRED,
                    "SYSTEM",           // 스케줄러이므로 IP는 SYSTEM으로 기록
                    "SERVER_SCHEDULER",  // UserAgent 대신 기록
                    true,
                    expiredInfo
            );
        }
    }
}
