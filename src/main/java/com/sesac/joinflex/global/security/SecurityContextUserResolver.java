package com.sesac.joinflex.global.security;

import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextUserResolver implements CurrentUserResolver {

    @Override
    public Long resolve(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // ===== 인증 정보 검증 =====
        if (auth == null || !auth.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = auth.getPrincipal();

        // ===== anonymousUser 체크 =====
        // 인증되지 않은 요청은 "anonymousUser" 문자열이 principal
        if ("anonymousUser".equals(principal)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // ===== UserDetails에서 userId 추출 =====
        // backend 통합 후: CustomUserDetails로 캐스팅하여 getId() 호출
        // 현재: UserDetails.getUsername()에서 userId를 파싱 (이메일이 아닌 경우)
        if (principal instanceof UserDetails userDetails) {
            // 임시: username이 userId인 경우 (실제 구현에 맞게 수정 필요)
            // 통합 후: ((CustomUserDetails) principal).getId() 사용
            try {
                return Long.parseLong(userDetails.getUsername());
            } catch (NumberFormatException e) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }
        }

        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
}
