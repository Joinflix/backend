package com.sesac.joinflex.domain.auth.controller;

import com.sesac.joinflex.domain.auth.dto.request.LoginRequest;
import com.sesac.joinflex.domain.auth.dto.request.SignupRequest;
import com.sesac.joinflex.domain.auth.dto.response.AuthResponse;
import com.sesac.joinflex.domain.auth.dto.response.TokenResponse;
import com.sesac.joinflex.domain.auth.service.AuthService;
import com.sesac.joinflex.domain.auth.service.EmailVerificationService;
import com.sesac.joinflex.domain.auth.service.RefreshTokenService;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import com.sesac.joinflex.global.util.CookieUtil;
import com.sesac.joinflex.global.util.NetworkUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmailVerificationService emailService;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;
    private final CookieUtil cookieUtil;
    
    // 1. 이메일 인증번호 발송
    @PostMapping("/email/send")
    public ResponseEntity<String> sendEmailCode(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) throws MessagingException {
        emailService.sendAuthCode(request.get("email"), httpRequest);
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    // 2. 이메일 인증번호 검증
    @PostMapping("/email/verify")
    public ResponseEntity<String> verifyEmailCode(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        emailService.verifyCode(request.get("email"), request.get("code"), httpRequest);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    // 3. 최종 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody SignupRequest request, HttpServletRequest httpRequest) {
        UserResponse response = authService.signup(request, httpRequest);
        return ResponseEntity.ok(response);
    }
    
    // 4. 로그인
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response,
            HttpServletRequest servletRequest
    ) {
        String ip = NetworkUtil.getClientIp(servletRequest);
        String userAgent = NetworkUtil.getUserAgent(servletRequest);
        TokenResponse token = authService.login(request, ip, userAgent);
        setTokenResponse(response, token);
        return ResponseEntity.ok(token.getUserResponse());
    }

    // 5. 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<AuthResponse> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = cookieUtil.getCookieValue(request, "refreshToken");
        TokenResponse token = authService.reissue(refresh);
        setTokenResponse(response, token);
        return ResponseEntity.ok(AuthResponse.from(token.getAccessToken()));
    }

    // 6. 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refresh = cookieUtil.getCookieValue(request, "refreshToken");
        try {
            authService.logout(refresh, request);
        } finally {
            cookieUtil.deleteCookie(response, "refreshToken");
        }
        return ResponseEntity.ok("로그아웃 완료");
    }

    // 토큰 세팅 설정
    private void setTokenResponse(HttpServletResponse response, TokenResponse token) {
        response.setHeader("Authorization", "Bearer " + token.getAccessToken());
        response.addCookie(authService.createRefreshTokenCookie(token.getRefreshToken()));
    }

}