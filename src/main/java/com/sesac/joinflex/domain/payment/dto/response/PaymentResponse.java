package com.sesac.joinflex.domain.payment.dto.response;

import com.sesac.joinflex.domain.payment.entity.Payment;
import com.sesac.joinflex.domain.payment.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentResponse {
    private final Long paymentId;
    private final Long membershipId;
    private final String membershipName;
    private final Long userId;
    private final int amount;
    private final PaymentStatus status;
    private final LocalDateTime paidAt;

    @Builder
    private PaymentResponse(Long paymentId, Long membershipId, String membershipName , Long userId, int amount, PaymentStatus status, LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.membershipId = membershipId;
        this.membershipName = membershipName;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.paidAt = paidAt;
    }

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .membershipId(payment.getMembership().getId())
                .membershipName(payment.getMembership().getDisplayName())
                .userId(payment.getUser().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paidAt(payment.getCreatedAt())
                .build();
    }
}
