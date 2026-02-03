package com.sesac.joinflex.domain.payment.dto.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sesac.joinflex.domain.payment.entity.Payment;
import com.sesac.joinflex.domain.payment.entity.PaymentStatus;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class PaymentDetailResponse {
    private final Long id;
    private final String paymentId;
    private final Long orderId;
    private final int amount;
    private final PaymentStatus status;
    private final String pgProvider;
    private final Map<String, Object> portoneData;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static PaymentDetailResponse from(Payment payment, ObjectMapper objectMapper) {
        Map<String, Object> parsedPortoneData = null;
        String pgProvider = null;
        try {
            if (payment.getPortoneData() != null && !payment.getPortoneData().isEmpty()) {
                parsedPortoneData = objectMapper.readValue(payment.getPortoneData(), new TypeReference<Map<String, Object>>() {});
                pgProvider = (String) parsedPortoneData.get("pg_provider");
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.JSON_SERIALIZATION_FAILED);
        }

        return PaymentDetailResponse.builder()
                .id(payment.getId())
                .paymentId(payment.getPortonePaymentId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .pgProvider(pgProvider)
                .portoneData(parsedPortoneData)
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
