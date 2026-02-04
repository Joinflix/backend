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
    public static final String NICKNAME_DUPLICATE = "/nickname-duplicate";

    // user
    public static final String USER = API_PREFIX + "/users";
    public static final String ID_PATH = "/{id}";
    public static final String SEARCH = "/search";

    public static final String NICKNAME = "/nickname";
    public static final String EMAIL = "/email";

    //payment
    public static final String PAYMENT = API_PREFIX + "/payments";
    public static final String PAYMENT_COMPLETE = "/complete";
    public static final String PAYMENT_ME = "/me";

    //refund
    public static final String REFUND = API_PREFIX + "/refunds";

    // file
    public static final String FILE = API_PREFIX + "/files";
    public static final String UPLOADFILE = "/upload";

    // friend
    public static final String FRIEND = API_PREFIX + "/friends";

    public static final String FRIEND_REQUESTS = "/requests";
    public static final String FRIEND_REQUESTS_INCOMING = FRIEND_REQUESTS + "/incoming";
    public static final String FRIEND_REQUESTS_OUTGOING = FRIEND_REQUESTS + "/outgoing";

    private static final String FRIEND_REQUEST_BY_ID = FRIEND_REQUESTS + "/{requestId}";
    public static final String FRIEND_REQUESTS_ACCEPT = FRIEND_REQUEST_BY_ID + "/accept";
    public static final String FRIEND_REQUESTS_REJECT = FRIEND_REQUEST_BY_ID + "/reject";
    public static final String FRIEND_REQUESTS_CANCEL = FRIEND_REQUEST_BY_ID + "/cancel";

    public static final String FRIEND_DELETE = "/{friendId}";
    public static final String FRIEND_ONLINE = "/online";

    // party
    public static final String PARTY = API_PREFIX + "/parties";

}
