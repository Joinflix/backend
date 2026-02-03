package com.sesac.joinflex.domain.refund.service;

import com.sesac.joinflex.domain.payment.entity.Payment;
import com.sesac.joinflex.domain.payment.service.PaymentService;
import com.sesac.joinflex.domain.refund.dto.request.RefundRequest;
import com.sesac.joinflex.domain.refund.dto.response.RefundResponse;
import com.sesac.joinflex.domain.refund.entity.Refund;
import com.sesac.joinflex.domain.refund.entity.RefundStatus;
import com.sesac.joinflex.domain.refund.repository.RefundRepository;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.service.UserService;
import com.sesac.joinflex.domain.userhistory.entity.UserAction;
import com.sesac.joinflex.domain.userhistory.service.UserHistoryService;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RefundService {
    private final UserService userService;
    private final UserHistoryService userHistoryService;
    private final PaymentService paymentService;
    private final RefundRepository refundRepository;
    private final IamportClient iamportClient;

    public RefundResponse createRefund(RefundRequest request, Long userId, String ip, String ua) {
        // 1. 조회 및 엔티티 위임 검증
        User user = userService.findById(userId);
        Payment payment = paymentService.findById(request.getPaymentId());

        payment.validateOwnership(userId); // 본인 소유 결제 검증
        payment.validateRefundable(); // 환불 가능 상태
        validateDuplicateRefund(payment); // 중복 환불 요청 검증

        // 2. 환불 기록 생성 (초기 상태: REQUESTED)
        Refund refund = createRefundEntity(payment, request.getReason());
        refundRepository.save(refund);

        try {
            // 3. 포트원 API 호출
            callPortOneCancel(payment, request.getReason());

            // 4. 성공 후속 처리
            processSuccess(user, payment, refund, ip, ua);

            return RefundResponse.builder()
                    .refundId(refund.getId())
                    .paymentId(payment.getId())
                    .status(refund.getStatus().name())
                    .message("환불 요청이 성공적으로 처리되었습니다.")
                    .build();

        } catch (IamportResponseException | IOException e) {
            processFailure(user, refund, ip, ua, "환불 API 통신 실패: " + e.getMessage());
            throw new CustomException(ErrorCode.REFUND_FAILED);
        } catch (Exception e) {
            processFailure(user, refund, ip, ua, "시스템 오류 발생");
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    // 중복 환불 요청 검증
    private void validateDuplicateRefund(Payment payment) {
        if (refundRepository.existsByPaymentAndStatusIn(payment,
                List.of(RefundStatus.REQUESTED, RefundStatus.COMPLETED))) {
            throw new CustomException(ErrorCode.REFUND_ALREADY_PROCESSED);
        }
    }

    // 환불 엔티티 생성
    private Refund createRefundEntity(Payment payment, String reason) {
        return Refund.builder()
                .payment(payment)
                .reason(reason)
                .amount(payment.getAmount())
                .status(RefundStatus.REQUESTED)
                .build();
    }

    // 포트원 환불 API 호출
    private void callPortOneCancel(Payment payment, String reason) throws Exception {
        CancelData cancelData = new CancelData(
                payment.getPortonePaymentId(),
                true,
                BigDecimal.valueOf(payment.getAmount())
        );
        cancelData.setReason(reason);
        iamportClient.cancelPaymentByImpUid(cancelData);
    }

    // 환불 성공
    private void processSuccess(User user, Payment payment, Refund refund, String ip, String ua) {
        payment.cancel();
        refund.complete();
        user.updateMembership(null, null);
        userHistoryService.saveLog(user.getEmail(), UserAction.REFUND_SUCCESS, ip, ua, true, "환불 처리 완료");
    }

    // 환불 실패
    private void processFailure(User user, Refund refund, String ip, String ua, String errorMsg) {
        refund.fail();
        userHistoryService.saveLog(user.getEmail(), UserAction.REFUND_FAIL, ip, ua, false, errorMsg);
    }
}