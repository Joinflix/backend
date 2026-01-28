package com.sesac.joinflex.domain.auth.controller;

import com.sesac.joinflex.domain.auth.dto.request.EmailSendRequest;
import com.sesac.joinflex.domain.auth.dto.request.EmailVerifyRequest;
import com.sesac.joinflex.domain.auth.dto.request.LoginRequest;
import com.sesac.joinflex.domain.auth.dto.request.SignupRequest;
import com.sesac.joinflex.domain.auth.dto.response.AuthResponse;
import com.sesac.joinflex.domain.auth.dto.response.TokenResponse;
import com.sesac.joinflex.domain.auth.service.AuthService;
import com.sesac.joinflex.domain.auth.service.EmailVerificationService;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import com.sesac.joinflex.global.common.constants.ApiPath;
import com.sesac.joinflex.global.util.CookieUtil;
import com.sesac.joinflex.global.util.NetworkUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(ApiPath.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final EmailVerificationService emailService;
    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    // 1. 이메일 인증번호 발송
    @PostMapping(ApiPath.EMAIL_SEND)
    public ResponseEntity<String> sendEmailCode(@Valid @RequestBody EmailSendRequest request, HttpServletRequest httpRequest) throws MessagingException {
        String ip = NetworkUtil.getClientIp(httpRequest);
        String ua = NetworkUtil.getUserAgent(httpRequest);
        emailService.sendAuthCode(request, ip, ua);
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    // 2. 이메일 인증번호 검증
    @PostMapping(ApiPath.EMAIL_VERIFY)
    public ResponseEntity<String> verifyEmailCode(@Valid @RequestBody EmailVerifyRequest request, HttpServletRequest httpRequest) {
        String ip = NetworkUtil.getClientIp(httpRequest);
        String ua = NetworkUtil.getUserAgent(httpRequest);
        emailService.verifyCode(request, ip, ua);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    // 3. 최종 회원가입
    @PostMapping(ApiPath.SIGNUP)
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request, HttpServletRequest httpRequest) {
        String ip = NetworkUtil.getClientIp(httpRequest);
        String ua = NetworkUtil.getUserAgent(httpRequest);
        UserResponse response = authService.signup(request, ip, ua);
        return ResponseEntity.ok(response);
    }
    
    // 4. 로그인
    @PostMapping(ApiPath.LOGIN)
    public ResponseEntity<UserResponse> login(
            @Valid @RequestBody LoginRequest request,
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
    @PostMapping(ApiPath.REISSUE)
    public ResponseEntity<AuthResponse> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = cookieUtil.getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
        TokenResponse token = authService.reissue(refresh);
        setTokenResponse(response, token);
        return ResponseEntity.ok(AuthResponse.from(token.getAccessToken()));
    }

    // 6. 로그아웃
    @PostMapping(ApiPath.LOGOUT)
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refresh = cookieUtil.getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
        try {
            authService.logout(refresh, request);
        } finally {
            cookieUtil.deleteCookie(response, REFRESH_TOKEN_COOKIE_NAME);
        }
        return ResponseEntity.ok("로그아웃 완료");
    }

    // 토큰 세팅 설정
    private void setTokenResponse(HttpServletResponse response, TokenResponse token) {
        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + token.getAccessToken());
        response.addCookie(authService.createRefreshTokenCookie(token.getRefreshToken()));
    }

}