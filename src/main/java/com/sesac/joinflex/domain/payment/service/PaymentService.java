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
    private static final Integer MEMBERSHIP_DURATION_DAYS = 30;

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
            IamportResponse<com.siot.IamportRestClient.response.Payment> portoneResponse = iamportClient.paymentByImpUid(request.getImpUid());

            System.out.println("포트원 결제 ImpUid: " + request.getImpUid());
            System.out.println("포트원 응답 상태: " + (portoneResponse != null ? portoneResponse.getMessage() : "null"));

            if (portoneResponse == null) {
                System.out.println("포트원 응답 객체가 null입니다.");
                throw new CustomException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
            }

            com.siot.IamportRestClient.response.Payment actualPayment = portoneResponse.getResponse();

            // 실제 결제 상태가 'paid'인지 확인
            if (actualPayment == null || !"paid".equals(actualPayment.getStatus())) {
                System.out.println("포트원에서 결제 정보를 찾을 수 없습니다. (응답 메시지: {}" + portoneResponse.getMessage() + ")");
                userHistoryService.saveLog(user.getEmail(), UserAction.PAYMENT_FAIL, ip, ua, false, "실제 결제 미완료");
                throw new CustomException(ErrorCode.PAYMENT_NOT_PAID);
            }

            // 실제 결제 금액이 우리 DB 가격과 일치하는지 확인 (보안의 핵심)
            if (!membership.getPrice().equals(actualPayment.getAmount().intValue())) {
                userHistoryService.saveLog(user.getEmail(), UserAction.PAYMENT_FAIL, ip, ua, false, "금액 위변조 감지");
                throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
            }

            System.out.println("포트원 결제 검증 성공: 실제 금액 {" + actualPayment.getAmount() +"}, 상태 {" + actualPayment.getStatus() + "}");
        } catch (IamportResponseException e) {
            System.out.println("포트원 API 호출 중 에러 발생: " + e.getMessage() + " (HTTP 상태코드: " + e.getHttpStatusCode() + ")");
            throw new CustomException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
        } catch (Exception e) {
            System.out.println("결제 검증 중 예상치 못한 에러 발생: " + e.getMessage());
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
            user.updateMembership(membership, MEMBERSHIP_DURATION_DAYS);
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
