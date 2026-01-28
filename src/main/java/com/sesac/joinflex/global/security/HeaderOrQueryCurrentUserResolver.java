package com.sesac.joinflex.global.security;

import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


//  X-User-Id 헤더 or userId 쿼리 파라미터로 유저 확인
//@Component
public class HeaderOrQueryCurrentUserResolver implements CurrentUserResolver{

    private static final String HEADER_NAME = "X-User-Id";
    private static final String QUERY_PARAM_NAME = "userId";

    @Override
    public Long resolve(HttpServletRequest request) {

        String headerValue = request.getHeader(HEADER_NAME);
        if (headerValue != null && !headerValue.isBlank()) {
            return parseUserId(headerValue);
        }


        String queryValue = request.getParameter(QUERY_PARAM_NAME);
        if (queryValue != null && !queryValue.isBlank()) {
            return parseUserId(queryValue);
        }


        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }

    private Long parseUserId(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

}
