package com.sesac.joinflex.domain.payment.repository;

import com.sesac.joinflex.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByPortonePaymentId(String impUid);
    Optional<Payment> findTopByUserIdOrderByCreatedAtDesc(Long userId);// 가장 최근 결제 내역 조회 (멤버십 상태 확인용)
    List<Payment> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
