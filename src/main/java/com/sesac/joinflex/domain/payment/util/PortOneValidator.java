package com.sesac.joinflex.domain.payment.util;

import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PortOneValidator {

    private final PortOneApi portOneApi;

    @Value("${portone.apiKey}")
    private String apiKey;

    @Value("${portone.apiSecret}")
    private String apiSecret;

    public com.siot.IamportRestClient.response.Payment verify(String impUid, Integer expectedAmount) {
        try {
            // 1. 액세스 토큰 발급 (SDK 없이 직접 호출)
            String token = getAccessToken();

            // 2. 결제 정보 조회 (include_sandbox=true 포함)
            Response<IamportResponse<Payment>> response =
                    portOneApi.getPayment(token, impUid, true).execute();

            if (!response.isSuccessful() || response.body() == null) {
                throw new CustomException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
            }

            com.siot.IamportRestClient.response.Payment actualPayment = response.body().getResponse();

            // 3. 비즈니스 검증 (상태 및 금액)
            if (actualPayment == null || !"paid".equals(actualPayment.getStatus())) {
                throw new CustomException(ErrorCode.PAYMENT_NOT_PAID);
            }

            if (expectedAmount != null && !expectedAmount.equals(actualPayment.getAmount().intValue())) {
                throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
            }

            return actualPayment;

        } catch (IOException e) {
            throw new CustomException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
        }
    }

    // 토큰 발급 로직 (내부 메서드)
    private String getAccessToken() throws IOException {
        // 토큰 발급용 DTO 혹은 Map 생성
        Map<String, String> authData = new HashMap<>();
        authData.put("imp_key", apiKey);
        authData.put("imp_secret", apiSecret);

        Response<IamportResponse<com.siot.IamportRestClient.response.AccessToken>> authResponse =
                portOneApi.getAuthToken(authData).execute();

        if (!authResponse.isSuccessful() || authResponse.body() == null) {
            throw new CustomException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
        }

        return authResponse.body().getResponse().getToken();
    }
}
