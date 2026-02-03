package com.sesac.joinflex.domain.refund.repository;

import com.sesac.joinflex.domain.payment.entity.Payment;
import com.sesac.joinflex.domain.refund.entity.Refund;
import com.sesac.joinflex.domain.refund.entity.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    boolean existsByPaymentAndStatus(Payment payment, RefundStatus status);
}
