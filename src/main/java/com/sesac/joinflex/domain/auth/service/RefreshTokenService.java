package com.sesac.joinflex.domain.auth.service;

import com.sesac.joinflex.domain.auth.dto.response.UserSession;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.userhistory.entity.UserAction;
import com.sesac.joinflex.domain.userhistory.service.UserHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class RefreshTokenService {
    private final UserHistoryService userHistoryService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String RT_PREFIX = "RT:"; // Redis Key 접두사 (예: RT:user@example.com)

    // Refresh Token 저장 및 동시 접속 제한
    public void saveRefreshToken(User user, String token, String sessionId, long expirationMs, String ip, String userAgent) {
        String key = RT_PREFIX + user.getEmail();

        Object obj = redisTemplate.opsForValue().get(key);
        System.out.println("DEBUG: Raw Object from Redis = " + obj);

        // 1. Redis에서 해당 유저의 세션 리스트 조회
        List<UserSession> sessions = (List<UserSession>) redisTemplate.opsForValue().get(key);
        if (sessions == null) {
            sessions = new ArrayList<>();
        }

        // 2. 멤버십 등급에 따른 최대 허용 세션 수 확인
        // 멤버십이 없으면 기본값 1, 있으면 해당 멤버십의 설정값 사용
        // TODO: maxConfig 1 -> 2
        int maxConfig = 1;
        if (user.canUseService()) {
            maxConfig = user.getMembership().getMaxConcurrent();
        } else if (user.getMembership() != null) {
            // 멤버십은 있지만 만료된 경우에 대한 로그 (선택사항)
            userHistoryService.saveLog(user.getEmail(), UserAction.LOGIN_EXPIRED_MEMBERSHIP, ip, userAgent, true, "멤버십 만료 상태로 로그인 (기본 세션 1개 부여)");
        }

        // 3. 허용량 초과 시 가장 오래된 세션(Index 0) 삭제
        if (sessions.size() >= maxConfig) {
            int removeCount = sessions.size() - maxConfig + 1;
            for (int i = 0; i < removeCount; i++) {
                UserSession removed = sessions.remove(0);
                // [History 추가] 어떤 세션이 밀려났는지 기록
                userHistoryService.saveLog(user.getEmail(), UserAction.SESSION_FORCED_LOGOUT, ip, userAgent, true,
                        "동시 접속 제한으로 세션 종료: " + removed.getSessionId());
            }
        }

        // 4. 새 세션 객체 생성 및 리스트 추가
        sessions.add(new UserSession(token, sessionId));

        // 5. Redis 저장 및 만료 시간(TTL) 설정
        redisTemplate.opsForValue().set(key, sessions, expirationMs, TimeUnit.MILLISECONDS);
    }

    // 토큰 존재 여부 확인 (이메일 기반 조회)
    public Boolean existsRefresh(String email, String token) {
        String key = RT_PREFIX + email;
        List<UserSession> sessions = (List<UserSession>) redisTemplate.opsForValue().get(key);

        if (sessions == null) return false;
        return sessions.stream().anyMatch(s -> s.getRefreshToken().equals(token));
    }

    // 토큰 삭제 (로그아웃 시)
    public void removeRefresh(String email, String token) {
        String key = RT_PREFIX + email;
        List<UserSession> sessions = (List<UserSession>) redisTemplate.opsForValue().get(key);

        if (sessions != null) {
            sessions.removeIf(s -> s.getRefreshToken().equals(token));
            if (sessions.isEmpty()) {
                redisTemplate.delete(key);
            } else {
                // 남은 세션이 있다면 다시 저장 (기존 TTL 유지를 위해 세밀한 처리가 필요할 수 있으나 보통 갱신함)
                redisTemplate.opsForValue().set(key, sessions);
            }
        }
    }

    // Token Rotation (재발급 시 세션 ID 유지하며 토큰 교체)
    public void rotateToken(String oldToken, String email, String newToken, String sessionId, long expirationMs) {
        String key = RT_PREFIX + email;
        List<UserSession> sessions = (List<UserSession>) redisTemplate.opsForValue().get(key);

        if (sessions != null) {
            // 기존 토큰 제거 후 새 토큰 세션 추가
            sessions.removeIf(s -> s.getRefreshToken().equals(oldToken));
            sessions.add(new UserSession(newToken, sessionId));

            redisTemplate.opsForValue().set(key, sessions, expirationMs, TimeUnit.MILLISECONDS);
        }
    }

    // 소셜 로그인 성공 후 쿠키(Refresh) -> 헤더 방식으로 응답 <-- 이건 추후에 작성
}
