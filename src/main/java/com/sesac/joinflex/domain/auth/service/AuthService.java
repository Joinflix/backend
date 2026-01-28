package com.sesac.joinflex.domain.auth.service;

import com.sesac.joinflex.domain.auth.dto.request.LoginRequest;
import com.sesac.joinflex.domain.auth.dto.request.SignupRequest;
import com.sesac.joinflex.domain.auth.dto.response.TokenResponse;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.service.UserService;
import com.sesac.joinflex.domain.userhistory.entity.UserAction;
import com.sesac.joinflex.domain.userhistory.service.UserHistoryService;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import com.sesac.joinflex.global.security.JwtProvider;
import com.sesac.joinflex.global.util.CookieUtil;
import com.sesac.joinflex.global.util.NetworkUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final UserHistoryService userHistoryService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;
    private final StringRedisTemplate redisTemplate;
    private static final String VERIFIED_PREFIX = "VERIFIED:";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // 회원가입
    @Transactional
    public UserResponse signup(SignupRequest request, String ip, String ua) {
        String email = request.getEmail();

        try {
            // 1. 사용자 유효성 체크
            userService.validateNewUser(email, request.getNickname(), ip);

            // 2. 이메일 인증 체크 (Redis에서 확인)
            String isVerified = redisTemplate.opsForValue().get(VERIFIED_PREFIX + email);
            if (isVerified == null) {
                throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
            }

            // 3. 사용자 생성
            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(request.getPassword()))
                    .nickname(request.getNickname())
                    .signupIp(ip)
                    .build();

            User savedUser = userService.save(user);

            // 4. 가입 완료 후 Redis 인증 마크 삭제
            redisTemplate.delete(VERIFIED_PREFIX + email);

            // 5. 히스토리 기록
            userHistoryService.saveLog(email, UserAction.SIGNUP, ip, ua, true, "회원가입 성공");
            return UserResponse.from(savedUser);

        } catch (CustomException e) {
            userHistoryService.saveLog(email, UserAction.SIGNUP, ip, ua, false, e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            userHistoryService.saveLog(email, UserAction.SIGNUP, ip, ua, false, "서버 내부 오류: " + e.getMessage());
            throw e;
        }
    }

    // 로그인
    @Transactional
    public TokenResponse login(LoginRequest request, String ip, String userAgent) {
        try {
            // 1. 로그인 시도 횟수 체크(1시간에 5회까지)
            if (userHistoryService.isLoginAttemptExceeded(request.getEmail())) {
                throw new CustomException(ErrorCode.TOO_MANY_LOGIN_ATTEMPTS);
            }
            User user = userService.findByEmailForAuth(request.getEmail());
            // 2. 비밀번호 일치 여부 체크
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
            }
            // 3. 계정 잠금 여부 체크
            if (user.getIsLock()) throw new CustomException(ErrorCode.LOCKED_ACCOUNT);
            // 4. session 및 token 생성 후 Redis 저장
            String sessionId = UUID.randomUUID().toString();
            UserResponse userResponse = UserResponse.from(user);

            String access = jwtProvider.createToken(TOKEN_TYPE_ACCESS, userResponse, sessionId);
            String refresh = jwtProvider.createToken(TOKEN_TYPE_REFRESH, userResponse, sessionId);

            refreshTokenService.saveRefreshToken(user, refresh, sessionId, refreshExpiration, ip, userAgent);
            // 5. 로그인IP 저장 및 히스토리 저장
            user.updateLoginInfo(ip);
            userHistoryService.saveLog(user.getEmail(), UserAction.LOGIN, ip, userAgent, true, "로그인 성공");

            return TokenResponse.builder()
                    .accessToken(access)
                    .refreshToken(refresh)
                    .userResponse(userResponse).build();

        } catch (CustomException e) {
            userHistoryService.saveLog(request.getEmail(), UserAction.LOGIN, ip, userAgent, false, e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            userHistoryService.saveLog(request.getEmail(), UserAction.LOGIN, ip, userAgent, false, "서버 오류: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public TokenResponse reissue(String refresh) {
        // 1. 유효성 및 Redis 존재 여부 통합 체크
        String email = jwtProvider.getEmail(refresh); // 토큰에서 이메일 추출
        validateToken(email, refresh);

        // 2. 세션 및 유저 정보 추출
        String sessionId = jwtProvider.getSessionId(refresh);
        Long userId = jwtProvider.getUserId(refresh);

        // 3. 최신 유저 정보 조회
        User user = userService.findById(userId);
        UserResponse userResponse = UserResponse.from(user);

        // 4. 새 토큰 생성
        String newAccess = jwtProvider.createToken(TOKEN_TYPE_ACCESS, userResponse, sessionId);
        String newRefresh = jwtProvider.createToken(TOKEN_TYPE_REFRESH, userResponse, sessionId);

        // 5. Redis Rotation 적용 (이메일 파라미터 추가)
        refreshTokenService.rotateToken(refresh, email, newRefresh, sessionId, refreshExpiration);

        return TokenResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .build();
    }

    // 로그아웃
    @Transactional
    public void logout(String refresh, HttpServletRequest httpRequest) {
        if (refresh == null || refresh.isBlank()) return; // 토큰 없으면 그냥 종료

        String ip = NetworkUtil.getClientIp(httpRequest);
        String ua = NetworkUtil.getUserAgent(httpRequest);
        String email = jwtProvider.getEmail(refresh);

        try {
            if (refreshTokenService.existsRefresh(email, refresh)) {
                refreshTokenService.removeRefresh(email, refresh);
                userHistoryService.saveLog(email, UserAction.LOGOUT, ip, ua, true, "로그아웃 성공");
            }
        } catch (Exception e) {
            userHistoryService.saveLog(email, UserAction.LOGOUT, ip, ua, false, "로그아웃 실패: " + e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    // 토큰 유효성 체크
    private void validateToken(String email, String token) {
        if (token == null || !jwtProvider.validateToken(token) ||
                !jwtProvider.getCategory(token).equals(TOKEN_TYPE_REFRESH)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // Redis에 해당 이메일의 세션 리스트 중 해당 토큰이 있는지 확인
        if (!refreshTokenService.existsRefresh(email, token)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    public Cookie createRefreshTokenCookie(String refresh) {
        return cookieUtil.createCookie(REFRESH_TOKEN_COOKIE_NAME, refresh, refreshExpiration);
    }
}
