package com.sesac.joinflex.domain.payment.entity;

public enum PaymentStatus {
    COMPLETED, // 결제 완료
    FAILED,    // 결제 실패
    READY,     // 결제 대기 (가상계좌 발급 등)
    REFUND;     //환불
    /**
     * PortOne에서 받은 문자열 상태를 내부 PaymentStatus Enum으로 변환합니다.
     * @param status PortOne의 결제 상태 문자열 (예: "paid", "ready", "failed")
     * @return 내부 시스템의 PaymentStatus
     */
    public static PaymentStatus fromStatusString(String status) {
        if (status == null) {
            return FAILED;
        }

        switch (status) {
            case "paid":
                return COMPLETED;
            case "ready":
                return READY;
            case "refund":
                return REFUND;
            // "failed"를 포함한 나머지 모든 경우를 FAILED로 처리
            default:
                return FAILED;
        }
    }
}
