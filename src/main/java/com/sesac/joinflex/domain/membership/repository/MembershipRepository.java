package com.sesac.joinflex.domain.membership.repository;

import com.sesac.joinflex.domain.membership.entity.Membership;
import com.sesac.joinflex.domain.membership.entity.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership,Long> {
    boolean existsByType(MembershipType type);
    Optional<Membership> findByType(MembershipType type);
}
