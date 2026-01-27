package com.sesac.joinflex.domain.auth.service;

import com.sesac.joinflex.domain.userhistory.entity.UserAction;
import com.sesac.joinflex.domain.userhistory.service.UserHistoryService;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import com.sesac.joinflex.global.util.NetworkUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final UserHistoryService userHistoryService;

    private static final String AUTH_PREFIX = "AUTH:";
    private static final String LIMIT_PREFIX = "LIMIT:";
    private static final String VERIFIED_PREFIX = "VERIFIED:";
    private static final int EXPIRE_MINUTES = 5;

    public void sendAuthCode(String email, HttpServletRequest httpRequest) {
        String ip = NetworkUtil.getClientIp(httpRequest);
        String ua = NetworkUtil.getUserAgent(httpRequest);
        // 1. 발송 횟수 제한 확인 (1시간 내 5회)
        String countStr = redisTemplate.opsForValue().get(LIMIT_PREFIX + email);
        if (countStr != null && Integer.parseInt(countStr) >= 5) {
            userHistoryService.saveLog(email, UserAction.EMAIL_AUTH, ip, ua, false, "메일 발송 횟수 초과");
            throw new CustomException(ErrorCode.TOO_MANY_VERIFICATION_ATTEMPTS);
        }

        String code = generateVerificationCode();

        try {
            sendMail(email, code);
            // 2. Redis에 인증 코드 저장 (5분 만료)
            redisTemplate.opsForValue().set(AUTH_PREFIX + email, code, Duration.ofMinutes(EXPIRE_MINUTES));
            // 3. 발송 횟수 증가 및 만료 시간 설정
            incrementSendCount(email);

            userHistoryService.saveLog(email, UserAction.EMAIL_AUTH, ip, ua, true, "인증 코드 발송 성공");
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }

    public void verifyCode(String email, String code, HttpServletRequest httpRequest) {
        String ip = NetworkUtil.getClientIp(httpRequest);
        String ua = NetworkUtil.getUserAgent(httpRequest);
        String savedCode = redisTemplate.opsForValue().get(AUTH_PREFIX + email);

        if (savedCode == null) {
            userHistoryService.saveLog(email, UserAction.EMAIL_AUTH, ip, ua, false, "만료된 인증 코드");
            throw new CustomException(ErrorCode.EXPIRED_VERIFICATION_CODE);
        }

        if (!savedCode.equals(code.trim())) {
            userHistoryService.saveLog(email, UserAction.EMAIL_AUTH, ip, ua, false, "인증 코드 불일치");
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        // 인증 성공 시: 코드 삭제 후 "인증 완료" 마크 저장 (10분 유효)
        redisTemplate.delete(AUTH_PREFIX + email);
        // TODO : 테스트 시 10분 -> 1분
        redisTemplate.opsForValue().set(VERIFIED_PREFIX + email, "true", Duration.ofMinutes(10));

        userHistoryService.saveLog(email, UserAction.EMAIL_AUTH, ip, ua, true, "이메일 인증 완료");
    }

    private void incrementSendCount(String email) {
        String key = LIMIT_PREFIX + email;
        redisTemplate.opsForValue().increment(key);
        if (redisTemplate.getExpire(key) == -1) {
            //TODO: 테스트 시 1시간 -> 1분
            redisTemplate.expire(key, Duration.ofHours(1));
        }
    }

    private String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    private void sendMail(String email, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject("[Joinflix] 이메일 인증 번호입니다.");
        helper.setText("인증 번호: " + code, true);
        mailSender.send(message);
    }
}