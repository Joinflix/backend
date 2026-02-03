package com.sesac.joinflex.domain.refund.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefundRequest {
    @NotNull(message = "결제 ID는 필수입니다.")
    private Long paymentId;
    private String reason;
}
