package com.sesac.joinflex.domain.refund.repository;

import com.sesac.joinflex.domain.payment.entity.Payment;
import com.sesac.joinflex.domain.refund.entity.Refund;
import com.sesac.joinflex.domain.refund.entity.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    // 쿼리 한 번으로 REQUESTED, COMPLETED 상태를 모두 체크
    Boolean existsByPaymentAndStatusIn(Payment payment, List<RefundStatus> statuses);
}
