package com.sesac.joinflex.global.security;

import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import com.sesac.joinflex.domain.user.entity.UserRoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration,
            @Value("${jwt.refresh-expiration}" ) long refreshExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    // Access/Refresh 생성 메서드
    public String createToken(String category, UserResponse userResponse, String sessionId) {
        Long targetExpiration = category.equals("access") ? accessExpiration : refreshExpiration;

        JwtBuilder builder = Jwts.builder()
                .subject(String.valueOf(userResponse.getId()))
                .claim("category", category) // access / refresh 구분
                .claim("sessionId", sessionId) // 동시 접속 제어 (요구사항 4번 핵심)
                .claim("email", userResponse.getEmail())
                .claim("role", userResponse.getRole().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + targetExpiration))
                .signWith(key);

        // Access 토큰에만 닉네임을 포함
        // 닉네임 변경 후 기존 Refresh 토큰으로 재발급하면 변경 전 닉네임을 들고 올 수 있음
        if (category.equals("access")) {
            builder.claim("nickName", userResponse.getNickName());
        }

        return builder.compact();
    }

    // access / refresh 구분
    public String getCategory(String token) {
        return getClaims(token).get("category", String.class);
    }

    // sessionId 추출 (동시 접속 및 세션 무효화 체크용)
    public String getSessionId(String token) {
        return getClaims(token).get("sessionId", String.class);
    }

    public Long getUserId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    public String getEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    public String getNickName(String token) {
        Object nickName = getClaims(token).get("nickName");
        return nickName != null ? nickName.toString() : null;
    }

    public UserRoleType getRole(String token) {
        String role = getClaims(token).get("role", String.class);
        return UserRoleType.valueOf(role);
    }

    // 토큰 정보를 DTO로 변환
    public UserResponse getUserResponse(String token) {
        Claims claims = getClaims(token);
        return UserResponse.builder()
                .id(Long.valueOf(claims.getSubject()))
                .email(claims.get("email", String.class))
                .nickName(claims.get("nickName", String.class)) // Refresh일 경우 null 가능
                .role(UserRoleType.valueOf(claims.get("role", String.class)))
                .build();
    }

    // 파싱 로직 공통화
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
