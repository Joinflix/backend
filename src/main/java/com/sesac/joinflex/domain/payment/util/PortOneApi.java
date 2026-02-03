package com.sesac.joinflex.domain.payment.util;

import com.siot.IamportRestClient.response.AccessToken;
import com.siot.IamportRestClient.response.IamportResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface PortOneApi {
    // 토큰 발급
    @POST("/users/getToken")
    Call<IamportResponse<AccessToken>> getAuthToken(
            @Body Map<String, String> authData
    );

    // 테스트 결제 정보 조회를 위한 커스텀 API
    @GET("/payments/{imp_uid}")
    Call<IamportResponse<com.siot.IamportRestClient.response.Payment>> getPayment(
            @Header("Authorization") String token,
            @Path("imp_uid") String impUid,
            @Query("include_sandbox") boolean includeSandbox
    );
}
