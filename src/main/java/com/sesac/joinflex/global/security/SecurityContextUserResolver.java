package com.sesac.joinflex.global.security;

import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component  // JWT 인증 활성화
public class SecurityContextUserResolver implements CurrentUserResolver {

    @Override
    public Long resolve(HttpServletRequest request) {

        Object principal = getObject();

        // CustomUserDetails에서 userId 추출
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }

        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }

    private static Object getObject() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보 검증
        if (auth == null || !auth.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = auth.getPrincipal();

        // anonymousUser 체크
        if ("anonymousUser".equals(principal)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return principal;
    }
}
