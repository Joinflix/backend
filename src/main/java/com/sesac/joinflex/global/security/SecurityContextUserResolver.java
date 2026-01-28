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


        if (auth == null || !auth.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = auth.getPrincipal();


        if ("anonymousUser".equals(principal)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }


        // ===== CustomUserDetails에서 userId 추출 =====
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }

        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
}
