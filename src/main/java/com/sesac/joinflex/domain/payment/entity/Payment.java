package com.sesac.joinflex.domain.payment.entity;

import com.sesac.joinflex.domain.membership.entity.Membership;
import com.sesac.joinflex.domain.refund.entity.Refund;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.global.common.entity.BaseEntity;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(nullable = false, unique = true)
    private String portonePaymentId; // PortOne의 imp_uid

    @Column(nullable = false)
    private int amount; // 결제 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status; // 결제 상태

    @Lob
    @Column(columnDefinition = "TEXT")
    private String portoneData; // 포트원 결제 데이터 (JSON)

    @ManyToOne(fetch = FetchType.LAZY) // 한 사용자는 여러 번 결제 가능
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // 어떤 멤버십을 결제했는지 기록
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<Refund> refunds = new ArrayList<>(); // 이 결제와 관련된 환불 목록

    @Builder
    private Payment(String portonePaymentId, int amount, PaymentStatus status, String portoneData, User user, Membership membership) {
        this.user = user;
        this.membership = membership;
        this.portonePaymentId = portonePaymentId;
        this.amount = amount;
        this.status = status;
        this.portoneData = portoneData;
    }

    // 환불시 주문 상태 변경
    public void cancel() {
        this.status = PaymentStatus.REFUND;
    }

    // 결제 소유자 검증
    public void validateOwnership(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_PAYMENT_OWNER);
        }
    }

    // 환불 가능 여부 검증
    public void validateRefundable() {
        if (this.status == PaymentStatus.REFUND) {
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_CANCELED);
        } else if (this.status != PaymentStatus.COMPLETED) {
            throw new CustomException(ErrorCode.PAYMENT_NOT_COMPLETED);
        }
    }

}
