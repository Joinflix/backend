package com.sesac.joinflex.global.security;

import jakarta.servlet.http.HttpServletRequest;

//JWT or 헤더 or 쿼리 파라미터 교체 위한 인터페이스
public interface CurrentUserResolver {
    Long resolve(HttpServletRequest request);
}
