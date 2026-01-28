package com.sesac.joinflex.domain.auth.service;

import com.sesac.joinflex.domain.auth.dto.request.EmailSendRequest;
import com.sesac.joinflex.domain.auth.dto.request.EmailVerifyRequest;
import com.sesac.joinflex.domain.userhistory.entity.UserAction;
import com.sesac.joinflex.domain.userhistory.service.UserHistoryService;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import com.sesac.joinflex.global.infra.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;
    private final UserHistoryService userHistoryService;

    private static final String AUTH_PREFIX = "AUTH:";
    private static final String FAIL_COUNT_PREFIX = "FAIL_COUNT:";
    private static final String VERIFIED_PREFIX = "VERIFIED:";

    private static final int MAX_FAIL_COUNT = 5;
    private static final int EXPIRE_MINUTES = 15;
    private static final int LOCK_DURATION_HOURS = 1;

    // 인증 코드 발송
    public void sendAuthCode(EmailSendRequest request, String ip, String ua) {
        String email = request.getEmail();
        String failKey = FAIL_COUNT_PREFIX + email; // 실패 카운트 키 (1시간 대기 끝나면 null 반환)

        String failCountStr = redisTemplate.opsForValue().get(failKey); // 현재 실패 카운트 조회
        if (failCountStr != null && Integer.parseInt(failCountStr) >= MAX_FAIL_COUNT) {
            Long ttl = redisTemplate.getExpire(failKey); // 남은 잠금 시간 조회
            long remainMin = (ttl != null && ttl > 0) ? (ttl / 60) + 1 : 0;

            userHistoryService.saveLog(email, UserAction.EMAIL_AUTH, ip, ua, false, "차단 중 발송 시도");

            // 결과 예시: "인증 시도 5회를 초과했습니다. (남은 시간: 45분)"
            throw new CustomException(ErrorCode.TOO_MANY_VERIFICATION_ATTEMPTS, " (남은 시간: " + remainMin + "분)");
        }

        String code = generateVerificationCode();
        emailService.sendEmail(email, "[Joinflix] 이메일 인증 번호입니다.", "인증 번호: " + code);
        redisTemplate.opsForValue().set(AUTH_PREFIX + email, code, Duration.ofMinutes(EXPIRE_MINUTES));
        userHistoryService.saveLog(email, UserAction.EMAIL_AUTH, ip, ua, true, "인증 코드 발송 성공");
    }

    // 인증 코드 검증
    public void verifyCode(EmailVerifyRequest request, String ip, String ua) {
        String email = request.getEmail();
        String code = request.getCode();
        String savedCode = redisTemplate.opsForValue().get(AUTH_PREFIX + email);

        if (savedCode == null) {
            throw new CustomException(ErrorCode.EXPIRED_VERIFICATION_CODE);
        }

        if (!savedCode.equals(code.trim())) {
            handleVerifyFailure(email, ip, ua);
        }

        handleVerificationSuccess(email);
        userHistoryService.saveLog(email, UserAction.EMAIL_AUTH, ip, ua, true, "이메일 인증 완료");
    }

    // 성공 시 클린업
    private void handleVerificationSuccess(String email) {
        redisTemplate.delete(AUTH_PREFIX + email);
        redisTemplate.delete(FAIL_COUNT_PREFIX + email);
        redisTemplate.opsForValue().set(VERIFIED_PREFIX + email, "true", Duration.ofMinutes(10));
    }

    // 인증 실패 처리
    private void handleVerifyFailure(String email, String ip, String ua) {
        String key = FAIL_COUNT_PREFIX + email;// 실패 카운트 키
        Long count = redisTemplate.opsForValue().increment(key); // 실패 카운트 증가

        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(EXPIRE_MINUTES)); // 첫 실패 시 만료 시간 설정
        }

        int remaining = MAX_FAIL_COUNT - (count != null ? count.intValue() : 0); // 남은 시도 횟수 계산

        if (remaining <= 0) {
            redisTemplate.delete(AUTH_PREFIX + email); // 인증 코드 삭제
            redisTemplate.expire(key, Duration.ofHours(LOCK_DURATION_HOURS)); // 잠금 설정
            throw new CustomException(ErrorCode.TOO_MANY_VERIFICATION_ATTEMPTS, " 1시간 뒤에 다시 시도해 주세요.");
        }

        userHistoryService.saveLog(email, UserAction.EMAIL_AUTH, ip, ua, false, "불일치 (남은 횟수: " + remaining + ")");

        // 결과 예시: "인증 번호가 일치하지 않습니다. (남은 횟수: 3회)"
        throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE, " (남은 횟수: " + remaining + "회)");
    }

    // 6자리 인증 코드 생성
    private String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}