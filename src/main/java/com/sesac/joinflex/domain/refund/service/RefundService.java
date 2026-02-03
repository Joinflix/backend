package com.sesac.joinflex.domain.refund.service;

import com.sesac.joinflex.domain.payment.entity.Payment;
import com.sesac.joinflex.domain.payment.entity.PaymentStatus;
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
        // 1. 주문 및 사용자 조회
        User user = userService.findById(userId);
        Payment payment = paymentService.findById(request.getPaymentId());

        // 2. 주문/결제 상태 및 중복 환불 요청 검증
        if(payment.getStatus() == PaymentStatus.REFUND) {
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_CANCELED);
        }

        if (refundRepository.existsByPaymentAndStatus(payment, RefundStatus.REQUESTED) ||
            refundRepository.existsByPaymentAndStatus(payment, RefundStatus.COMPLETED)) {
            throw new CustomException(ErrorCode.REFUND_ALREADY_PROCESSED);
        }

        // 3. 환불 엔티티 생성 및 'REQUESTED' 상태로 저장
        Refund refund = Refund.builder()
                .payment(payment)
                .reason(request.getReason())
                .amount(payment.getAmount())
                .status(RefundStatus.REQUESTED)
                .build();
        refundRepository.save(refund);

        // 4. 외부 API 호출 및 결과에 따른 상태 업데이트
        try {
            // 포트원 환불 처리
            String impUid = payment.getPortonePaymentId();
            BigDecimal refundAmount = new BigDecimal(payment.getAmount());
            CancelData cancelData = new CancelData(impUid, true, refundAmount);
            cancelData.setReason(request.getReason());

            iamportClient.cancelPaymentByImpUid(cancelData);

            // 성공 시 DB 상태 변경
            payment.cancel();
            refund.complete();
            user.updateMembership(null, null); // 멤버십 정보 초기화

            userHistoryService.saveLog(user.getEmail(), UserAction.REFUND_SUCCESS , ip, ua, true, "환불 처리 완료");

            return RefundResponse.builder()
                    .refundId(refund.getId())
                    .paymentId(payment.getId())
                    .status(refund.getStatus().name())
                    .message("환불 요청이 성공적으로 처리되었습니다.")
                    .build();

        } catch (IamportResponseException e) {
            // 외부 API 실패
            refund.fail();
            userHistoryService.saveLog(user.getEmail(), UserAction.REFUND_FAIL , ip, ua, false, "환불 처리 실패");
            throw new CustomException(ErrorCode.REFUND_FAILED);
        } catch (IOException e) {
            // 통신 실패
            refund.fail();
            userHistoryService.saveLog(user.getEmail(), UserAction.REFUND_FAIL , ip, ua, false, "환불 처리 실패");
            throw new CustomException(ErrorCode.REFUND_FAILED);
        } catch (Exception e) {
            // 기타 예외 발생
            userHistoryService.saveLog(user.getEmail(), UserAction.REFUND_FAIL , ip, ua, false, "환불 처리 실패");
            refund.fail();
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }
}