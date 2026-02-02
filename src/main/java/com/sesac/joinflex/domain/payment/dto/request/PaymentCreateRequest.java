package com.sesac.joinflex.domain.payment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCreateRequest {

    @JsonProperty("imp_uid")
    private String impUid; // PortOne(아임포트)에서 발급하는 결제 고유 ID

    @JsonProperty("merchant_uid")
    private String merchantUid; // 가맹점(우리 시스템)에서 생성/관리하는 주문 번호

    @JsonProperty("pay_method")
    private String payMethod; // 결제 수단 (예: card, trans 등)

    @JsonProperty("paid_amount")
    private Integer paidAmount; // 실제 결제된 금액

    @NotNull(message = "membershipId 필수입니다.")
    private Long membershipId; // 구매한 멤버십 ID

    private String status; // 결제 상태 (예: paid, ready, failed 등)
}
