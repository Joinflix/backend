package com.sesac.joinflex.domain.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sesac.joinflex.domain.membership.entity.Membership;
import com.sesac.joinflex.domain.membership.service.MembershipService;
import com.sesac.joinflex.domain.payment.dto.request.PaymentCreateRequest;
import com.sesac.joinflex.domain.payment.dto.response.PaymentResponse;
import com.sesac.joinflex.domain.payment.entity.Payment;
import com.sesac.joinflex.domain.payment.entity.PaymentStatus;
import com.sesac.joinflex.domain.payment.repository.PaymentRepository;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.service.UserService;
import com.sesac.joinflex.domain.userhistory.entity.UserAction;
import com.sesac.joinflex.domain.userhistory.service.UserHistoryService;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import retrofit2.Call;      // call.execute()를 위해 필요
import retrofit2.Response;  // 응답 객체를 받기 위해 필요
import retrofit2.Retrofit;  // Retrofit 빌더를 위해 필요
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final UserHistoryService userHistoryService;
    private final MembershipService membershipService;
    private final IamportClient iamportClient;
    private static final Long MEMBERSHIP_DURATION_MONTH = 1L;

    //테스트 결제 조회를 위한 커스텀 Iamport API 인터페이스
    private interface CustomIamportApi {
        @GET("/payments/{imp_uid}")
        Call<IamportResponse<com.siot.IamportRestClient.response.Payment>> paymentByImpUid(
                @Header("Authorization") String token,
                @Path("imp_uid") String imp_uid,
                @Query("include_sandbox") boolean includeSandbox
        );
    }

    @Transactional
    public PaymentResponse complete(PaymentCreateRequest request, Long userId, String ip, String ua) {
        // 1. 사용자 및 멤버십 정보 조회
        User user = userService.findById(userId);
        Membership membership = membershipService.findById(request.getMembershipId());

        // 2. 중복 결제 확인
        if (paymentRepository.existsByPortonePaymentId(request.getImpUid())) {
            userHistoryService.saveLog(user.getEmail(), UserAction.PAYMENT_FAIL, ip, ua, false,
                    "중복된 결제 ID 시도: " + request.getImpUid());
            throw new CustomException(ErrorCode.ALREADY_PAID);
        }

        // 3. PortOne API를 통한 실제 결제 정보 검증
        try {
            // 1. 토큰 발급 (기존 SDK 활용)
            String token = iamportClient.getAuth().getResponse().getToken();

            // 2. 샌드박스 옵션을 포함하여 직접 호출하기 위한 Retrofit 설정
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.iamport.kr")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CustomIamportApi customApi = retrofit.create(CustomIamportApi.class);

            // 3. 조회 실행
            Call<IamportResponse<com.siot.IamportRestClient.response.Payment>> call =
                    customApi.paymentByImpUid(token, request.getImpUid(), true);

            Response<IamportResponse<com.siot.IamportRestClient.response.Payment>> retrofitResponse = call.execute();

            if (!retrofitResponse.isSuccessful() || retrofitResponse.body() == null) {
                throw new CustomException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
            }

            IamportResponse<com.siot.IamportRestClient.response.Payment> portoneResponse = retrofitResponse.body();
            com.siot.IamportRestClient.response.Payment actualPayment = portoneResponse.getResponse();

            // 4. 결제 정보 검증
            if (actualPayment == null || !"paid".equals(actualPayment.getStatus())) {
                userHistoryService.saveLog(user.getEmail(), UserAction.PAYMENT_FAIL, ip, ua, false, "실제 결제 미완료");
                throw new CustomException(ErrorCode.PAYMENT_NOT_PAID);
            }

            // 5. 결제 금액 검증
            if (!membership.getPrice().equals(actualPayment.getAmount().intValue())) {
                userHistoryService.saveLog(user.getEmail(), UserAction.PAYMENT_FAIL, ip, ua, false, "금액 위변조 감지");
                throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
            }

            System.out.println("포트원 테스트 결제 검증 성공");

        } catch (Exception e) {
            System.out.println("결제 검증 중 에러 발생: " + e.getMessage());
            throw new CustomException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
        }

        // 4. JSON 데이터 변환
        String portoneDataJson;
        try {
            portoneDataJson = objectMapper.writeValueAsString(request);
        } catch (IOException e) {
            userHistoryService.saveLog(user.getEmail(), UserAction.PAYMENT_FAIL, ip, ua, false, "결제 데이터 직렬화 실패");
            throw new CustomException(ErrorCode.JSON_SERIALIZATION_FAILED);
        }

        // 5. Payment 엔티티 생성 및 연관관계 편의 메서드 활용
        Payment payment = Payment.builder()
                .portonePaymentId(request.getImpUid())
                .amount(request.getPaidAmount())
                .user(user)
                .membership(membership)
                .status(PaymentStatus.fromStatusString(request.getStatus()))
                .portoneData(portoneDataJson)
                .build();

        // 6. 결제 내역 저장
        Payment savedPayment = paymentRepository.save(payment);

        // 7. 사용자의 멤버십 권한 업데이트
        if (savedPayment.getStatus() == PaymentStatus.COMPLETED) {
            user.updateMembership(membership, MEMBERSHIP_DURATION_MONTH);
        } else {
            userHistoryService.saveLog(user.getEmail(), UserAction.PAYMENT_FAIL, ip, ua, false,
                    "결제 상태 미완료: " + savedPayment.getStatus());
            return PaymentResponse.from(savedPayment);
        }

        // 8. 결제 성공 기록 저장
        userHistoryService.saveLog(
                user.getEmail(),
                UserAction.PAYMENT_SUCCESS,
                ip,
                ua,
                true,
                membership.getDisplayName() + " 결제 성공 및 멤버십 적용 완료"
        );

        return PaymentResponse.from(savedPayment);
    }

}
