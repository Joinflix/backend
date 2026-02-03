package com.sesac.joinflex.domain.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sesac.joinflex.domain.membership.entity.Membership;
import com.sesac.joinflex.domain.membership.service.MembershipService;
import com.sesac.joinflex.domain.payment.dto.request.PaymentCreateRequest;
import com.sesac.joinflex.domain.payment.dto.response.PaymentResponse;
import com.sesac.joinflex.domain.payment.entity.Payment;
import com.sesac.joinflex.domain.payment.entity.PaymentStatus;
import com.sesac.joinflex.domain.payment.repository.PaymentRepository;
import com.sesac.joinflex.domain.payment.util.PortOneValidator; // 새로 만든 Validator 주입
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.service.UserService;
import com.sesac.joinflex.domain.userhistory.entity.UserAction;
import com.sesac.joinflex.domain.userhistory.service.UserHistoryService;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final UserHistoryService userHistoryService;
    private final MembershipService membershipService;
    private final PortOneValidator portOneValidator;

    private static final Long MEMBERSHIP_DURATION_MONTH = 1L;

    @Transactional
    public PaymentResponse complete(PaymentCreateRequest request, Long userId, String ip, String ua) {
        // 1. 사용자 및 멤버십 정보 조회
        User user = userService.findById(userId);
        Membership membership = membershipService.findById(request.getMembershipId());

        // 2. 중복 결제 확인
        validateDuplicatePayment(request.getImpUid(), user.getEmail(), ip, ua);

        // 3. PortOne API를 통한 실제 결제 정보 검증 (PortOneValidator 활용)
        portOneValidator.verify(request.getImpUid(), membership.getPrice());

        // 4. 결제 내역 저장
        Payment savedPayment = savePaymentEntity(request, user, membership);

        // 5. 멤버십 업데이트 및 사후 로직 처리
        return processPaymentCompletion(savedPayment, user, membership, ip, ua);
    }

    /**
     * 중복 결제 여부 확인
     */
    private void validateDuplicatePayment(String impUid, String email, String ip, String ua) {
        if (paymentRepository.existsByPortonePaymentId(impUid)) {
            userHistoryService.saveLog(email, UserAction.PAYMENT_FAIL, ip, ua, false,
                    "중복된 결제 ID 시도: " + impUid);
            throw new CustomException(ErrorCode.ALREADY_PAID);
        }
    }

    /**
     * Payment 엔티티 생성 및 저장
     */
    private Payment savePaymentEntity(PaymentCreateRequest request, User user, Membership membership) {
        try {
            String portoneDataJson = objectMapper.writeValueAsString(request);

            Payment payment = Payment.builder()
                    .portonePaymentId(request.getImpUid())
                    .amount(request.getPaidAmount())
                    .user(user)
                    .membership(membership)
                    .status(PaymentStatus.fromStatusString(request.getStatus()))
                    .portoneData(portoneDataJson)
                    .build();

            return paymentRepository.save(payment);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.JSON_SERIALIZATION_FAILED);
        }
    }

    /**
     * 결제 상태에 따른 멤버십 업데이트 및 로그 저장
     */
    private PaymentResponse processPaymentCompletion(Payment payment, User user, Membership membership, String ip, String ua) {
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            // 멤버십 기간 업데이트
            user.updateMembership(membership, MEMBERSHIP_DURATION_MONTH);

            // 성공 로그 저장
            userHistoryService.saveLog(
                    user.getEmail(),
                    UserAction.PAYMENT_SUCCESS,
                    ip,
                    ua,
                    true,
                    membership.getDisplayName() + " 결제 성공 및 멤버십 적용 완료"
            );
        } else {
            // 미완료 상태 처리
            userHistoryService.saveLog(user.getEmail(), UserAction.PAYMENT_FAIL, ip, ua, false,
                    "결제 상태 미완료: " + payment.getStatus());
        }

        return PaymentResponse.from(payment);
    }
}