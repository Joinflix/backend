package com.sesac.joinflex.domain.refund.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefundResponse {
    private final Long refundId;
    private final Long paymentId;
    private final String status;
    private final String message;
}