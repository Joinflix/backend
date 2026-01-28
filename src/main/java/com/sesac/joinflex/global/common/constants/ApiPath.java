package com.sesac.joinflex.global.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * API 경로 상수를 관리하는 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE) // 인스턴스화 방지
public final class ApiPath {

    public static final String API_PREFIX = "/api";

    // auth
    public static final String AUTH = API_PREFIX + "/auth";
    public static final String SIGNUP = "/signup";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String REISSUE = "/reissue";
    public static final String EMAIL_SEND = "/email-send";
    public static final String EMAIL_VERIFY = "/email-verify";

    //user
    public static final String USER = API_PREFIX + "/users";
    public static final String ID_PATH = "/{id}";

    // file
    public static final String FILE = API_PREFIX + "/files";
    public static final String UPLOADFILE = "/upload";
}
